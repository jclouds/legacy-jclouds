/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.config;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterEntries;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class MappedHttpInvocationModuleTest {
   static interface Sync {
      String get();
   }

   private static interface Async {
      ListenableFuture<String> get();
   }

   public void testPutInvokablesWhenInterfacesMatch() {
      Cache<Invokable<?, ?>, Invokable<?, ?>> cache = CacheBuilder.newBuilder().build();
      SyncToAsyncHttpInvocationModule.putInvokables(Sync.class, Async.class, cache);

      assertEquals(cache.size(), 1);

      Invokable<?, ?> sync = cache.asMap().keySet().iterator().next();
      assertEquals(sync.getOwnerType().getRawType(), Sync.class);
      assertEquals(sync.getName(), "get");
      assertEquals(sync.getReturnType(), TypeToken.of(String.class));

      Invokable<?, ?> async = cache.getIfPresent(sync);
      assertEquals(async.getOwnerType().getRawType(), Async.class);
      assertEquals(async.getName(), "get");
      assertEquals(async.getReturnType(), new TypeToken<ListenableFuture<String>>() {
         private static final long serialVersionUID = 1L;
      });
   }

   private static interface AsyncWithException {
      ListenableFuture<String> get() throws IOException;
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".* has different typed exceptions than target .*")
   public void testPutInvokablesWhenInterfacesMatchExceptExceptions() {
      Cache<Invokable<?, ?>, Invokable<?, ?>> cache = CacheBuilder.newBuilder().build();
      SyncToAsyncHttpInvocationModule.putInvokables(Sync.class, AsyncWithException.class, cache);
   }

   private static interface AsyncWithMisnamedMethod {
      ListenableFuture<String> got();
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "no such method .*")
   public void testPutInvokablesWhenTargetMethodNotFound() {
      Cache<Invokable<?, ?>, Invokable<?, ?>> cache = CacheBuilder.newBuilder().build();
      SyncToAsyncHttpInvocationModule.putInvokables(Sync.class, AsyncWithMisnamedMethod.class, cache);
   }

   static final Predicate<Entry<Invokable<?, ?>, Invokable<?, ?>>> isHttpInvokable = new Predicate<Map.Entry<Invokable<?, ?>, Invokable<?, ?>>>() {
      public boolean apply(Map.Entry<Invokable<?, ?>, Invokable<?, ?>> in) {
         return in.getKey().getOwnerType().getRawType().equals(HttpClient.class)
               && in.getValue().getOwnerType().getRawType().equals(HttpAsyncClient.class);
      }
   };

   public void testSeedKnownSync2AsyncIncludesHttpClientByDefault() {
      Map<Invokable<?, ?>, Invokable<?, ?>> cache = SyncToAsyncHttpInvocationModule.seedKnownSync2AsyncInvokables(
            ImmutableMap.<Class<?>, Class<?>> of()).asMap();

      assertEquals(cache.size(), 6);
      assertEquals(filterEntries(cache, isHttpInvokable), cache);
   }

   public void testSeedKnownSync2AsyncInvokablesInterfacesMatch() {
      Map<Invokable<?, ?>, Invokable<?, ?>> cache = SyncToAsyncHttpInvocationModule.seedKnownSync2AsyncInvokables(
            ImmutableMap.<Class<?>, Class<?>> of(Sync.class, Async.class)).asMap();

      assertEquals(cache.size(), 7);

      cache = filterEntries(cache, not(isHttpInvokable));

      assertEquals(cache.size(), 1);
   }

}
