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

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;

/**
 * CreateClientForCaller is parameterized, so clients it creates aren't singletons. For example,
 * CreateClientForCaller satisfies a call like this:
 * {@code context.getProviderSpecificContext().getApi().getOrgClientForName(name)}
 * 
 * @author Adrian Cole
 */
public class CreateClientForCaller extends CacheLoader<ClassMethodArgs, Object> {
   private final Cache<ClassMethodArgs, Object> asyncMap;
   private final Provider<Cache<ClassMethodArgs, Object>> delegateMap;
   Map<Class<?>, Class<?>> sync2Async;

   @Inject
   CreateClientForCaller(@Named("async") Cache<ClassMethodArgs, Object> asyncMap,
            @Named("sync") Provider<Cache<ClassMethodArgs, Object>> delegateMap) {
      this.asyncMap = asyncMap;
      this.delegateMap = delegateMap;
   }

   @Override
   public Object load(ClassMethodArgs from) throws ExecutionException {
      Class<?> syncClass = from.getMethod().getReturnType();
      Class<?> asyncClass = sync2Async.get(syncClass);
      checkState(asyncClass != null, "configuration error, sync class " + syncClass + " not mapped to an async class");
      Object asyncClient = asyncMap.get(from);
      checkState(asyncClient != null, "configuration error, sync client for " + from + " not found");
      try {
         return SyncProxy.proxy(syncClass, new SyncProxy(syncClass, asyncClient, delegateMap.get(), sync2Async));
      } catch (Exception e) {
         Throwables.propagate(e);
         assert false : "should have propagated";
         return null;
      }

   }
}