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

package org.jclouds.management.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.internal.ManagedTypeModel;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import static org.jclouds.reflect.Reflection2.getPropertyValue;
import static org.jclouds.reflect.Reflection2.isAssignable;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.typeParameterOf;

/**
 * A {@link Function} that converts an Object to {@link CompositeData}.
 * @param <T>
 */
public final class ToCompositeData<T> implements Function<T, CompositeData> {

   private final Class<T> type;
   private final ManagedTypeModel<T> model;
   private static final LoadingCache<Class<?>, ToCompositeData<?>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, ToCompositeData<?>>() {

      @Override
      public ToCompositeData<?> load(Class<?> key) throws Exception {
         return new ToCompositeData(key);
      }
   });

   /**
    * Constructor.
    * Only available to cache.
    * @param type
    */
   private ToCompositeData(Class<T> type) {
      this.type = type;
      this.model = ManagedTypeModel.of(type);
   }

   /**
    * Factory method.
    * @param type
    * @param <T>
    * @return
    */
   public static <T> ToCompositeData<T> from(Class<T> type) {
      try {
         return (ToCompositeData<T>) CACHE.get(type);
      } catch (ExecutionException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Conversion Function.
    * @param input
    * @return
    */
   @Override
   public CompositeData apply(@Nullable T input) {
      int index = 0;
      int size = model.getNames().size();
      String[] attributeNames = model.getNames().toArray(new String[size]);
      Object[] attributeValues = new Object[size];

      for (String attributeName : attributeNames) {
         TypeToken raw = model.getTypeToken(attributeName);
         OpenType openType = model.getOpenType(attributeName);
         try {
            attributeValues[index] = convertValue(getValue(input, attributeName), raw, openType);
         } catch (Exception ex) {
            attributeValues[index] = null;
         }
         index++;
      }

      try {
         return new CompositeDataSupport(model.getCompositeType(), attributeNames, attributeValues);
      } catch (OpenDataException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Converts a value to the appropriate type.
    *
    * @param value
    * @param raw
    * @param openType
    * @return
    */
   private Object convertValue(Object value, TypeToken raw, OpenType openType) throws OpenDataException {
      if (value == null) {
         return null;
      } else if (isAssignable(Optional.class, value.getClass())) {
         //Unwarp Optional type.
         return convertOptional((Optional) value, raw, openType);
      } else if (isAssignable(Iterable.class, value.getClass()) && isAssignable(ArrayType.class, openType.getClass())) {
         //Convert to array.
         return convertToArray((Iterable) value, raw, (ArrayType) openType);
      } else if (isAssignable(Iterable.class, value.getClass()) && isAssignable(TabularType.class, openType.getClass())) {
         //Convert to tabular
         return convertToTabular((Iterable) value, raw, (TabularType) openType);
      } else if (isAssignable(CompositeType.class, openType.getClass())) {
         //Convert to complex type.
         return convertToComposite(value, raw, (CompositeType) openType);
         //Get String value of.
      } else if (SimpleType.STRING.equals(openType) && !String.class.isAssignableFrom(value.getClass())) {
         return String.valueOf(value);
      }
      return value;
   }

   /**
    * Converts an object to the matching composite type.
    *
    * @param object
    * @param raw
    * @param openType
    * @return
    */
   private CompositeData convertToComposite(Object object, TypeToken raw, CompositeType openType) throws OpenDataException {
      return ToCompositeData.from(raw.getRawType()).apply(object);
   }

   /**
    * Converts an object to the matching composite type.
    *
    * @param iterable
    * @param raw
    * @param openType
    * @return
    */
   private TabularData convertToTabular(Iterable iterable, TypeToken raw, TabularType openType) throws OpenDataException {
      return ToTabularData.from(raw.getRawType()).apply(iterable);
   }

   /**
    * Converts an Itearble to an Object array.
    *
    * @param iterable
    * @param raw
    * @param openType
    * @return
    */
   private Object[] convertToArray(Iterable iterable, TypeToken raw, ArrayType openType) {
      return Iterables.toArray(iterable, typeParameterOf(raw.getType()));
   }


   /**
    * Converts an {@link Optional} object to the matching composite type.
    *
    * @param optional
    * @param raw
    * @param openType
    * @return
    */
   private Object convertOptional(Optional optional, TypeToken raw, OpenType openType) throws OpenDataException {
      return convertValue(optional.orNull(), raw, openType);
   }

   /**
    * Extracts the attribute value from the target object.
    * The attribute is retrieved from a matching getter, filed or method.
    *
    * @param target
    * @param attribute
    * @return
    * @throws java.lang.reflect.InvocationTargetException
    *
    * @throws IllegalAccessException
    * @throws NoSuchMethodException
    */
   private Object getValue(Object target, String attribute) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      try {
         return getPropertyValue(target, attribute);
      } catch (Exception ex) {
         Invokable invokable = method(target.getClass(), attribute);
         if (invokable != null) {
            return invokable.invoke(target);
         } else {
            throw new NoSuchMethodException("No fields, getter or methods found for property " + attribute + " on " + target.getClass());
         }
      }
   }
}
