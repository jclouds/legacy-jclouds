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

import org.jclouds.rest.AsyncClientFactory;

import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AsyncClientProvider<A> implements Provider<A> {
   @Inject
   Injector injector;
   private final Class<?> asyncClientType;

   @Inject
   AsyncClientProvider(Class<?> asyncClientType) {
      this.asyncClientType = asyncClientType;
   }

   @Override
   @Singleton
   public A get() {
      return (A) injector.getInstance(AsyncClientFactory.class).create(asyncClientType);
   }

}
