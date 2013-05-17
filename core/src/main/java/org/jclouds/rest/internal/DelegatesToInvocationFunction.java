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
package org.jclouds.rest.internal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.find;
import static com.google.inject.util.Types.newParameterizedType;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Optionals2.isReturnTypeOptional;
import static org.jclouds.util.Optionals2.unwrapIfOptional;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;
import static org.jclouds.util.Throwables2.propagateIfPossible;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.lifecycle.Closer;
import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.InvocationSuccess;
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
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.util.Types;

/**
 * @param <S>
 *           The enclosing type of the interface that a dynamic proxy like this implements
 * @param <F>
 *           The function that implements this dynamic proxy
 */
@Beta
public class DelegatesToInvocationFunction<S, F extends Function<Invocation, Object>> implements
      InvocationHandler {

   private static final Object[] NO_ARGS = {};

   /**
    * {@inheritDoc}
    * 
    * <p>
    * <ul>
    * <li>{@code proxy.hashCode()} delegates to {@link AbstractInvocationHandler#hashCode}
    * <li>{@code proxy.toString()} delegates to {@link AbstractInvocationHandler#toString}
    * <li>{@code proxy.equals(argument)} returns true if:
    * <ul>
    * <li>{@code proxy} and {@code argument} are of the same type
    * <li>and {@link AbstractInvocationHandler#equals} returns true for the {@link InvocationHandler} of
    * {@code argument}
    * </ul>
    * <li>other method calls are dispatched to {@link #handleInvocation}.
    * </ul>
    * 
    * @throws Throwable
    */
   @Override
   public final Object invoke(Object proxy, Method invoked, @Nullable Object[] argv) throws Throwable {
      if (argv == null) {
         argv = NO_ARGS;
      }
      if (argv.length == 0 && invoked.getName().equals("hashCode")) {
         return hashCode();
      }
      if (argv.length == 1 && invoked.getName().equals("equals") && invoked.getParameterTypes()[0] == Object.class) {
         Object arg = argv[0];
         return proxy.getClass().isInstance(arg) && equals(Proxy.getInvocationHandler(arg));
      }
      if (argv.length == 0 && invoked.getName().equals("toString")) {
         return toString();
      }
      List<Object> args = Arrays.asList(argv);
      if (all(args, notNull()))
         args = ImmutableList.copyOf(args);
      else
         args = Collections.unmodifiableList(args);
      Invokable<?, Object> invokable = method(ownerType, invoked);
      Invocation invocation = Invocation.create(invokable, args);
      try {
         return handle(invocation);
      } catch (Throwable e) {
         propagateIfPossible(e, invocation.getInvokable().getExceptionTypes());
         throw e;
      }
   }

   private static final Invokable<?, ?> CLOSE;

   static {
      try {
         CLOSE = Invokable.from(Closeable.class.getMethod("close"));
      } catch (SecurityException e) {
         throw propagate(e);
      } catch (NoSuchMethodException e) {
         throw propagate(e);
      }
   }

   protected Object handle(Invocation invocation) {
      Invokable<?, ?> invokable = invocation.getInvokable();
      if (CLOSE.equals(invokable)) {
         try {
            injector.getInstance(Closer.class).close();
            return null;
         } catch (Throwable e) {
            throw propagate(e);
         }
      } else if (invokable.isAnnotationPresent(Provides.class)) {
         return lookupValueFromGuice(invokable);
      } else if (invokable.isAnnotationPresent(Delegate.class)) {
         return propagateContextToDelegate(invocation);
      } else {
         return methodInvoker.apply(invocation);
      }
   }

   protected final Injector injector;
   protected final TypeToken<S> ownerType;
   protected final SetCaller setCaller;
   protected final Function<InvocationSuccess, Optional<Object>> optionalConverter;
   protected final F methodInvoker;

   @Inject
   DelegatesToInvocationFunction(Injector injector, SetCaller setCaller, 
         Class<S> ownerType, Function<InvocationSuccess, Optional<Object>> optionalConverter, F methodInvoker) {
      this.injector = checkNotNull(injector, "injector");
      this.ownerType = typeToken(checkNotNull(ownerType, "ownerType"));
      this.setCaller = checkNotNull(setCaller, "setCaller");
      this.optionalConverter = checkNotNull(optionalConverter, "optionalConverter");
      this.methodInvoker = checkNotNull(methodInvoker, "methodInvoker");
   }

   private Object propagateContextToDelegate(Invocation caller) {
      Class<?> returnType = unwrapIfOptional(caller.getInvokable().getReturnType());
      Function<Invocation, Object> delegate;
      setCaller.enter(caller);
      try {
         @SuppressWarnings("unchecked")
         Key<Function<Invocation, Object>> delegateType = (Key<Function<Invocation, Object>>) methodInvokerFor(returnType);
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
   protected Key<?> methodInvokerFor(Class<?> returnType) {
      switch (methodInvoker.getClass().getTypeParameters().length) {
      case 0:
         return Key.get(methodInvoker.getClass());
      case 1:
         return Key.get(Types.newParameterizedType(methodInvoker.getClass(), returnType));
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
      return Objects.toStringHelper("").omitNullValues().add("ownerType", ownerType.getRawType().getSimpleName())
            .add("methodInvoker", methodInvoker).toString();
   }
}
