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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;
import static com.google.inject.util.Types.newParameterizedType;
import static org.jclouds.util.Optionals2.isReturnTypeOptional;
import static org.jclouds.util.Optionals2.unwrapIfOptional;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import com.google.common.reflect.Invokable;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.config.SetCaller;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @param <S>
 *           The enclosing type of the interface that a dynamic proxy like this implements
 * @param <A>
 *           The enclosing type that is processed by this proxy
 * @param <F>
 *           The function that implements this dynamic proxy
 */
@Beta
public final class DelegatingInvocationFunction<S, A, F extends Function<Invocation, Result>> implements
      Function<Invocation, Result> {
   private final Injector injector;
   private final TypeToken<S> enclosingType;
   private final SetCaller setCaller;
   private final Map<Class<?>, Class<?>> syncToAsync;
   private final Function<InvocationSuccess, Optional<Object>> optionalConverter;
   private final F methodInvoker;

   @SuppressWarnings("unchecked")
   @Inject
   DelegatingInvocationFunction(Injector injector, SetCaller setCaller, Map<Class<?>, Class<?>> syncToAsync,
         TypeLiteral<S> enclosingType, Function<InvocationSuccess, Optional<Object>> optionalConverter, F methodInvoker) {
      this.injector = checkNotNull(injector, "injector");
      this.enclosingType = (TypeToken<S>) TypeToken.of(checkNotNull(enclosingType, "enclosingType").getType());
      this.setCaller = checkNotNull(setCaller, "setCaller");
      this.syncToAsync = checkNotNull(syncToAsync, "syncToAsync");
      this.optionalConverter = checkNotNull(optionalConverter, "optionalConverter");
      this.methodInvoker = checkNotNull(methodInvoker, "methodInvoker");
   }

   @Override
   public Result apply(Invocation in) {
      if (in.getInvokable().isAnnotationPresent(Provides.class))
         return Result.success(lookupValueFromGuice(in.getInvokable()));
      else if (in.getInvokable().isAnnotationPresent(Delegate.class))
         return Result.success(propagateContextToDelegate(in));
      return methodInvoker.apply(in);
   }

   private Object propagateContextToDelegate(Invocation caller) {
      Class<?> returnType = unwrapIfOptional(caller.getInvokable().getReturnType());
      Function<Invocation, Result> delegate;
      setCaller.enter(enclosingType, caller);
      try {
         @SuppressWarnings("unchecked")
         Key<Function<Invocation, Result>> delegateType = (Key<Function<Invocation, Result>>) methodInvokerFor(returnType);
         delegate = injector.getInstance(delegateType);
      } finally {
         setCaller.exit();
      }
      Object result = FunctionalReflection.newProxy(returnType, delegate);
      if (isReturnTypeOptional(caller.getInvokable())) {
         result = optionalConverter.apply(InvocationSuccess.create(caller, result));
      }
      return result;
   }

   /**
    * attempts to guess the generic type params for the delegate's invocation function based on the supplied type 
    */
   private Key<?> methodInvokerFor(Class<?> returnType) {
      switch (methodInvoker.getClass().getTypeParameters().length) {
      case 0:
         return Key.get(methodInvoker.getClass());
      case 1:
         return Key.get(Types.newParameterizedType(methodInvoker.getClass(), returnType));
      case 2:
         if (syncToAsync.containsValue(returnType))
            return Key.get(Types.newParameterizedType(methodInvoker.getClass(), returnType, returnType));
         return Key.get(Types.newParameterizedType(
               methodInvoker.getClass(),
               returnType,
               checkNotNull(syncToAsync.get(returnType), "need async type of %s for %s", returnType,
                     methodInvoker.getClass())));
      }
      throw new IllegalArgumentException(returnType + " has too many type parameters");
   }

   static final Predicate<Annotation> isQualifierPresent = new Predicate<Annotation>() {
      public boolean apply(Annotation input) {
         return input.annotationType().isAnnotationPresent(Qualifier.class);
      }
   };

   private Object lookupValueFromGuice(Invokable<?, ?> invoked) {
      try {
         Type genericReturnType = invoked.getReturnType().getType();
         try {
            Annotation qualifier = find(ImmutableList.copyOf(invoked.getAnnotations()), isQualifierPresent);
            return getInstanceOfTypeWithQualifier(genericReturnType, qualifier);
         } catch (ProvisionException e) {
            throw propagate(e.getCause());
         } catch (RuntimeException e) {
            return instanceOfTypeOrPropagate(genericReturnType, e);
         }
      } catch (ProvisionException e) {
         AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            throw aex;
         throw e;
      }
   }

   Object instanceOfTypeOrPropagate(Type genericReturnType, RuntimeException e) {
      try {
         // look for an existing binding
         Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType));
         if (binding != null)
            return binding.getProvider().get();

         // then, try looking via supplier
         binding = injector.getExistingBinding(Key.get(newParameterizedType(Supplier.class, genericReturnType)));
         if (binding != null)
            return Supplier.class.cast(binding.getProvider().get()).get();

         // else try to create an instance
         return injector.getInstance(Key.get(genericReturnType));
      } catch (ConfigurationException ce) {
         throw e;
      }
   }

   Object getInstanceOfTypeWithQualifier(Type genericReturnType, Annotation qualifier) {
      // look for an existing binding
      Binding<?> binding = injector.getExistingBinding(Key.get(genericReturnType, qualifier));
      if (binding != null)
         return binding.getProvider().get();

      // then, try looking via supplier
      binding = injector
            .getExistingBinding(Key.get(newParameterizedType(Supplier.class, genericReturnType), qualifier));
      if (binding != null)
         return Supplier.class.cast(binding.getProvider().get()).get();

      // else try to create an instance
      return injector.getInstance(Key.get(genericReturnType, qualifier));
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues()
            .add("enclosingType", enclosingType.getRawType().getSimpleName()).add("methodInvoker", methodInvoker)
            .toString();
   }
}