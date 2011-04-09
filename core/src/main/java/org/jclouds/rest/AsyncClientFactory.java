/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest;

import java.lang.reflect.Proxy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.internal.AsyncRestClientProxy;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

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
      return (T) create(clazz, (AsyncRestClientProxy<T>) injector.getInstance(Key.get(TypeLiteral
               .get(Types.newParameterizedType(AsyncRestClientProxy.class, clazz)))));
   }

   @SuppressWarnings("unchecked")
   public static <T> T create(Class<T> clazz, AsyncRestClientProxy<T> proxy) {
      return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, proxy);
   }
}
