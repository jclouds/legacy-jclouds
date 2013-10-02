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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.byon.Node;
import org.jclouds.io.CopyInputStreamInputSupplierMap;
import org.jclouds.location.Provider;
import org.jclouds.util.Strings2;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.LoadingCache;
import com.google.common.io.InputSupplier;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class YamlNodeStoreModuleTest {
   Yaml yaml = createInjector().getInstance(Yaml.class);

   @DataProvider(name = "names")
   public Object[][] createData() {
      return new Object[][] { { "instance1", "bear" }, { "instance2", "apple" }, { "instance2", "francis" },
            { "instance4", "robot" } };
   }

   @Test(dataProvider = "names")
   public void deleteObject(String id, String name) throws InterruptedException, IOException {
      Injector injector = createInjector();
      Map<String, InputStream> map = getMap(injector);
      check(map, getStore(injector), "i-20312", id, name);
   }

   public void testProvidedMapWithValue() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      map.put("test", new ByteArrayInputStream("id: instance1\nname: instancename\n".getBytes()));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedConsistentAcrossRepeatedWrites() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      Injector injector = createInjectorWithProvidedMap(map);
      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Map<String, InputStream>>() {
      }, Names.named("yaml"))), map);
      LoadingCache<String, Node> store = getStore(injector);

      for (int i = 0; i < 10; i++)
         check(map, store, "test" + i, "instance1" + i, "instancename" + i);

   }

   public void testProvidedConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      put(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "instance1", "instancename");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testDefaultConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, InputStream> map = getMap(createInjector());

      put(map, getStore(createInjector()), "test", "instance1", "instancename");
      
      checkConsistent(map, getStore(createInjector()), "test", "instance1", "instancename");
      checkConsistent(map, getStore(createInjector()), "test", "instance1", "instancename");
      remove(map, getStore(createInjector()), "test");

   }

   protected LoadingCache<String, Node> getStore(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<LoadingCache<String, Node>>() {
      }));
   }

   protected Map<String, InputStream> getMap(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, InputStream>>() {
      }, Names.named("yaml")));
   }

   protected Injector createInjectorWithProvidedMap(Map<String, InputStream> map) {
      return Guice.createInjector(new YamlNodeStoreModule(map), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<InputStream>>() {
            }).annotatedWith(Provider.class).toInstance(Suppliers.<InputStream> ofInstance(null));
         }

      });
   }

   protected Injector createInjector() {
      return Guice.createInjector(new YamlNodeStoreModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<InputStream>>() {
            }).annotatedWith(Provider.class).toInstance(Suppliers.<InputStream> ofInstance(null));
         }

      });
   }

   protected void check(Map<String, InputStream> map, LoadingCache<String, Node> store, String key, String id, String name)
         throws IOException {
      put(map, store, key, id, name);
      checkConsistent(map, store, key, id, name);
      remove(map, store, key);
   }

   protected void remove(Map<String, InputStream> map, LoadingCache<String, Node> store, String key) {
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

   protected void checkConsistent(Map<String, InputStream> map, LoadingCache<String, Node> store, String key, String id,
         String name) throws IOException {
      assertEquals(map.size(), 1);
      if (store.size() == 0)
         store.getUnchecked(key);
      assertEquals(store.size(), 1);
      // checkRepeatedRead
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
      assertEquals(store.getUnchecked(key), Node.builder().id(id).name(name).build());
      // checkRepeatedRead
      checkToYaml(map, key, id, name);
      checkToYaml(map, key, id, name);
   }

   protected void checkToYaml(Map<String, InputStream> map, String key, String id, String name) throws IOException {
      assertEquals(Strings2.toStringAndClose(map.get(key)), String.format("id: %s\nname: %s\n", id, name));
   }

   protected void put(Map<String, InputStream> map, LoadingCache<String, Node> store, String key, String id, String name) {
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      map.put(key, new ByteArrayInputStream(String.format("id: %s\nname: %s\n", id, name).getBytes()));
      store.getUnchecked(key);
   }
}
