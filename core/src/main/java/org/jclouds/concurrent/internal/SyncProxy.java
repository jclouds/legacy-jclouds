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
package org.jclouds.concurrent.internal;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.internal.ForwardInvocationToInterface;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.internal.AsyncRestClientProxy;
import org.jclouds.util.Optionals2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.assistedinject.Assisted;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * 
 * @author Adrian Cole
 */
public final class SyncProxy implements Function<Invocation, Result> {

   public static interface Factory {
      /**
       * @param declaring
       *           type of the interface where all invokeds match those of {@code async} except the return values are
       *           dereferenced
       * @param async
       *           object whose interface matched {@code declaring} except all invokeds return {@link ListenableFuture}
       * @return blocking invocation handler
       */
      SyncProxy create(Class<?> declaring, Object async);
   }

   /**
    * CreateClientForCaller is parameterized, so clients it creates aren't singletons. For example,
    * CreateClientForCaller satisfies a call like this:
    * {@code context.getProviderSpecificContext().getApi().getOrgClientForName(name)}
    * 
    * @author Adrian Cole
    */
   public static class CreateClientForCaller implements Function<ForwardInvocationToInterface, Object> {
      private final SyncProxy.Factory factory;
      private final AsyncRestClientProxy.Caller.Factory asyncFactory;

      @Inject
      private CreateClientForCaller(SyncProxy.Factory factory, AsyncRestClientProxy.Caller.Factory asyncFactory) {
         this.factory = factory;
         this.asyncFactory = asyncFactory;
      }

      @Override
      public Object apply(ForwardInvocationToInterface from) {
         Object asyncClient = FunctionalReflection.newProxy(from.getInterfaceType(),
               asyncFactory.caller(from.getInvocation(), from.getInterfaceType()));
         checkState(asyncClient != null, "configuration error, sync client for " + from + " not found");
         Class<?> type = Optionals2.unwrapIfOptional(from.getInvocation().getInvokable().getReturnType());
         return FunctionalReflection.newProxy(type, factory.create(type, asyncClient));
      }
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Function<InvocationSuccess, Optional<Object>> optionalConverter;
   private final Object delegate;
   private final Class<?> declaring;
   private final Map<Invokable<?, ?>, Invokable<Object, ListenableFuture<?>>> invokedMap;
   private final Map<Invokable<?, ?>, Invokable<Object, ?>> syncMethodMap;
   private final Map<Invokable<?, ?>, Optional<Long>> timeoutMap;
   private final Function<ForwardInvocationToInterface, Object> createClientForCaller;
   private final Map<Class<?>, Class<?>> sync2Async;
   private static final Set<Method> objectMethods = ImmutableSet.copyOf(Object.class.getMethods());

   @SuppressWarnings("unchecked")
   @Inject
   @VisibleForTesting
   SyncProxy(Function<InvocationSuccess, Optional<Object>> optionalConverter,
         Function<ForwardInvocationToInterface, Object> createClientForCaller, Map<Class<?>, Class<?>> sync2Async,
         @Named("TIMEOUTS") Map<String, Long> timeouts, @Assisted Class<?> declaring, @Assisted Object async)
         throws SecurityException, NoSuchMethodException {
      this.optionalConverter = optionalConverter;
      this.createClientForCaller = createClientForCaller;
      this.delegate = async;
      this.declaring = declaring;
      this.sync2Async = ImmutableMap.copyOf(sync2Async);

      ImmutableMap.Builder<Invokable<?, ?>, Invokable<Object, ListenableFuture<?>>> invokedMapBuilder = ImmutableMap
            .builder();
      ImmutableMap.Builder<Invokable<?, ?>, Invokable<Object, ?>> syncMethodMapBuilder = ImmutableMap.builder();

      for (Method invoked : declaring.getMethods()) {
         if (!objectMethods.contains(invoked)) {
            Method delegatedMethod = delegate.getClass().getMethod(invoked.getName(), invoked.getParameterTypes());
            if (!Arrays.equals(delegatedMethod.getExceptionTypes(), invoked.getExceptionTypes()))
               throw new IllegalArgumentException(String.format(
                     "invoked %s has different typed exceptions than delegated invoked %s", invoked, delegatedMethod));
            if (delegatedMethod.getReturnType().isAssignableFrom(ListenableFuture.class)) {
               invokedMapBuilder.put(Invokable.from(invoked), Invokable.class.cast(Invokable.from(delegatedMethod)));
            } else {
               syncMethodMapBuilder.put(Invokable.from(invoked), Invokable.class.cast(Invokable.from(delegatedMethod)));
            }
         }
      }
      invokedMap = invokedMapBuilder.build();
      syncMethodMap = syncMethodMapBuilder.build();

      ImmutableMap.Builder<Invokable<?, ?>, Optional<Long>> timeoutMapBuilder = ImmutableMap.builder();
      for (Invokable<?, ?> invoked : invokedMap.keySet()) {
         timeoutMapBuilder.put(invoked, timeoutInNanos(invoked, timeouts));
      }
      timeoutMap = timeoutMapBuilder.build();
   }

   @Override
   public Result apply(Invocation invocation) {
      if (invocation.getInvokable().isAnnotationPresent(Delegate.class))
         return forwardToDelegate(invocation);
      if (syncMethodMap.containsKey(invocation.getInvokable()))
         return invokeOnDelegate(invocation);
      return invokeFutureAndBlock(invocation);
   }

   private Result forwardToDelegate(Invocation invocation) {
      Class<?> returnType = Optionals2.unwrapIfOptional(invocation.getInvokable().getReturnType());
      // get the return type of the asynchronous class associated with this client
      // ex. FloatingIPClient is associated with FloatingIPAsyncClient
      Class<?> asyncClass = sync2Async.get(returnType);
      checkState(asyncClass != null, "please configure corresponding async class for %s in your RestClientModule",
            returnType);
      // pass any parameters necessary to get a relevant instance of that async class
      // ex. getClientForRegion("north") might return an instance whose endpoint is
      // different that "south"
      ForwardInvocationToInterface cma = ForwardInvocationToInterface.create(invocation, asyncClass);
      Object result = createClientForCaller.apply(cma);
      if (Optionals2.isReturnTypeOptional(invocation.getInvokable())) {
         result = optionalConverter.apply(InvocationSuccess.create(invocation, result));
      }
      return Result.success(result);
   }

   private Result invokeFutureAndBlock(Invocation invocation) {
      try {
         Invokable<Object, ListenableFuture<?>> asyncMethod = invokedMap.get(invocation.getInvokable());
         ListenableFuture<?> future =  asyncMethod.invoke(delegate, invocation.getArgs().toArray());
         Optional<Long> timeoutNanos = timeoutMap.get(invocation.getInvokable());
         return block(future, timeoutNanos);
      } catch (InvocationTargetException e) {
         return Result.fail(e);
      } catch (IllegalAccessException e) {
         return Result.fail(e);
      }
   }

   private Result block(ListenableFuture<?> future, Optional<Long> timeoutNanos) {
      try {
         if (timeoutNanos.isPresent()) {
            logger.debug(">> blocking on %s for %s", future, timeoutNanos);
            return Result.success(future.get(timeoutNanos.get(), TimeUnit.NANOSECONDS));
         } else {
            logger.debug(">> blocking on %s", future);
            return Result.success(future.get());
         }
      } catch (ExecutionException e) {
         return Result.fail(e.getCause());
      } catch (InterruptedException e) {
         return Result.fail(e); // TODO: should we kill the future?
      } catch (TimeoutException e) {
         return Result.fail(e);
      }
   }

   private Result invokeOnDelegate(Invocation invocation) {
      Invokable<Object, ?> toInvoke = syncMethodMap.get(invocation.getInvokable());
      try {
         return Result.success(toInvoke.invoke(delegate, invocation.getArgs().toArray()));
      } catch (InvocationTargetException e) {
         return Result.fail(e);
      } catch (IllegalAccessException e) {
         return Result.fail(e);
      }
   }

   // override timeout by values configured in properties(in ms)
   private Optional<Long> timeoutInNanos(Invokable<?, ?> invoked, Map<String, Long> timeouts) {
      String className = declaring.getSimpleName();
      Optional<Long> timeoutMillis = fromNullable(timeouts.get(className + "." + invoked.getName())).or(
            fromNullable(timeouts.get(className))).or(fromNullable(timeouts.get("default")));
      if (timeoutMillis.isPresent())
         return Optional.of(TimeUnit.MILLISECONDS.toNanos(timeoutMillis.get()));
      return Optional.absent();
   }

   @Override
   public String toString() {
      return String.format("%s->%s", declaring.getClass().getSimpleName(), delegate.getClass().getSimpleName());
   }
}
