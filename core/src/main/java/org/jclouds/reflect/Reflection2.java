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
package org.jclouds.reflect;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.tryFind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Utilities that allow access to {@link Invokable}s with {@link Invokable#getOwnerType() owner types}.
 * 
 * @since 1.6
 */
@Beta
public class Reflection2 {

   /**
    * gets a {@link TypeToken} for the given type.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeToken(Type in) {
      return (TypeToken<T>) get(typeTokenForType, checkNotNull(in, "class"));
   }

   /**
    * gets a {@link TypeToken} for the given class.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeToken(Class<T> in) {
      return (TypeToken<T>) get(typeTokenForClass, checkNotNull(in, "class"));
   }

   /**
    * returns an {@link Invokable} object that reflects a constructor present in the {@link TypeToken} type.
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    * @param parameterTypes
    *           corresponds to {@link Constructor#getParameterTypes()}
    * 
    * @throws IllegalArgumentException
    *            if the constructor doesn't exist or a security exception occurred
    */
   @SuppressWarnings("unchecked")
   public static <T> Invokable<T, T> constructor(Class<T> ownerType, Class<?>... parameterTypes) {
      return (Invokable<T, T>) get(constructorForParams, new TypeTokenAndParameterTypes(typeToken(ownerType),
            parameterTypes));
   }

   /**
    * return all constructors present in the class as {@link Invokable}s.
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    */
   @SuppressWarnings("unchecked")
   public static <T> Collection<Invokable<T, T>> constructors(TypeToken<T> ownerType) {
      return Collection.class.cast(get(constructorsForTypeToken, ownerType));
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
      return (Invokable<T, R>) method(ownerType.getRawType(), method.getName(), method.getParameterTypes());
   }

   /**
    * returns an {@link Invokable} object that reflects a method present in the {@link TypeToken} type.
    * If there are multiple methods of the same name and parameter list, returns the method in the nearest
    * ancestor with the most specific return type (see {@link Class#getDeclaredMethod}).
    * 
    * @param ownerType
    *           corresponds to {@link Invokable#getOwnerType()}
    * @param name
    *           name of the method to be returned
    * @param parameterTypes
    *           corresponds to {@link Method#getParameterTypes()}
    * 
    * @throws IllegalArgumentException
    *            if the method doesn't exist or a security exception occurred
    */
   @SuppressWarnings("unchecked")
   public static <T, R> Invokable<T, R> method(Class<T> ownerType, String name, Class<?>... parameterTypes) {
      return (Invokable<T, R>) get(methodForParams, new TypeTokenNameAndParameterTypes(typeToken(ownerType), name,
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
      return Collection.class.cast(get(methodsForTypeToken, typeToken(ownerType)));
   }

   /**
    * this gets all declared constructors, not just public ones. makes them accessible, as well.
    */
   private static LoadingCache<TypeToken<?>, Set<Invokable<?, ?>>> constructorsForTypeToken = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeToken<?>, Set<Invokable<?, ?>>>() {
            public Set<Invokable<?, ?>> load(TypeToken<?> key) {
               ImmutableSet.Builder<Invokable<?, ?>> builder = ImmutableSet.<Invokable<?, ?>> builder();
               for (Constructor<?> ctor : key.getRawType().getDeclaredConstructors()) {
                  ctor.setAccessible(true);
                  builder.add(key.constructor(ctor));
               }
               return builder.build();
            }
         });

   protected static List<Class<?>> toClasses(ImmutableList<Parameter> params) {
      return Lists.transform(params, new Function<Parameter, Class<?>>() {
         public Class<?> apply(Parameter input) {
            return input.getType().getRawType();
         }
      });
   }

   private static LoadingCache<Type, TypeToken<?>> typeTokenForType = CacheBuilder.newBuilder().build(
         new CacheLoader<Type, TypeToken<?>>() {
            public TypeToken<?> load(Type key) {
               return TypeToken.of(key);
            }
         });

   private static LoadingCache<Class<?>, TypeToken<?>> typeTokenForClass = CacheBuilder.newBuilder().build(
         new CacheLoader<Class<?>, TypeToken<?>>() {
            public TypeToken<?> load(Class<?> key) {
               return TypeToken.of(key);
            }
         });

   private static LoadingCache<TypeTokenAndParameterTypes, Invokable<?, ?>> constructorForParams = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeTokenAndParameterTypes, Invokable<?, ?>>() {
            public Invokable<?, ?> load(final TypeTokenAndParameterTypes key) {
               Set<Invokable<?, ?>> constructors = get(constructorsForTypeToken, key.type);
               Optional<Invokable<?, ?>> constructor = tryFind(constructors, new Predicate<Invokable<?, ?>>() {
                  public boolean apply(Invokable<?, ?> input) {
                     return Objects.equal(toClasses(input.getParameters()), key.parameterTypes);
                  }
               });
               if (constructor.isPresent())
                  return constructor.get();
               throw new IllegalArgumentException("no such constructor " + key.toString() + "in: " + constructors);
            }
         });

   private static class TypeTokenAndParameterTypes {

      protected TypeToken<?> type;
      protected List<Class<?>> parameterTypes;

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

   private static LoadingCache<TypeTokenNameAndParameterTypes, Invokable<?, ?>> methodForParams = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeTokenNameAndParameterTypes, Invokable<?, ?>>() {
            public Invokable<?, ?> load(final TypeTokenNameAndParameterTypes key) {
               Set<Invokable<?, ?>> methods = get(methodsForTypeToken, key.type);
               /*
                * There may be multiple instances, even on the most immediate ancestor,
                * of a method with the required name and parameter set. This will occur 
                * if the method overrides one declared in a parent class with a less specific
                * return type. These bridge methods inserted by the compiler will be marked
                * as "synthetic".
                */
               Optional<Invokable<?, ?>> method = tryFind(methods, new Predicate<Invokable<?, ?>>() {
                  public boolean apply(Invokable<?, ?> input) {
                     // Invokable doesn't expose Method#isBridge
                     return !input.isSynthetic() && Objects.equal(input.getName(), key.name)
                           && Objects.equal(toClasses(input.getParameters()), key.parameterTypes);
                  }
               });
               checkArgument(method.isPresent(), "no such method %s in: %s", key.toString(), methods);
               return method.get();
            }
         });

   private static class TypeTokenNameAndParameterTypes extends TypeTokenAndParameterTypes {

      private String name;

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

   /**
    * this gets all declared methods, not just public ones. makes them accessible. Does not include Object methods.
    * Invokables for a type are ordered so all invokables on a subtype are always listed before invokables on a
    * supertype (see {@link TypeToken#getTypes()}).
    */
   private static LoadingCache<TypeToken<?>, Set<Invokable<?, ?>>> methodsForTypeToken = CacheBuilder
         .newBuilder().build(new CacheLoader<TypeToken<?>, Set<Invokable<?, ?>>>() {
            public Set<Invokable<?, ?>> load(TypeToken<?> key) {
               ImmutableSet.Builder<Invokable<?, ?>> builder = ImmutableSet.<Invokable<?, ?>> builder();
               for (TypeToken<?> token : key.getTypes()) {
                  Class<?> raw = token.getRawType();
                  if (raw == Object.class)
                     continue;
                  for (Method method : raw.getDeclaredMethods()) {
                     if (!coreJavaClass(raw)) {
                        method.setAccessible(true);
                     }
                     builder.add(key.method(method));
                  }
               }
               return builder.build();
            }
         });

   private static boolean coreJavaClass(Class<?> clazz) {
      // treat null packages (e.g. for proxy objects) as "non-core"
      Package clazzPackage = clazz.getPackage();
      if (clazzPackage == null) {
         return false;
      }
      String packageName = clazzPackage.getName();
      return packageName.startsWith("com.sun.") || packageName.startsWith("java.")
              || packageName.startsWith("javax.") || packageName.startsWith("sun.");
   }

   /**
    * ensures that exceptions are not doubly-wrapped
    */
   private static <K, V> V get(LoadingCache<K, V> cache, K key) {
      try {
         return cache.get(key);
      } catch (UncheckedExecutionException e) {
         throw propagate(e.getCause());
      } catch (ExecutionException e) {
         throw propagate(e.getCause());
      }
   }
}
