/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.util.Utils.toStringAndClose;
import static org.testng.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.crypto.PemsTest;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.rest.config.CredentialStoreModule.CopyInputStreamInputSupplierMap;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.io.InputSupplier;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "rest.CredentialStoreModuleTest")
public class CredentialStoreModuleTest {
   Json json = createInjector().getInstance(Json.class);

   @DataProvider(name = "credentials")
   public Object[][] createData() {
      return new Object[][] { { "root", PemsTest.PRIVATE_KEY }, { "identity", "Base64==" },
            { "user@domain", "pa$sw@rd" }, { "user", "unic₪de" } };
   }

   @Test(dataProvider = "credentials")
   public void deleteObject(String identity, String credential) throws InterruptedException, IOException {
      Injector injector = createInjector();
      Map<String, InputStream> map = getMap(injector);
      check(map, getStore(injector), "i-20312", identity, credential);
   }

   public void testProvidedMapWithValue() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      map.put("test", new ByteArrayInputStream(json.toJson(new Credentials("user", "pass")).getBytes()));
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "user", "pass");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "user", "pass");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testProvidedConsistentAcrossRepeatedWrites() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      Injector injector = createInjectorWithProvidedMap(map);
      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Map<String, InputStream>>() {
      })), map);
      Map<String, Credentials> store = getStore(injector);

      for (int i = 0; i < 10; i++)
         check(map, store, "test" + i, "user" + i, "pass" + i);

   }

   public void testProvidedConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, InputStream> map = new CopyInputStreamInputSupplierMap(
            new ConcurrentHashMap<String, InputSupplier<InputStream>>());

      put(map, getStore(createInjectorWithProvidedMap(map)), "test", "user", "pass");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "user", "pass");
      checkConsistent(map, getStore(createInjectorWithProvidedMap(map)), "test", "user", "pass");
      remove(map, getStore(createInjectorWithProvidedMap(map)), "test");

   }

   public void testDefaultConsistentAcrossMultipleInjectors() throws IOException {
      Map<String, InputStream> map = getMap(createInjector());

      put(map, getStore(createInjector()), "test", "user", "pass");
      checkConsistent(map, getStore(createInjector()), "test", "user", "pass");
      checkConsistent(map, getStore(createInjector()), "test", "user", "pass");
      remove(map, getStore(createInjector()), "test");

   }

   protected Map<String, Credentials> getStore(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, Credentials>>() {
      }));
   }

   protected Map<String, InputStream> getMap(Injector injector) {
      return injector.getInstance(Key.get(new TypeLiteral<Map<String, InputStream>>() {
      }));
   }

   protected Injector createInjectorWithProvidedMap(Map<String, InputStream> map) {
      return Guice.createInjector(new CredentialStoreModule(map), new GsonModule());
   }

   protected Injector createInjector() {
      return Guice.createInjector(new CredentialStoreModule(), new GsonModule());
   }

   protected void check(Map<String, InputStream> map, Map<String, Credentials> store, String key, String identity,
         String credential) throws IOException {
      put(map, store, key, identity, credential);
      checkConsistent(map, store, key, identity, credential);
      remove(map, store, key);
   }

   protected void remove(Map<String, InputStream> map, Map<String, Credentials> store, String key) {
      store.remove(key);
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      assertEquals(store.get(key), null);
      assertEquals(map.get(key), null);
   }

   protected void checkConsistent(Map<String, InputStream> map, Map<String, Credentials> store, String key,
         String identity, String credential) throws IOException {
      assertEquals(store.size(), 1);
      assertEquals(map.size(), 1);
      // checkRepeatedRead
      assertEquals(store.get(key), new Credentials(identity, credential));
      assertEquals(store.get(key), new Credentials(identity, credential));
      // checkRepeatedRead
      checkToJson(map, key, identity, credential);
      checkToJson(map, key, identity, credential);
   }

   protected void checkToJson(Map<String, InputStream> map, String key, String identity, String credential)
         throws IOException {
      assertEquals(toStringAndClose(map.get(key)), json.toJson(new Credentials(identity, credential)));
   }

   protected void put(Map<String, InputStream> map, Map<String, Credentials> store, String key, String identity,
         String credential) {
      assertEquals(store.size(), 0);
      assertEquals(map.size(), 0);
      store.put(key, new Credentials(identity, credential));
   }
}