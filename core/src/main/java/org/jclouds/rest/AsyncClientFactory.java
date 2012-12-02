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
package org.jclouds.rest;

import static com.google.common.reflect.Reflection.newProxy;
import static com.google.inject.util.Types.newParameterizedType;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.internal.AsyncRestClientProxy;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AsyncClientFactory {
   private final Injector injector;

   @Inject
   public AsyncClientFactory(Injector injector) {
      this.injector = injector;
   }

   @SuppressWarnings("unchecked")
   public <T> T create(Class<T> clazz) {
      Key<AsyncRestClientProxy<T>> key = (Key<AsyncRestClientProxy<T>>) Key.get(TypeLiteral.get(newParameterizedType(
            AsyncRestClientProxy.class, clazz)));
      return newProxy(clazz, injector.getInstance(key));
   }

}
