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
import static com.google.common.collect.Iterables.toArray;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

/**
 * Utilities that allow access to {@link Invokable}s with {@link Invokable#getOwnerType() owner types}.
 * 
 * @since 1.6
 */
@Beta
public final class Reflection2 {

   /**
    * gets a {@link TypeToken} for the given class.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeToken(Class<T> in) {
      return (TypeToken<T>) typeTokenForClass.apply(checkNotNull(in, "class"));
   }

   /**
    * returns an {@link Invokable} object that links the {@code method} to its owner.
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    * @param method
    *           present in {@code ownerType}
    */
   @SuppressWarnings("unchecked")
   public static <T, R> Invokable<T, R> method(TypeToken<T> ownerType, Method method) {
      return (Invokable<T, R>) methods.apply(new TypeTokenAndMethod(ownerType, method));
   }

   /**
    * returns an {@link Invokable} object that reflects a method present in the {@link TypeToken} type.
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    * @param parameterTypes
    *           corresponds to {@link Method#getParameterTypes()}
    * 
    * @throws IllegalArgumentException
    *            if the method doesn't exist or a security exception occurred
    */
   @SuppressWarnings("unchecked")
   public static <T, R> Invokable<T, R> method(Class<T> ownerType, String name, Class<?>... parameterTypes) {
      return (Invokable<T, R>) methodForArgs.apply(new TypeTokenNameAndParameterTypes(typeToken(ownerType), name,
            parameterTypes));
   }

   /**
    * return all methods present in the class as {@link Invokable}s.
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    */
   @SuppressWarnings("unchecked")
   public static <T> Collection<Invokable<T, Object>> methods(Class<T> ownerType) {
      return Collection.class.cast(methodsForTypeToken.apply(typeToken(ownerType)).values());
   }

   private static final LoadingCache<TypeTokenAndMethod, Invokable<?, ?>> methods = CacheBuilder.newBuilder().build(
         new CacheLoader<TypeTokenAndMethod, Invokable<?, ?>>() {
            public Invokable<?, ?> load(TypeTokenAndMethod key) {
               return key.type.method(key.method);
            }
         });

   private static class TypeTokenAndMethod {

      protected final TypeToken<?> type;
      protected final Method method;

      public TypeTokenAndMethod(TypeToken<?> type, Method method) {
         this.type = checkNotNull(type, "type");
         this.method = checkNotNull(method, "method");
      }

      public int hashCode() {
         return Objects.hashCode(type, method);
      }

      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         TypeTokenAndMethod that = TypeTokenAndMethod.class.cast(obj);
         return Objects.equal(this.type, that.type) && Objects.equal(this.method, that.method);
      }
   }

   private static final LoadingCache<Class<?>, TypeToken<?>> typeTokenForClass = CacheBuilder.newBuilder().build(
         new CacheLoader<Class<?>, TypeToken<?>>() {
            public TypeToken<?> load(final Class<?> key) {
               return TypeToken.of(key);
            }
         });

   private static class TypeTokenAndParameterTypes {

      protected final TypeToken<?> type;
      protected final List<Class<?>> parameterTypes;

      public TypeTokenAndParameterTypes(TypeToken<?> type, Class<?>... parameterTypes) {
         this.type = checkNotNull(type, "type");
         this.parameterTypes = Arrays.asList(checkNotNull(parameterTypes, "parameterTypes"));
      }

      public int hashCode() {
         return Objects.hashCode(type, parameterTypes);
      }

      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         TypeTokenAndParameterTypes that = TypeTokenAndParameterTypes.class.cast(obj);
         return Objects.equal(this.type, that.type) && Objects.equal(this.parameterTypes, that.parameterTypes);
      }

      public String toString() {
         return Objects.toStringHelper("").add("type", type).add("parameterTypes", parameterTypes).toString();
      }
   }

   private static final LoadingCache<TypeTokenNameAndParameterTypes, Invokable<?, ?>> methodForArgs = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeTokenNameAndParameterTypes, Invokable<?, ?>>() {
            public Invokable<?, ?> load(final TypeTokenNameAndParameterTypes key) {
               try {
                  Method method = key.type.getRawType().getMethod(key.name, toArray(key.parameterTypes, Class.class));
                  return methods.apply(new TypeTokenAndMethod(key.type, method));
               } catch (SecurityException e) {
                  throw new IllegalArgumentException(e.getMessage() + " getting method " + key.toString(), e);
               } catch (NoSuchMethodException e) {
                  throw new IllegalArgumentException("no such method " + key.toString(), e);
               }
            }
         });

   private static class TypeTokenNameAndParameterTypes extends TypeTokenAndParameterTypes {

      private final String name;

      public TypeTokenNameAndParameterTypes(TypeToken<?> type, String name, Class<?>... parameterTypes) {
         super(type, parameterTypes);
         this.name = checkNotNull(name, "name");
      }

      public int hashCode() {
         return Objects.hashCode(super.hashCode(), name);
      }

      public boolean equals(Object obj) {
         if (super.equals(obj)) {
            TypeTokenNameAndParameterTypes that = TypeTokenNameAndParameterTypes.class.cast(obj);
            return name.equals(that.name);
         }
         return false;
      }

      public String toString() {
         return Objects.toStringHelper("").add("type", type).add("name", name).add("parameterTypes", parameterTypes)
               .toString();
      }
   }

   private static final LoadingCache<TypeToken<?>, Map<Method, Invokable<?, ?>>> methodsForTypeToken = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeToken<?>, Map<Method, Invokable<?, ?>>>() {
            public Map<Method, Invokable<?, ?>> load(final TypeToken<?> key) {
               Builder<Method, Invokable<?, ?>> builder = ImmutableMap.<Method, Invokable<?, ?>> builder();
               for (Method method : key.getRawType().getMethods())
                  builder.put(method, method(key, method));
               return builder.build();
            }
         });

}