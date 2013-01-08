package org.jclouds.rest.internal;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.find;
import static com.google.inject.util.Types.newParameterizedType;
import static org.jclouds.util.Optionals2.isReturnTypeOptional;
import static org.jclouds.util.Optionals2.unwrapIfOptional;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Qualifier;

import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.reflect.Invokable;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.Delegate;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;

@Beta
abstract class BaseInvocationFunction implements Function<Invocation, Result> {
   protected final Injector injector;
   protected final Function<InvocationSuccess, Optional<Object>> optionalConverter;

   protected BaseInvocationFunction(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter) {
      this.injector = injector;
      this.optionalConverter = optionalConverter;
   }

   protected abstract Result invoke(Invocation in);

   protected abstract Function<Invocation, Result> forwardInvocations(Invocation invocation, Class<?> returnType);

   @Override
   public Result apply(Invocation invocation) {
      if (invocation.getInvokable().isAnnotationPresent(Provides.class))
         return Result.success(lookupValueFromGuice(invocation.getInvokable()));
      else if (invocation.getInvokable().isAnnotationPresent(Delegate.class))
         return Result.success(propagateContextToDelegate(invocation));
      return invoke(invocation);
   }

   private Object propagateContextToDelegate(Invocation invocation) {
      Class<?> returnType = unwrapIfOptional(invocation.getInvokable().getReturnType());
      Object result = FunctionalReflection.newProxy(returnType, forwardInvocations(invocation, returnType));
      if (isReturnTypeOptional(invocation.getInvokable())) {
         result = optionalConverter.apply(InvocationSuccess.create(invocation, result));
      }
      return result;
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
}