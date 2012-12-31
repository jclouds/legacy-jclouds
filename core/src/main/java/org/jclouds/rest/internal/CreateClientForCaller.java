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
import static com.google.common.reflect.Reflection.newProxy;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.util.Optionals2;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * CreateClientForCaller is parameterized, so clients it creates aren't singletons. For example, CreateClientForCaller
 * satisfies a call like this: {@code context.getProviderSpecificContext().getApi().getOrgClientForName(name)}
 * 
 * @author Adrian Cole
 */
public class CreateClientForCaller extends CacheLoader<ClassMethodArgs, Object> {
   private final SyncProxy.Factory factory;
   private final LoadingCache<ClassMethodArgs, Object> asyncMap;
   private final Map<Class<?>, Class<?>> sync2Async;

   @Inject
   private CreateClientForCaller(SyncProxy.Factory factory,
         @Named("async") LoadingCache<ClassMethodArgs, Object> asyncMap, Map<Class<?>, Class<?>> sync2Async) {
      this.factory = factory;
      this.asyncMap = asyncMap;
      this.sync2Async = sync2Async;
   }

   @Override
   public Object load(ClassMethodArgs from) {
      Class<?> syncClass = Optionals2.returnTypeOrTypeOfOptional(from.getMethod());
      Class<?> asyncClass = sync2Async.get(syncClass);
      checkState(asyncClass != null, "configuration error, sync class " + syncClass + " not mapped to an async class");
      Object asyncClient = asyncMap.getUnchecked(from);
      checkState(asyncClient != null, "configuration error, sync client for " + from + " not found");
      return newProxy(syncClass, factory.create(syncClass, asyncClient));
   }
}
