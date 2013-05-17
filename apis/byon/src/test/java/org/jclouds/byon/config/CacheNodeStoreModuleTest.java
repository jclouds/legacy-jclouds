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
package org.jclouds.byon.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jclouds.byon.Node;
import org.jclouds.location.Provider;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "CacheNodeStoreModuleTest")
public class CacheNodeStoreModuleTest {

   @DataProvider(name = "names")
   public Object[][] createData() {
      return new Object[][] { { "instance1", "bear" }, { "instance2", "apple" }, { "instance2", "francis" },
            { "instance4", "robot" } };
   }

   public void testProvidedMapWithValue() throws IOException {
      Map<String, Node> map = Maps.newConcurrentMap();

      map.put("test", Node.builder().id("instance1").name("instancename").build());
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedConsistentAcrossRepeatedWrites() throws IOException {
      Map<String, Node> map = Maps.newConcurrentMap();

      Injector injector = createInjectorWithProvidedMap(map);
      assertEquals(injector.getInstance(Key.get(new TypeLiteral<LoadingCache<String, Node>>() {
      })).asMap(), map);
      LoadingCache<String, Node> store = getStore(injector);

      for (int i = 0; i < 10; i++)
         check(map, store, "test" + i, "instance1" + i, "instancename" + i);

   }

   public void testProvidedConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, Node> map = Maps.newConcurrentMap();

      put(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedCacheConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, Node> map = Maps.newConcurrentMap();

      LoadingCache<String, Node> cache = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(map)));

      put(map, getStore(createInjectorWithProvidedCache(cache)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedCache(cache)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedCache(cache)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedCache(cache)), "test");

   }

   private LoadingCache<String, Node> getStore(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<LoadingCache<String, Node>>() {
      }));
   }

   private Injector createInjectorWithProvidedMap(Map<String, Node> map) {
      return Guice.createInjector(new CacheNodeStoreModule(map), new AbstractModule() {

         @Override
         public void configure() {
            bind(new TypeLiteral<Supplier<InputStream>>() {
            }).annotatedWith(Provider.class).toInstance(Suppliers.<InputStream> ofInstance(null));
         }

      });
   }

   private Injector createInjectorWithProvidedCache(LoadingCache<String, Node> cache) {
      return Guice.createInjector(new CacheNodeStoreModule(cache), new AbstractModule() {

         @Override
         public void configure() {
            bind(new TypeLiteral<Supplier<InputStream>>() {
            }).annotatedWith(Provider.class).toInstance(Suppliers.<InputStream> ofInstance(null));
         }

      });
   }

   private void check(Map<String, Node> map, LoadingCache<String, Node> store, String key, String id, String name)
         throws IOException {
      put(map, store, key, id, name);
      checkConsistent(map, store, key, id, name);
      remove(map, store, key);
   }

   private void remove(Map<String, Node> map, LoadingCache<String, Node> store, String key) {
      store.invalidate(key);
      assertEquals(store.size(), 0);
      map.remove(key);
      assertEquals(map.size(), 0);
      try {
         assertEquals(store.getUnchecked(key), null);
         fail("should not work as null is invalid");
      } catch (UncheckedExecutionException e) {

      }
      assertEquals(map.get(key), null);
   }

   private void checkConsistent(Map<String, Node> map, LoadingCache<String, Node> store, String key, String id, String name)
         throws IOException {
      assertEquals(map.size(), 1);
      if (store.size() == 0)
         store.getUnchecked(key);
      assertEquals(store.size(), 1);
      // checkRepeatedRead
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
   }

   private void put(Map<String, Node> map, LoadingCache<String, Node> store, String key, String id, String name) {
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      map.put(key, Node.builder().id(id).name(name).build());
      store.getUnchecked(key);
   }
}
