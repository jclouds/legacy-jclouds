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

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.util.Optionals2;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * CreateClientForCaller is parameterized, so clients it creates aren't singletons. For example,
 * CreateClientForCaller satisfies a call like this:
 * {@code context.getProviderSpecificContext().getApi().getOrgClientForName(name)}
 * 
 * @author Adrian Cole
 */
public class CreateClientForCaller extends CacheLoader<ClassMethodArgs, Object> {
   @Inject
   Injector injector;
   private final LoadingCache<ClassMethodArgs, Object> asyncMap;
   private final Provider<LoadingCache<ClassMethodArgs, Object>> delegateMap;
   Map<Class<?>, Class<?>> sync2Async;

   @Inject
   CreateClientForCaller(@Named("async") LoadingCache<ClassMethodArgs, Object> asyncMap,
            @Named("sync") Provider<LoadingCache<ClassMethodArgs, Object>> delegateMap) {
      this.asyncMap = asyncMap;
      this.delegateMap = delegateMap;
   }

   @Override
   public Object load(ClassMethodArgs from) throws ExecutionException {
      Class<?> syncClass = Optionals2.returnTypeOrTypeOfOptional(from.getMethod());
      Class<?> asyncClass = sync2Async.get(syncClass);
      checkState(asyncClass != null, "configuration error, sync class " + syncClass + " not mapped to an async class");
      Object asyncClient = asyncMap.get(from);
      checkState(asyncClient != null, "configuration error, sync client for " + from + " not found");
      Function<ClassMethodArgsAndReturnVal, Optional<Object>> optionalConverter = injector.getInstance(Key.get(new TypeLiteral<Function<ClassMethodArgsAndReturnVal, Optional<Object>>>() {
      }));
      Map<String, Long> timeoutsMap = injector.getInstance(Key.get(new TypeLiteral<Map<String, Long>>() {
      }, Names.named("TIMEOUTS")));
      try {
         return SyncProxy.proxy(optionalConverter, syncClass, asyncClient, delegateMap.get(), sync2Async, timeoutsMap);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }

   }
}
