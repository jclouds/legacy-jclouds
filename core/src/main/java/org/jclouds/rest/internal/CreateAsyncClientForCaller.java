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

import static com.google.common.reflect.Reflection.newProxy;

import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.rest.internal.AsyncRestClientProxy.Factory;

import com.google.common.cache.CacheLoader;
import com.google.inject.Inject;

public final class CreateAsyncClientForCaller extends CacheLoader<ClassMethodArgs, Object> {
   private final Factory factory;

   @Inject
   private CreateAsyncClientForCaller(AsyncRestClientProxy.Factory factory) {
      this.factory = factory;
   }

   @Override
   public Object load(ClassMethodArgs from) {
      return newProxy(from.getClazz(), factory.caller(from));
   }
}