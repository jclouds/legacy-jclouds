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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.tryFind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Parameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.jclouds.javax.annotation.Nullable;

/**
 * Utilities that allow access to {@link Invokable}s with {@link Invokable#getOwnerType() owner types}.
 *
 * @since 1.6
 */
@Beta
public class Reflection2 {

   private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)([A-Z]+[a-zA-Z0-9]*)");
   private static final Pattern SETTER_PATTERN = Pattern.compile("set[A-Z]+[a-zA-Z0-9]*]");

   /**
    * gets a {@link TypeToken} for the given type.
    */
   @SuppressWarnings("unchecked")
   public static <T> TypeToken<T> typeToken(Type in) {
      return (TypeToken<T>) get(typeTokenForType, checkNotNull(in, "class"));
   }

   /**
    * Checks if type is assignable from another type.
    * @param type    The type to check if is assignable.
    * @param from    The target type.
    * @return
    */
   public static boolean isAssignable(Type type, Type from) {
      return get(typeTokenForType,type).isAssignableFrom(from);
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
    * Return true if {@link Method} is a getter.
    *
    * @param invokable
    * @return
    */
   public static boolean isGetter(Invokable invokable) {
      String name = invokable.getName();
      TypeToken typeToken = invokable.getReturnType();

      TypeVariable params[] = invokable.getTypeParameters();

      if (!GETTER_PATTERN.matcher(name).matches()) {
         return false;
      }

      // special for isXXX boolean
      if (name.startsWith("is")) {
         return params.length == 0 && typeToken.getRawType().getSimpleName().equalsIgnoreCase("boolean");
      }

      return params.length == 0 && !typeToken.equals(Void.TYPE);
   }


   /**
    * Returns the property name that corresponds to the {@link Method}.
    * If method is a getter, it strips the get/is prefix and returns the rest, with the first character to lower case.
    *
    * @param invokable
    * @return
    */
   public static String shortNameOf(Invokable invokable) {
      if (!isGetter(invokable)) {
         return invokable.getName();
      }
      String name = invokable.getName();
      Matcher matcher = GETTER_PATTERN.matcher(name);
      if (matcher.matches()) {
         String propertyName = matcher.group(2);
         if (!Strings.isNullOrEmpty(propertyName)) {
            return propertyName.substring(0, 1).toLowerCase(Locale.ENGLISH) + propertyName.substring(1);
         }
      }
      return name;
   }

   /**
    * Returns a getter {@link Invokable} for property.
    *
    * @param type
    * @param property
    * @return
    */
   public static Optional<Invokable<?, ?>> getPropertyGetter(Type type, String property)  {
      final String getMethodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
      final String isMethodName = "is" + property.substring(0, 1).toUpperCase() + property.substring(1);

      TypeToken typeToken = TypeToken.of(type);
      return Iterables.tryFind(get(methodsForTypeToken, typeToken), new Predicate<Invokable<?, ?>>() {
         @Override
         public boolean apply(@Nullable Invokable<?, ?> input) {
            return input.getName().equals(getMethodName) || input.getName().equals(isMethodName);
         }
      });
   }

   /**
    * Returns all methods annotated with the specified annotation.
    * @param type
    * @param annotation
    * @return
    */
   public static Iterable<Invokable<?, ?>> methodsAnnotatedWith(Type type, final Class<? extends Annotation> annotation)  {
      TypeToken typeToken = TypeToken.of(type);
      return Iterables.filter(get(methodsForTypeToken, typeToken), new Predicate<Invokable<?, ?>>() {
         @Override
         public boolean apply(@Nullable Invokable<?, ?> input) {
            return input.isAnnotationPresent(annotation);
         }
      });
   }

   /**
    * Returns all methods annotated with the specified annotation.
    *
    * @param type
    * @param annotation
    * @return
    */
   public static Iterable<Field> fieldsAnnotatedWith(Type type, final Class<? extends Annotation> annotation)  {
      TypeToken typeToken = TypeToken.of(type);
      return Iterables.filter(get(fieldsForTypeToken, typeToken), new Predicate<Field>() {
         @Override
         public boolean apply(@Nullable Field input) {
            return input.isAnnotationPresent(annotation);
         }
      });
   }

   /**
    * Returns a getter {@link Invokable} for property.
    * @param type
    * @param property
    * @return
    */
   public static Optional<Field> getPropertyField(Type type, final String property) {
      TypeToken typeToken = TypeToken.of(type);
      return Iterables.tryFind(get(fieldsForTypeToken, typeToken), new Predicate<Field>() {
         @Override
         public boolean apply(@Nullable Field input) {
            return input.getName().equals(property);
         }
      });
   }

   /**
    * Returns the property value of the target object.
    * It first tries to find a getter for the property, then tries to find a field and finally it falls back to finding a
    * method that matches the property name.
    * @param target
    * @param property
    * @return
    * @throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    */
   public static Object getPropertyValue(Object target, final String property) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      Optional<Invokable<?, ?>> getter = getPropertyGetter(target.getClass(), property);
      if (getter.isPresent()) {
         return ((Invokable) getter.get()).invoke(target);
      }

      Optional<Field> field = getPropertyField(target.getClass(), property);
      if (field.isPresent()) {
         return field.get().get(target);
      } else {
         throw new NoSuchMethodException("No getters or field found for property:" + property + " on " + target.getClass());
      }
   }


   /**
    * Returns the type parameter of t.
    * @param t
    * @return
    */
   public static Class typeParameterOf(Type t) {
      if (t instanceof ParameterizedType) {
         Type[] arguments = ((ParameterizedType) t).getActualTypeArguments();
         return (Class) arguments[0];
      } else {
         throw new IllegalArgumentException("Type " + t + " is not parameterized.");
      }
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
      return (packageName.startsWith("com.sun.") || packageName.startsWith("java.")
              || packageName.startsWith("javax.") || packageName.startsWith("sun."));
   }

   /**
    * this gets all declared fields, not just public ones. makes them accessible. Does not include Object methods.
    * Fields for a type are ordered so all fields on a subtype are always listed before fields on a
    * supertype (see {@link TypeToken#getTypes()}).
    */
   private static LoadingCache<TypeToken<?>, Set<Field>> fieldsForTypeToken = CacheBuilder
           .newBuilder().build(new CacheLoader<TypeToken<?>, Set<Field>>() {
              public Set<Field> load(TypeToken<?> key) {
                 ImmutableSet.Builder<Field> builder = ImmutableSet.<Field> builder();
                 for (TypeToken<?> token : key.getTypes()) {
                    Class<?> raw = token.getRawType();
                    if (raw == Object.class)
                       continue;
                    for (Field field : raw.getDeclaredFields()) {
                       field.setAccessible(true);
                       builder.add(field);
                    }
                 }
                 return builder.build();
              }
           });

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