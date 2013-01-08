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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.reflect.FunctionalReflection;
import org.jclouds.reflect.Invokable;
import org.jclouds.rest.internal.InvokeSyncApi;

import com.google.common.cache.Cache;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

/**
 * ClientProvider makes the primary interface for the provider context. ex.
 * {@code context.getProviderSpecificContext().getApi()} is created by ClientProvider, which is a singleton
 * 
 * @author Adrian Cole
 */
@Singleton
public class ClientProvider<S, A> implements Provider<S> {

   private final InvokeSyncApi.Factory factory;
   private final Class<S> syncClientType;
   private final A asyncClient;

   @Inject
   private ClientProvider(Cache<Invokable<?, ?>, Invokable<?, ?>> invokables, InvokeSyncApi.Factory factory,
         Class<S> syncClientType, Class<A> asyncClientType, A asyncClient) {
      this.factory = factory;
      this.asyncClient = asyncClient;
      this.syncClientType = syncClientType;
      RestModule.putInvokables(TypeToken.of(syncClientType), TypeToken.of(asyncClientType), invokables);
   }

   @Override
   @Singleton
   public S get() {
      return FunctionalReflection.newProxy(syncClientType, factory.create(asyncClient));
   }
}
