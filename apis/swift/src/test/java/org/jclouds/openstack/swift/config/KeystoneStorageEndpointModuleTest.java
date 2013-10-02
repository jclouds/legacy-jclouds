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
package org.jclouds.openstack.swift.config;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.KeystoneStorageEndpointModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

@Test(groups = "unit")
public class KeystoneStorageEndpointModuleTest {

   private final String apiVersion = "9.8.7";
   private final RegionIdToURISupplier.Factory mockFactory = createStrictMock(RegionIdToURISupplier.Factory.class);
   private final RegionIdToURISupplier mockSupplier = createStrictMock(RegionIdToURISupplier.class);

   /**
    * Setup the expectations for our mock factory to return 3 region urls keyed
    * by test region names
    */
   @BeforeTest
   public void setup() {
      Map<String, Supplier<URI>> endpoints = new HashMap<String, Supplier<URI>>();

      try {
         endpoints.put("region1", Suppliers.ofInstance(new URI("http://region1.example.org/")));
         endpoints.put("region2", Suppliers.ofInstance(new URI("http://region2.example.org/")));
         endpoints.put("region3", Suppliers.ofInstance(new URI("http://region3.example.org/")));
      } catch (URISyntaxException ex) {
         fail("static test Strings do not parse to URI: " + ex.getMessage());
      }

      expect(mockSupplier.get())
         .andReturn(endpoints)
         .anyTimes();
      expect(mockFactory.createForApiTypeAndVersion(ServiceType.OBJECT_STORE,apiVersion))
         .andReturn(mockSupplier)
         .anyTimes();

      replay(mockSupplier);
      replay(mockFactory);
   }

   /**
    * Test that specifying an empty region will return an arbitrary URL
    */
   @Test
   public void testEmptyRegion() {
      final KeystoneStorageEndpointModule moduleToTest = new KeystoneStorageEndpointModule();

      // Test with an empty Region - just ensure we get either a region 1,2 or 3
      // URI
      Supplier<URI> resultingSupplier = moduleToTest.provideStorageUrl(mockFactory, apiVersion, "");
      assertNotNull(resultingSupplier);
      URI resultingUri = resultingSupplier.get();
      assertNotNull(resultingUri);

      // Without a region our choice is arbitrary. We can't enforce an ordering
      // on the map
      // as that varies from JVM to JVM - easier to just assume its one of the
      // possible values
      assertTrue(resultingUri.toString().equals("http://region1.example.org/")
            || resultingUri.toString().equals("http://region2.example.org/")
            || resultingUri.toString().equals("http://region3.example.org/"));
   }

   /**
    * Test that specifying a region will return the correct URL
    */
   @Test
   public void testSpecificRegion() {
      final KeystoneStorageEndpointModule moduleToTest = new KeystoneStorageEndpointModule();

      // Iterate through our region names
      for (int i = 1; i <= 3; i++) {
         Supplier<URI> resultingSupplier = moduleToTest.provideStorageUrl(mockFactory, apiVersion, String.format("region%1$s", i));
         assertNotNull(resultingSupplier);
         URI resultingUri = resultingSupplier.get();
         assertNotNull(resultingUri);

         assertEquals(resultingUri.toString(),
               String.format("http://region%1$s.example.org/", i));
      }
   }

   /**
    * Test that specifying an undefined region will return null
    */
   @Test
   public void testUndefinedRegion() {
      final KeystoneStorageEndpointModule moduleToTest = new KeystoneStorageEndpointModule();

      Supplier<URI> resultingSupplier = moduleToTest.provideStorageUrl(mockFactory, apiVersion, "region-that-dne");
      assertNotNull(resultingSupplier);
      URI resultingUri = resultingSupplier.get();
      assertNull(resultingUri);
   }
}
