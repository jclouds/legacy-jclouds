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
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.internal.ManagedTypeModel;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import static org.jclouds.reflect.Reflection2.isAssignable;

/**
 * A singleton function that is used to map java {@link Type} to mbean {@link OpenType}.
 * The method will map native types to the corresponding {@link OpenType}s.
 * Classes that are annotated with {@link org.jclouds.management.annotations.ManagedType} will be mapped to ComplexTypes.
 * Collections, will be mapped to ArrayType or TabularType for native and managed collection types respectively.
 * Optional types will be mapped to the generic type.
 *
 */
public enum ToOpenType implements Function<Type, OpenType> {

   FUNCTION;

   private static final LoadingCache<Type, OpenType> OPEN_TYPE_MAPPING = CacheBuilder.newBuilder().build(new CacheLoader<Type, OpenType>() {
      @Override
      public OpenType load(Type key) throws Exception {

         if (key instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) key;
            Type generic = p.getActualTypeArguments()[0];
            Type raw = p.getRawType();
            OpenType genericOpenType = ToOpenType.FUNCTION.apply(generic);

            //1. Collections of primitives should be mapped to ArrayType.
            //2. Collections of SimpleType should be mapped to ArrayType.
            //3. Collections of CompositeType should be mapped to TabularType.
            //4. All other generics should map the generic type.

            if (isAssignable(Collection.class, raw) && TypeToken.of(generic).getRawType().isPrimitive()) {
               return new ArrayType((SimpleType<?>) genericOpenType, true);
            } else if (isAssignable(Collection.class, raw) && isAssignable(SimpleType.class, genericOpenType.getClass())) {
               return new ArrayType((SimpleType<?>) genericOpenType, false);
            } else if (isAssignable(Collection.class, raw) && isAssignable(CompositeType.class, genericOpenType.getClass())) {
               CompositeType compositeType = (CompositeType) genericOpenType;
               return new TabularType(compositeType.getTypeName(), compositeType.getDescription(), compositeType, compositeType.keySet().toArray(new String[compositeType.keySet().size()]));
            } else {
               return genericOpenType;
            }
         } else if (key instanceof Class) {
            Class clazz = (Class) key;
            return ManagedTypeModel.of(clazz).getCompositeType();
         }
         return null;
      }
   });

   //Pre-loading of the standard mapping.
   static {
      OPEN_TYPE_MAPPING.put(boolean.class, SimpleType.BOOLEAN);
      OPEN_TYPE_MAPPING.put(Boolean.class, SimpleType.BOOLEAN);
      OPEN_TYPE_MAPPING.put(byte.class, SimpleType.BYTE);
      OPEN_TYPE_MAPPING.put(Byte.class, SimpleType.BYTE);
      OPEN_TYPE_MAPPING.put(char.class, SimpleType.CHARACTER);
      OPEN_TYPE_MAPPING.put(Character.class, SimpleType.CHARACTER);
      OPEN_TYPE_MAPPING.put(short.class, SimpleType.SHORT);
      OPEN_TYPE_MAPPING.put(Short.class, SimpleType.SHORT);
      OPEN_TYPE_MAPPING.put(int.class, SimpleType.INTEGER);
      OPEN_TYPE_MAPPING.put(Integer.class, SimpleType.INTEGER);
      OPEN_TYPE_MAPPING.put(long.class, SimpleType.LONG);
      OPEN_TYPE_MAPPING.put(Long.class, SimpleType.LONG);
      OPEN_TYPE_MAPPING.put(double.class, SimpleType.DOUBLE);
      OPEN_TYPE_MAPPING.put(Double.class, SimpleType.DOUBLE);
      OPEN_TYPE_MAPPING.put(float.class, SimpleType.FLOAT);
      OPEN_TYPE_MAPPING.put(Float.class, SimpleType.FLOAT);
      OPEN_TYPE_MAPPING.put(BigDecimal.class, SimpleType.BIGDECIMAL);
      OPEN_TYPE_MAPPING.put(BigInteger.class, SimpleType.BIGINTEGER);
      OPEN_TYPE_MAPPING.put(Date.class, SimpleType.DATE);
      OPEN_TYPE_MAPPING.put(String.class, SimpleType.STRING);
      //Indirect mappings
      //Classes that have no mapping available, but we can live with their String representation.
      OPEN_TYPE_MAPPING.put(URI.class, SimpleType.STRING);
      OPEN_TYPE_MAPPING.put(URL.class, SimpleType.STRING);
   }


   /**
    * Converts a {@link Type} to its corresponding {@link OpenType}.
    *
    * @throws IllegalArgumentException if type contains cyclic references or isn't applicable for any other reason.
    */
   @Override
   public OpenType apply(@Nullable Type type) {
      OpenType result = null;
      try {
         result = OPEN_TYPE_MAPPING.get(type);
      } catch (Exception e) {
         //We are mostly catching types with cyclic references, which are illegal.
         throw new IllegalArgumentException("Failed to map " + type + " to OpenType.", e);
      }

      if (result == null) {
         throw new IllegalArgumentException("Type " + type + " not mappable to OpenType.");
      } else {
         return result;
      }
   }
}
