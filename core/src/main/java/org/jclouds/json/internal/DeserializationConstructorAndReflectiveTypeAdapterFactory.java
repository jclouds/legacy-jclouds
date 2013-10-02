/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.json.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.jclouds.json.internal.NamingStrategies.AnnotationConstructorNamingStrategy;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
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
 * If there's an annotation designating a parameterized constructor, invoke that for fields correlating to named
 * parameter annotations. Otherwise, use {@link ConstructorConstructor}, and set fields via reflection.
 * <p/>
 * Notes: primitive constructor params are set to the Java defaults (0 or false) if not present; and the empty object
 * ({}) is treated as a null if the constructor for the object throws an NPE.
 * <li>Serialization</li> Serialize based on reflective access to fields, delegating to ReflectiveTypeAdaptor.
 * </ul>
 * <h3>Example: Using javax inject to select a constructor and corresponding named parameters</h3>
 * <p/>
 * 
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
 * 
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
   private final AnnotationConstructorNamingStrategy constructorFieldNamingPolicy;
   private final ReflectiveTypeAdapterFactory delegateFactory;

   /**
    * @see ReflectiveTypeAdapterFactory
    */
   public DeserializationConstructorAndReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor,
         FieldNamingStrategy serializationFieldNamingPolicy, Excluder excluder,
         AnnotationConstructorNamingStrategy deserializationFieldNamingPolicy) {
      this.constructorFieldNamingPolicy = checkNotNull(deserializationFieldNamingPolicy,
            "deserializationFieldNamingPolicy");
      this.delegateFactory = new ReflectiveTypeAdapterFactory(constructorConstructor, checkNotNull(
            serializationFieldNamingPolicy, "fieldNamingPolicy"), checkNotNull(excluder, "excluder"));
   }

   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      com.google.common.reflect.TypeToken<T> token = typeToken(type.getType());
      Invokable<T, T> deserializationCtor = constructorFieldNamingPolicy.getDeserializer(token);

      if (deserializationCtor == null) {
         return null; // allow GSON to choose the correct Adapter (can't simply return delegateFactory.create())
      } else {
         return new DeserializeIntoParameterizedConstructor<T>(delegateFactory.create(gson, type), deserializationCtor,
               getParameterReaders(gson, deserializationCtor));
      }
   }

   private final class DeserializeIntoParameterizedConstructor<T> extends TypeAdapter<T> {
      private final TypeAdapter<T> serializer;
      private final Invokable<T, T> parameterizedCtor;
      private final Map<String, ParameterReader<?>> parameterReaders;

      private DeserializeIntoParameterizedConstructor(TypeAdapter<T> serializer, Invokable<T, T> deserializationCtor,
            Map<String, ParameterReader<?>> parameterReaders) {
         this.serializer = serializer;
         this.parameterizedCtor = deserializationCtor;
         this.parameterReaders = parameterReaders;
      }

      @Override
      public T read(JsonReader in) throws IOException {
         if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
         }

         List<Parameter> params = parameterizedCtor.getParameters();
         Object[] values = new Object[params.size()];
         boolean empty = true;

         // Set all primitive constructor params to defaults
         for (Parameter param : params) {
            if (param.getType().getRawType() == boolean.class) {
               values[param.hashCode()] = Boolean.FALSE;
            } else if (param.getType().getRawType().isPrimitive()) {
               values[param.hashCode()] = 0;
            }
         }

         try {
            in.beginObject();
            while (in.hasNext()) {
               empty = false;
               String name = in.nextName();
               ParameterReader<?> parameter = parameterReaders.get(name);
               if (parameter == null) {
                  in.skipValue();
               } else {
                  Object value = parameter.read(in);
                  if (value != null)
                     values[parameter.position] = value;
               }
            }
         } catch (IllegalStateException e) {
            throw new JsonSyntaxException(e);
         }

         for (Parameter param : params) {
            if (param.getType().getRawType().isPrimitive()) {
               checkArgument(values[param.hashCode()] != null,
                  "Primitive param[%s] in constructor %s cannot be absent!", param.hashCode(), parameterizedCtor);
            } else if (param.getType().getRawType() == Optional.class && values[param.hashCode()] == null) {
               values[param.hashCode()] = Optional.absent();
            }
         }
         in.endObject();

         try {
            return newInstance(values);
         } catch (NullPointerException ex) {
            // If {} was found and constructor threw NPE, we treat the field as null
            if (empty && values.length > 0) {
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
         serializer.write(out, value);
      }

      private T newInstance(Object[] ctorParams) throws AssertionError {
         try {
            return parameterizedCtor.invoke(null, ctorParams);
         } catch (IllegalAccessException e) {
            throw new AssertionError(e);
         } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException)
               throw RuntimeException.class.cast(e.getCause());
            throw new AssertionError(e);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(serializer, parameterizedCtor, parameterReaders);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         DeserializeIntoParameterizedConstructor<?> that = DeserializeIntoParameterizedConstructor.class.cast(obj);
         return Objects.equal(this.serializer, that.serializer)
               && Objects.equal(this.parameterizedCtor, that.parameterizedCtor)
               && Objects.equal(this.parameterReaders, that.parameterReaders);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("parameterizedCtor", parameterizedCtor)
               .add("parameterReaders", parameterReaders).add("serializer", serializer).toString();
      }

   }

   // logic borrowed from ReflectiveTypeAdapterFactory
   static class ParameterReader<T> {
      final String name;
      final int position;
      final TypeAdapter<T> typeAdapter;

      ParameterReader(int position, String name, TypeAdapter<T> typeAdapter) {
         this.name = name;
         this.position = position;
         this.typeAdapter = typeAdapter;
      }

      public Object read(JsonReader reader) throws IOException {
         return typeAdapter.read(reader);
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ParameterReader) {
            ParameterReader<?> that = ParameterReader.class.cast(obj);
            return position == that.position && name.equals(that.name);
         }
         return false;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(position, name);
      }

      @Override
      public String toString() {
         return typeAdapter + " arg" + position;
      }
   }

   private <T> Map<String, ParameterReader<?>> getParameterReaders(Gson context, Invokable<T, T> deserializationCtor) {
      Builder<String, ParameterReader<?>> result = ImmutableMap.builder();
      for (Parameter param : deserializationCtor.getParameters()) {
         TypeAdapter<?> adapter = context.getAdapter(TypeToken.get(param.getType().getType()));
         String parameterName = constructorFieldNamingPolicy.translateName(deserializationCtor, param.hashCode());
         checkArgument(parameterName != null, deserializationCtor + " parameter " + 0 + " failed to be named by "
               + constructorFieldNamingPolicy);
         @SuppressWarnings({ "rawtypes", "unchecked" })
         ParameterReader<?> parameterReader = new ParameterReader(param.hashCode(), parameterName, adapter);
         result.put(parameterReader.name, parameterReader);
      }
      return result.build();
   }
}
