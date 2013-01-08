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

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.Invocation.Result;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.reflect.Invokable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.Assisted;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * <p/>
 * Particularly, this code delegates calls to other things.
 * <ol>
 * <li>if the invoked has a {@link Provides} annotation, it responds via a {@link Injector} lookup</li>
 * <li>if the invoked has a {@link Delegate} annotation, it responds with an instance of interface set in returnVal,
 * adding the current JAXrs annotations to whatever are on that class.</li>
 * <ul>
 * <li>ex. if the invoked with {@link Delegate} has a {@code Path} annotation, and the returnval interface also has
 * {@code Path}, these values are combined.</li>
 * </ul>
 * <li>if {@link RestAnnotationProcessor#delegationMap} contains a mapping for this, and the returnVal is properly
 * assigned as a {@link ListenableFuture}, it responds with an http implementation.</li>
 * <li>otherwise a RuntimeException is thrown with a message including:
 * {@code invoked is intended solely to set constants}</li>
 * </ol>
 * 
 * @author Adrian Cole
 */
@Singleton
public class InvokeAsyncApi extends BaseInvocationFunction {

   public final static class Delegate extends InvokeAsyncApi {

      public static interface Factory {
         Delegate caller(Invocation caller);
      }

      private final String string;

      @Inject
      private Delegate(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter,
            Predicate<Invokable<?, ?>> mapsToAsyncHttpRequest,
            InvokeListenableFutureViaHttp.Caller.Factory httpCallerFactory, Delegate.Factory factory,
            @Assisted Invocation caller) {
         super(injector, optionalConverter, mapsToAsyncHttpRequest, httpCallerFactory.caller(caller), factory);
         this.string = String.format("%s->%s", caller, caller.getInvokable().getReturnType().getRawType()
               .getSimpleName());
      }

      @Override
      public String toString() {
         return string;
      }
   }

   @Resource
   private Logger logger = Logger.NULL;

   private final Predicate<Invokable<?, ?>> mapsToAsyncHttpRequest;
   private final Function<Invocation, ListenableFuture<?>> invokeMethod;
   private final Delegate.Factory factory;

   @Inject
   private InvokeAsyncApi(Injector injector, Function<InvocationSuccess, Optional<Object>> optionalConverter,
         Predicate<Invokable<?, ?>> mapsToAsyncHttpRequest, Function<Invocation, ListenableFuture<?>> invokeMethod,
         Delegate.Factory factory) {
      super(injector, optionalConverter);
      this.mapsToAsyncHttpRequest = mapsToAsyncHttpRequest;
      this.invokeMethod = invokeMethod;
      this.factory = factory;
   }

   @Override
   protected Result invoke(Invocation invocation) {
      checkState(mapsToAsyncHttpRequest.apply(invocation.getInvokable()),
            "please configure corresponding async class for %s in your RestClientModule", invocation.getInvokable());
      return Result.success(invokeMethod.apply(invocation));
   }

   @Override
   protected Function<Invocation, Result> forwardInvocations(Invocation invocation, Class<?> returnType) {
      return factory.caller(invocation);
   }

   @Override
   public String toString() {
      return String.format("async->http");
   }

}
