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
package org.jclouds.reflect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Type;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;

/**
 * Invokable utilities
 * 
 * @since 1.6
 */
@Beta
public final class Reflection2 {
   private static final LoadingCache<Class<?>, TypeToken<?>> typeTokenForClass = CacheBuilder.newBuilder().build(
         new CacheLoader<Class<?>, TypeToken<?>>() {
            public TypeToken<?> load(final Class<?> key) throws Exception {
               return TypeToken.of(key);
            }
         });

   /**
    * Cache of type tokens for the supplied class.
    */
   public static Function<Class<?>, TypeToken<?>> typeTokenForClass() {
      return typeTokenForClass;
   }

   /**
    * Cache of type tokens for the supplied class.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeTokenOf(Class<T> in) {
      return (TypeToken<T>) typeTokenForClass.apply(checkNotNull(in, "class"));
   }

   private static final LoadingCache<Type, TypeToken<?>> typeTokenForType = CacheBuilder.newBuilder().build(
         new CacheLoader<Type, TypeToken<?>>() {
            public TypeToken<?> load(final Type key) throws Exception {
               return TypeToken.of(key);
            }
         });

   /**
    * Cache of type tokens for the supplied class.
    */
   public static Function<Type, TypeToken<?>> typeTokenForType() {
      return typeTokenForType;
   }

   /**
    * Cache of type tokens for the supplied type.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeTokenOf(Type in) {
      return (TypeToken<T>) typeTokenForType.apply(checkNotNull(in, "class"));
   }

}