/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.json.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Map;

import org.jclouds.json.internal.NamingStrategies.ConstructorFieldNamingStrategy;

import com.google.common.collect.Maps;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Creates type adapters for types handled in the following ways:
 * <p/>
 * <ul>
 * <li>Deserialization</li>
 * If there's an annotation designating a parameterized constructor, invoke that for fields
 * correlating to named parameter annotations. Otherwise, use {@link ConstructorConstructor}, and
 * set fields via reflection.
 * <p/>
 * Notes: primitive constructor params are set to the Java defaults (0 or false) if not present; and
 *  the empty object ({}) is treated as a null if the constructor for the object throws an NPE.
 * <li>Serialization</li>
 * Serialize based on reflective access to fields, delegating to ReflectiveTypeAdaptor.
 * </ul>
 * <h3>Example: Using javax inject to select a constructor and corresponding named parameters</h3>
 * <p/>
 * <pre>
 *    
 * import NamingStrategies.*;
 *
 * serializationStrategy = new AnnotationOrNameFieldNamingStrategy(
 *    new ExtractSerializedName(), new ExtractNamed());
 * 
 * deserializationStrategy = new AnnotationConstructorNamingStrategy(
 *    ImmutableSet.of(javax.inject.Inject.class),
 *    ImmutableSet.of(new ExtractNamed()));
 *    
 * factory = new DeserializationConstructorAndReflectiveTypeAdapterFactory(new ConstructorConstructor(),
 *      serializationStrategy, Excluder.DEFAULT, deserializationStrategy);
 *
 * gson = new GsonBuilder(serializationStrategy).registerTypeAdapterFactory(factory).create();
 *
 * </pre>
 * <p/>
 * The above would work fine on the following class, which has no gson-specific annotations:
 * <p/>
 * <pre>
 * private static class ImmutableAndVerifiedInCtor {
 *    final int foo;
 *    &#064;Named(&quot;_bar&quot;)
 *    final int bar;
 *
 *    &#064;Inject
 *    ImmutableAndVerifiedInCtor(@Named(&quot;foo&quot;) int foo, @Named(&quot;_bar&quot;) int bar) {
 *       if (foo &lt; 0)
 *          throw new IllegalArgumentException(&quot;negative!&quot;);
 *       this.foo = foo;
 *       this.bar = bar;
 *    }
 * }
 * </pre>
 * <p/>
 * <br/>
 *
 * @author Adrian Cole
 * @author Adam Lowe
 */
public final class DeserializationConstructorAndReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorFieldNamingStrategy constructorFieldNamingPolicy;
   private final ReflectiveTypeAdapterFactory delegateFactory;

   /**
    * @param constructorConstructor         passed through to delegate ReflectiveTypeAdapterFactory for serialization
    * @param serializationFieldNamingPolicy passed through to delegate ReflectiveTypeAdapterFactory for serialization
    * @param excluder                       passed through to delegate ReflectiveTypeAdapterFactory for serialization
    * @param deserializationFieldNamingPolicy
    *                                       determines which constructor to use and how to determine field names for
    *                                       deserialization
    * @see ReflectiveTypeAdapterFactory
    */
   public DeserializationConstructorAndReflectiveTypeAdapterFactory(
         ConstructorConstructor constructorConstructor,
         FieldNamingStrategy serializationFieldNamingPolicy,
         Excluder excluder,
         ConstructorFieldNamingStrategy deserializationFieldNamingPolicy) {
      this.constructorFieldNamingPolicy = checkNotNull(deserializationFieldNamingPolicy, "deserializationFieldNamingPolicy");
      this.delegateFactory = new ReflectiveTypeAdapterFactory(constructorConstructor, checkNotNull(serializationFieldNamingPolicy, "fieldNamingPolicy"), checkNotNull(excluder, "excluder"));
   }

   @SuppressWarnings("unchecked")
   public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
      Class<? super T> raw = type.getRawType();
      Constructor<? super T> deserializationCtor = constructorFieldNamingPolicy.getDeserializationConstructor(raw);

      if (deserializationCtor == null) {
         return null; // allow GSON to choose the correct Adapter (can't simply return delegateFactory.create())
      } else {
         deserializationCtor.setAccessible(true);
         return new DeserializeWithParameterizedConstructorSerializeWithDelegate<T>(delegateFactory.create(gson, type), deserializationCtor,
               getParameterReaders(gson, type, deserializationCtor));
      }
   }

   private final class DeserializeWithParameterizedConstructorSerializeWithDelegate<T> extends TypeAdapter<T> {
      private final Constructor<? super T> parameterizedCtor;
      private final Map<String, ParameterReader> parameterReaders;
      private final TypeAdapter<T> delegate;

      private DeserializeWithParameterizedConstructorSerializeWithDelegate(TypeAdapter<T> delegate,
                                                                           Constructor<? super T> parameterizedCtor, Map<String, ParameterReader> parameterReaders) {
         this.delegate = delegate;
         this.parameterizedCtor = parameterizedCtor;
         this.parameterReaders = parameterReaders;
      }

      @Override
      public T read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         }

         Class<?>[] paramTypes = parameterizedCtor.getParameterTypes();
         Object[] ctorParams = new Object[paramTypes.length];
         boolean empty = true;

         // Set all primitive constructor params to defaults
         for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] == boolean.class) {
               ctorParams[i] = Boolean.FALSE;
            } else if (paramTypes[i].isPrimitive()) {
               ctorParams[i] = 0;
            }
         }

         try {
            in.beginObject();
            while (in.hasNext()) {
               empty = false;
               String name = in.nextName();
               ParameterReader parameter = parameterReaders.get(name);
               if (parameter == null) {
                  in.skipValue();
               } else {
                  Object value = parameter.read(in);
                  if (value != null) ctorParams[parameter.index] = value;
               }
            }
         } catch (IllegalStateException e) {
            throw new JsonSyntaxException(e);
         }

         for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isPrimitive()) {
               checkArgument(ctorParams[i] != null, "Primitive param[" + i + "] in constructor " + parameterizedCtor
                     + " cannot be absent!");
            }
         }
         in.endObject();

         try {
            return newInstance(ctorParams);
         } catch (NullPointerException ex) {
            // If {} was found and constructor threw NPE, we treat the field as null
            if (empty && paramTypes.length > 0) {
               return null;
            }
            throw ex;
         }
      }

      /**
       * pass to delegate
       */
      @Override
      public void write(JsonWriter out, T value) throws IOException {
         delegate.write(out, value);
      }

      @SuppressWarnings("unchecked")
      private T newInstance(Object[] ctorParams) throws AssertionError {
         try {
            return (T) parameterizedCtor.newInstance(ctorParams);
         } catch (InstantiationException e) {
            throw new AssertionError(e);
         } catch (IllegalAccessException e) {
            throw new AssertionError(e);
         } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException)
               throw RuntimeException.class.cast(e.getCause());
            throw new AssertionError(e);
         }
      }
   }

   // logic borrowed from ReflectiveTypeAdapterFactory
   static class ParameterReader<T> {
      final String name;
      final int index;
      final TypeAdapter<T> typeAdapter;

      ParameterReader(String name, int index, TypeAdapter<T> typeAdapter) {
         this.name = name;
         this.index = index;
         this.typeAdapter = typeAdapter;
      }

      public Object read(JsonReader reader) throws IOException {
         return typeAdapter.read(reader);
      }
   }

   @SuppressWarnings("unchecked")
   private Map<String, ParameterReader> getParameterReaders(Gson context, TypeToken<?> declaring, Constructor<?> constructor) {
      Map<String, ParameterReader> result = Maps.newLinkedHashMap();

      for (int index = 0; index < constructor.getGenericParameterTypes().length; index++) {
         Type parameterType = getTypeOfConstructorParameter(declaring, constructor, index);
         TypeAdapter<?> adapter = context.getAdapter(TypeToken.get(parameterType));
         String parameterName = constructorFieldNamingPolicy.translateName(constructor, index);
         checkArgument(parameterName != null, constructor + " parameter " + 0 + " failed to be named by " + constructorFieldNamingPolicy);
         ParameterReader parameterReader = new ParameterReader(parameterName, index, adapter);
         ParameterReader previous = result.put(parameterReader.name, parameterReader);
         checkArgument(previous == null, constructor + " declares multiple JSON parameters named " + parameterReader.name);
      }

      return result;
   }

   private Type getTypeOfConstructorParameter(TypeToken<?> declaring, Constructor<?> constructor, int index) {
      Type genericParameter = constructor.getGenericParameterTypes()[index];
      return $Gson$Types.resolve(declaring.getType(), declaring.getRawType(), genericParameter);
   }
}
