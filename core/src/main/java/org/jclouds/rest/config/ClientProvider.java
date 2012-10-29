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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.internal.ClassMethodArgsAndReturnVal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * ClientProvider makes the primary interface for the provider context. ex. {@code
 * context.getProviderSpecificContext().getApi()} is created by ClientProvider, which is a singleton
 * 
 * @author Adrian Cole
 */
@Singleton
public class ClientProvider<S, A> implements Provider<S> {
   @Inject
   Injector injector;
   private final Class<?> syncClientType;
   private final Class<?> asyncClientType;
   private final Map<Class<?>, Class<?>> sync2Async;

   @Inject
   ClientProvider(Class<?> syncClientType, Class<?> asyncClientType, Map<Class<?>, Class<?>> sync2Async) {
      this.asyncClientType = asyncClientType;
      this.syncClientType = syncClientType;
      this.sync2Async = sync2Async;
   }

   @Override
   @Singleton
   public S get() {
      A client = (A) injector.getInstance(Key.get(asyncClientType));
      Function<ClassMethodArgsAndReturnVal, Optional<Object>> optionalConverter = injector.getInstance(Key.get(new TypeLiteral<Function<ClassMethodArgsAndReturnVal, Optional<Object>>>() {
      }));
      LoadingCache<ClassMethodArgs, Object> delegateMap = injector.getInstance(Key.get(
               new TypeLiteral<LoadingCache<ClassMethodArgs, Object>>() {
               }, Names.named("sync")));
      Map<String, Long> timeoutsMap = injector.getInstance(Key.get(new TypeLiteral<Map<String, Long>>() {
      }, Names.named("TIMEOUTS")));
      try {
         return (S) SyncProxy.proxy(optionalConverter, syncClientType, client, delegateMap, sync2Async,
               timeoutsMap);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }
}
