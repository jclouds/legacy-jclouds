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
package org.jclouds.rest.config;

import static org.jclouds.reflect.Reflection2.typeTokenOf;

import java.lang.reflect.Proxy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.internal.DelegatesToInvocationFunction;
import org.jclouds.rest.internal.InvokeAndCallGetOnFutures;

import com.google.common.cache.Cache;
import com.google.common.reflect.Invokable;
import com.google.inject.Provider;

/**
 * @author Adrian Cole
 */
@Singleton
public class CallGetOnFuturesProvider<S, A> implements Provider<S> {

   private final Class<? super S> apiType;
   private final DelegatesToInvocationFunction<S, InvokeAndCallGetOnFutures<A>> syncInvoker;

   @Inject
   private CallGetOnFuturesProvider(Cache<Invokable<?, ?>, Invokable<?, ?>> invokables,
         DelegatesToInvocationFunction<S, InvokeAndCallGetOnFutures<A>> syncInvoker, Class<S> apiType,
         Class<A> asyncApiType) {
      this.syncInvoker = syncInvoker;
      this.apiType = apiType;
      RestModule.putInvokables(typeTokenOf(apiType), typeTokenOf(asyncApiType), invokables);
   }

   @SuppressWarnings("unchecked")
   @Override
   @Singleton
   public S get() {
      return (S) Proxy.newProxyInstance(apiType.getClassLoader(), new Class<?>[] { apiType }, syncInvoker);
   }
}
