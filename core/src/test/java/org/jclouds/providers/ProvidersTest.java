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
package org.jclouds.providers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.fail;

import java.util.NoSuchElementException;

import org.jclouds.apis.Balancer;
import org.jclouds.apis.Compute;
import org.jclouds.apis.Storage;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * The ProvidersTest tests the org.jclouds.providers.Providers class.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit", testName = "ProvidersTest")
public class ProvidersTest {

   private final JcloudsTestBlobStoreProviderMetadata testBlobstoreProvider = new JcloudsTestBlobStoreProviderMetadata();
   private final JcloudsTestComputeProviderMetadata testComputeProvider = new JcloudsTestComputeProviderMetadata();
   private final JcloudsTestYetAnotherComputeProviderMetadata testYetAnotherComputeProvider = new JcloudsTestYetAnotherComputeProviderMetadata();

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata;

      try {
         providerMetadata = Providers.withId("fake-id");
         fail("Looking for a provider with an id that doesn't exist should " + "throw an exception.");
      } catch (NoSuchElementException nsee) {
         ; // Expected
      }

      providerMetadata = Providers.withId(testBlobstoreProvider.getId());

      assertEquals(testBlobstoreProvider, providerMetadata);
      assertNotEquals(testBlobstoreProvider, testComputeProvider);
      assertNotEquals(testBlobstoreProvider, testYetAnotherComputeProvider);
   }

   @Test
   public void testTransformableTo() {
      Iterable<ProviderMetadata> providersMetadata = Providers.viewableAs(Storage.class);

      for (ProviderMetadata providerMetadata : providersMetadata) {
         assertEquals(testBlobstoreProvider, providerMetadata);
      }

      providersMetadata = Providers.viewableAs(Compute.class);

      for (ProviderMetadata providerMetadata : providersMetadata) {
         if (providerMetadata.getName().equals(testComputeProvider.getName())) {
            assertEquals(testComputeProvider, providerMetadata);
         } else {
            assertEquals(testYetAnotherComputeProvider, providerMetadata);
         }
      }

      providersMetadata = Providers.viewableAs(Balancer.class);

      assertEquals(false, providersMetadata.iterator().hasNext());
   }
   
   @Test
   public void testAll() {
      Iterable<ProviderMetadata> providersMetadata = Providers.all();

      for (ProviderMetadata providerMetadata : providersMetadata) {
         if (providerMetadata.getName().equals(testBlobstoreProvider.getName())) {
            assertEquals(testBlobstoreProvider, providerMetadata);
         } else if (providerMetadata.getName().equals(testComputeProvider.getName())) {
            assertEquals(testComputeProvider, providerMetadata);
         } else {
            assertEquals(testYetAnotherComputeProvider, providerMetadata);
         }
      }
   }

   @Test
   public void testBoundedByIso3166Code() {
      // Test filtering by ISO 3166 code alone
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-CA")), 2);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-FL")), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US")), 2);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("JP-13")), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("JP")), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("FAKE-CODE")), 0);

      // Test filtering by ISO 3166 code and type
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-CA", Storage.class)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-CA", Compute.class)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-FL", Storage.class)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-FL", Compute.class)), 0);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US", Storage.class)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US", Compute.class)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("FAKE-CODE", Storage.class)), 0);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("FAKE-CODE", Compute.class)), 0);
   }

   @Test
   public void testCollocatedWith() {
      // Test filtering by collocation alone
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider)), 0);

      // Test filtering by collocation and type
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider, Storage.class)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider, Compute.class)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider, Compute.class)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider, Storage.class)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider, Compute.class)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider, Storage.class)), 0);
   }

}
