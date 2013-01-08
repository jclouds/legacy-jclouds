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
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.reflect.Invokable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

/**
 * 
 * @author Adrian Cole
 */
public final class InvokeSyncApi extends BaseInvocationFunction {

   public static interface Factory {
      /**
       * @param receiver
       *           object whose interface matched {@code declaring} except all invokeds return {@link ListenableFuture}
       * @return blocking invocation handler
       */
      InvokeSyncApi create(Object receiver);
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final InvokeSyncApi.Factory factory;
   private final InvokeAsyncApi.Delegate.Factory asyncFactory;
   private final Map<Class<?>, Class<?>> sync2Async;
   private final Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables;
   private final InvokeFutureAndBlock.Factory blocker;
   private final Object receiver;

   @Inject
   @VisibleForTesting
   InvokeSyncApi(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter,
         InvokeSyncApi.Factory factory, InvokeAsyncApi.Delegate.Factory asyncFactory,
         Map<Class<?>, Class<?>> sync2Async, Cache<Invokable<?, ?>, Invokable<?, ?>> sync2AsyncInvokables,
         InvokeFutureAndBlock.Factory blocker, @Assisted Object receiver) {
      super(injector, optionalConverter);
      this.factory = factory;
      this.asyncFactory = asyncFactory;
      this.sync2Async = sync2Async;
      this.sync2AsyncInvokables = sync2AsyncInvokables;
      this.blocker = blocker;
      this.receiver = receiver;
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Result invoke(Invocation in) {
      @SuppressWarnings("rawtypes")
      Invokable async = checkNotNull(sync2AsyncInvokables.getIfPresent(in.getInvokable()), "invokable %s not in %s",
            in.getInvokable(), sync2AsyncInvokables);
      if (async.getReturnType().getRawType().isAssignableFrom(ListenableFuture.class)) {
         return blocker.create(receiver).apply(in);
      }
      try { // try any method
         return Result.success(async.invoke(receiver, in.getArgs().toArray()));
      } catch (InvocationTargetException e) {
         return Result.fail(e);
      } catch (IllegalAccessException e) {
         return Result.fail(e);
      }
   }

   @Override
   protected Function<Invocation, Result> forwardInvocations(Invocation invocation, Class<?> returnType) {
      // get the return type of the asynchronous class associated with this client
      // ex. FloatingIPClient is associated with FloatingIPAsyncClient
      Class<?> asyncClass = sync2Async.get(returnType);
      checkState(asyncClass != null, "please configure corresponding async class for %s in your RestClientModule",
            returnType);
      // pass any parameters necessary to get a relevant instance of that async class
      // ex. getClientForRegion("north") might return an instance whose endpoint is
      // different that "south"
      Object asyncProxy = FunctionalReflection.newProxy(asyncClass, asyncFactory.caller(invocation));
      checkState(asyncProxy != null, "configuration error, sync client for " + invocation + " not found");
      return factory.create(asyncProxy);
   }

   @Override
   public String toString() {
      return String.format("syncProxy(%s)", receiver);
   }

}
