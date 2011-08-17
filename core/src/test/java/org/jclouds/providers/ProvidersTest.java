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
package org.jclouds.providers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import com.google.common.collect.Iterables;

import java.util.NoSuchElementException;

import org.testng.annotations.Test;

/**
 * The ProvidersTest tests the org.jclouds.providers.Providers class.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test( groups = "unit" )
public class ProvidersTest {

   private final ProviderMetadata testBlobstoreProvider = new JcloudsTestBlobStoreProviderMetadata();
   private final ProviderMetadata testComputeProvider = new JcloudsTestComputeProviderMetadata();
   private final ProviderMetadata testYetAnotherComputeProvider = new JcloudsTestYetAnotherComputeProviderMetadata();

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata;
    
      try {
         providerMetadata = Providers.withId("fake-id");
         fail("Looking for a provider with an id that doesn't exist should " +
              "throw an exceptoin.");
      } catch (NoSuchElementException nsee) {
         ; // Expected
      }

      providerMetadata = Providers.withId(testBlobstoreProvider.getId());

      assertEquals(testBlobstoreProvider, providerMetadata);
   }

   @Test
   public void testOfType() {
      Iterable<ProviderMetadata> providersMetadata = Providers.ofType(ProviderMetadata.BLOBSTORE_TYPE);

      for (ProviderMetadata providerMetadata : providersMetadata) {
         assertEquals(testBlobstoreProvider, providerMetadata);
      }

      providersMetadata = Providers.ofType(ProviderMetadata.COMPUTE_TYPE);

      for (ProviderMetadata providerMetadata : providersMetadata) {
         if (providerMetadata.getName().equals(testComputeProvider.getName())) {
            assertEquals(testComputeProvider, providerMetadata);
         } else {
            assertEquals(testYetAnotherComputeProvider, providerMetadata);
         }
      }

      providersMetadata = Providers.ofType("fake-type");

      assertEquals(false, providersMetadata.iterator().hasNext());
   }

   @Test
   public void testAll() {
      Iterable<ProviderMetadata> providersMetadata = Providers.all();

      for (ProviderMetadata providerMetadata : providersMetadata) {
         if (providerMetadata.getName().equals(testBlobstoreProvider.getName())) {
            assertEquals(testBlobstoreProvider, providerMetadata);
         } else if (providerMetadata.getName().equals(testComputeProvider.getName())){
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
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-CA", ProviderMetadata.BLOBSTORE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-CA", ProviderMetadata.COMPUTE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-FL", ProviderMetadata.BLOBSTORE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US-FL", ProviderMetadata.COMPUTE_TYPE)), 0);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US", ProviderMetadata.BLOBSTORE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("US", ProviderMetadata.COMPUTE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("FAKE-CODE", ProviderMetadata.BLOBSTORE_TYPE)), 0);
      assertEquals(Iterables.size(Providers.boundedByIso3166Code("FAKE-CODE", ProviderMetadata.COMPUTE_TYPE)), 0);
   }

   @Test
   public void testCollocatedWith() {
      // Test filtering by collocation alone
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider)), 0);

      // Test filtering by collocation and type
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider, ProviderMetadata.BLOBSTORE_TYPE)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testBlobstoreProvider, ProviderMetadata.COMPUTE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider, ProviderMetadata.COMPUTE_TYPE)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testComputeProvider, ProviderMetadata.BLOBSTORE_TYPE)), 1);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider,
            ProviderMetadata.COMPUTE_TYPE)), 0);
      assertEquals(Iterables.size(Providers.collocatedWith(testYetAnotherComputeProvider,
            ProviderMetadata.BLOBSTORE_TYPE)), 0);
   }
}
