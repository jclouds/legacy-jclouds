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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

/**
 * based on the {@link com.google.reflect.AccessibleObject} copied in as {@link com.google.reflect.Invokable} is package
 * private. This adds access to {#link {@link #getEnclosingType()} and folds in ability to lookup methods and
 * constructors by name.
 * 
 * @author Adrian Cole
 * @since 1.6
 */
@Beta
public abstract class Invokable<T, R> extends Element implements GenericDeclaration {
   protected final TypeToken<?> enclosingType;

   <M extends AccessibleObject & Member> Invokable(TypeToken<?> enclosingType, M member) {
      super(member);
      this.enclosingType = checkNotNull(enclosingType, "enclosingType");
   }

   /** Returns {@link Invokable} of {@code method}. */
   public static Invokable<?, Object> from(Method method) {
      return from(TypeToken.of(method.getDeclaringClass()), method);
   }

   /** Returns {@link Invokable} of {@code constructor}. */
   public static <T> Invokable<T, T> from(Constructor<T> constructor) {
      return from(TypeToken.of(constructor.getDeclaringClass()), constructor);
   }

   /** Returns {@link Invokable} of {@code method}. */
   public static <T> Invokable<T, Object> from(TypeToken<T> enclosingType, Method method) {
      return new MethodInvokable<T>(enclosingType, method);
   }

   /** Returns {@link Invokable} of {@code constructor}. */
   public static <T> Invokable<T, T> from(TypeToken<T> enclosingType, Constructor<T> constructor) {
      return new ConstructorInvokable<T>(enclosingType, constructor);
   }

   /**
    * different than {@link #getDeclaringClass()} when this is a member of a class it was not declared in.
    */
   public TypeToken<?> getEnclosingType() {
      return enclosingType;
   }

   /**
    * Returns {@code true} if this is an overridable method. Constructors, private, static or final methods, or methods
    * declared by final classes are not overridable.
    */
   public abstract boolean isOverridable();

   /** Returns {@code true} if this was declared to take a variable number of arguments. */
   public abstract boolean isVarArgs();

   /**
    * Invokes with {@code receiver} as 'this' and {@code args} passed to the underlying method and returns the return
    * value; or calls the underlying constructor with {@code args} and returns the constructed instance.
    * 
    * @throws IllegalAccessException
    *            if this {@code Constructor} object enforces Java language access control and the underlying method or
    *            constructor is inaccessible.
    * @throws IllegalArgumentException
    *            if the number of actual and formal parameters differ; if an unwrapping conversion for primitive
    *            arguments fails; or if, after possible unwrapping, a parameter value cannot be converted to the
    *            corresponding formal parameter type by a method invocation conversion.
    * @throws InvocationTargetException
    *            if the underlying method or constructor throws an exception.
    */
   // All subclasses are owned by us and we'll make sure to get the R type right.
   @SuppressWarnings("unchecked")
   public final R invoke(@Nullable T receiver, Object... args) throws InvocationTargetException, IllegalAccessException {
      return (R) invokeInternal(receiver, checkNotNull(args, "args"));
   }

   /** Returns the return type of this {@code Invokable}. */
   // All subclasses are owned by us and we'll make sure to get the R type right.
   @SuppressWarnings("unchecked")
   public final TypeToken<? extends R> getReturnType() {
      return (TypeToken<? extends R>) TypeToken.of(getGenericReturnType());
   }

   /**
    * Returns all declared parameters of this {@code Invokable}. Note that if this is a constructor of a non-static
    * inner class, unlike {@link Constructor#getParameterTypes}, the hidden {@code this} parameter of the enclosing
    * class is excluded from the returned parameters.
    */
   public final ImmutableList<Parameter> getParameters() {
      Type[] parameterTypes = getGenericParameterTypes();
      Annotation[][] annotations = getParameterAnnotations();
      ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
      for (int i = 0; i < parameterTypes.length; i++) {
         builder.add(new Parameter(this, i, TypeToken.of(parameterTypes[i]), annotations[i]));
      }
      return builder.build();
   }

   /** Returns all declared exception types of this {@code Invokable}. */
   public final ImmutableList<TypeToken<? extends Throwable>> getExceptionTypes() {
      ImmutableList.Builder<TypeToken<? extends Throwable>> builder = ImmutableList.builder();
      for (Type type : getGenericExceptionTypes()) {
         // getGenericExceptionTypes() will never return a type that's not exception
         @SuppressWarnings("unchecked")
         TypeToken<? extends Throwable> exceptionType = (TypeToken<? extends Throwable>) TypeToken.of(type);
         builder.add(exceptionType);
      }
      return builder.build();
   }

   /**
    * Explicitly specifies the return type of this {@code Invokable}. For example:
    * 
    * <pre>
    * {
    *    &#064;code
    *    Method factoryMethod = Person.class.getMethod(&quot;create&quot;);
    *    Invokable&lt;?, Person&gt; factory = Invokable.of(getNameMethod).returning(Person.class);
    * }
    * </pre>
    */
   public final <R1 extends R> Invokable<T, R1> returning(Class<R1> returnType) {
      return returning(TypeToken.of(returnType));
   }

   /** Explicitly specifies the return type of this {@code Invokable}. */
   public final <R1 extends R> Invokable<T, R1> returning(TypeToken<R1> returnType) {
      if (!returnType.isAssignableFrom(getReturnType())) {
         throw new IllegalArgumentException("Invokable is known to return " + getReturnType() + ", not " + returnType);
      }
      @SuppressWarnings("unchecked")
      // guarded by previous check
      Invokable<T, R1> specialized = (Invokable<T, R1>) this;
      return specialized;
   }

   @SuppressWarnings("unchecked")
   // The declaring class is T's raw class, or one of its supertypes.
   @Override
   public final Class<? super T> getDeclaringClass() {
      return (Class<? super T>) super.getDeclaringClass();
   }

   abstract Object invokeInternal(@Nullable Object receiver, Object[] args) throws InvocationTargetException,
         IllegalAccessException;

   abstract Type[] getGenericParameterTypes();

   /** This should never return a type that's not a subtype of Throwable. */
   abstract Type[] getGenericExceptionTypes();

   abstract Annotation[][] getParameterAnnotations();

   abstract Type getGenericReturnType();

   static class MethodInvokable<T> extends Invokable<T, Object> {

      private final Method method;

      MethodInvokable(TypeToken<?> enclosingType, Method method) {
         super(enclosingType, method);
         this.method = method;
         checkArgument(TypeToken.of(method.getDeclaringClass()).isAssignableFrom(enclosingType),
               "%s not declared by %s", method, enclosingType);
      }

      @Override
      final Object invokeInternal(@Nullable Object receiver, Object[] args) throws InvocationTargetException,
            IllegalAccessException {
         return method.invoke(receiver, args);
      }

      @Override
      Type getGenericReturnType() {
         return resolveType(method.getGenericReturnType()).getType();
      }

      @Override
      Type[] getGenericParameterTypes() {
         return resolveInPlace(method.getGenericParameterTypes());
      }

      @Override
      Type[] getGenericExceptionTypes() {
         return resolveInPlace(method.getGenericExceptionTypes());
      }

      @Override
      final Annotation[][] getParameterAnnotations() {
         return method.getParameterAnnotations();
      }

      @Override
      public final TypeVariable<?>[] getTypeParameters() {
         return method.getTypeParameters();
      }

      @Override
      public final boolean isOverridable() {
         return !(isFinal() || isPrivate() || isStatic() || Modifier.isFinal(getDeclaringClass().getModifiers()));
      }

      @Override
      public final boolean isVarArgs() {
         return method.isVarArgs();
      }
   }

   protected TypeToken<?> resolveType(Type type) {
      return enclosingType.resolveType(type);
   }

   protected Type[] resolveInPlace(Type[] types) {
      for (int i = 0; i < types.length; i++) {
         types[i] = resolveType(types[i]).getType();
      }
      return types;
   }

   static class ConstructorInvokable<T> extends Invokable<T, T> {

      private final Constructor<?> constructor;

      ConstructorInvokable(TypeToken<?> enclosingType, Constructor<?> constructor) {
         super(enclosingType, constructor);
         this.constructor = constructor;
         checkArgument(constructor.getDeclaringClass() == enclosingType.getRawType(), "%s not declared by %s",
               constructor, enclosingType.getRawType());
      }

      @Override
      final Object invokeInternal(@Nullable Object receiver, Object[] args) throws InvocationTargetException,
            IllegalAccessException {
         try {
            return constructor.newInstance(args);
         } catch (InstantiationException e) {
            throw new RuntimeException(constructor + " failed.", e);
         }
      }

      @Override
      Type getGenericReturnType() {
         return resolveType(constructor.getDeclaringClass()).getType();
      }

      @Override
      Type[] getGenericParameterTypes() {
         Type[] types = constructor.getGenericParameterTypes();
         Class<?> declaringClass = constructor.getDeclaringClass();
         if (!Modifier.isStatic(declaringClass.getModifiers()) && declaringClass.getEnclosingClass() != null) {
            if (types.length == constructor.getParameterTypes().length) {
               // first parameter is the hidden 'this'
               return Arrays.copyOfRange(types, 1, types.length);
            }
         }
         return resolveInPlace(types);
      }

      @Override
      Type[] getGenericExceptionTypes() {
         return resolveInPlace(constructor.getGenericExceptionTypes());
      }

      @Override
      final Annotation[][] getParameterAnnotations() {
         return constructor.getParameterAnnotations();
      }

      @Override
      public final TypeVariable<?>[] getTypeParameters() {
         return constructor.getTypeParameters();
      }

      @Override
      public final boolean isOverridable() {
         return false;
      }

      @Override
      public final boolean isVarArgs() {
         return constructor.isVarArgs();
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Invokable<?, ?> that = Invokable.class.cast(o);
      return equal(this.enclosingType, that.enclosingType) && super.equals(o);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enclosingType, super.hashCode());
   }

   @Override
   public String toString() {
      int parametersTypeHashCode = 0;
      for (Parameter param : getParameters())
         parametersTypeHashCode += param.getType().hashCode();
      return String.format("%s.%s[%s]", enclosingType.getRawType().getSimpleName(), getName(), parametersTypeHashCode);
   }
}
