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
package org.jclouds.providers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.Map;
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
      @SuppressWarnings("serial")
      Map<String, Integer> expectedResults = new HashMap<String, Integer>() {{
         put("US-CA", 2);
         put("US-FL", 1);
         put("US", 2);
         put("JP-13", 1);
         put("JP", 1);
         put("SOME-FAKE-CODE", 0);
      }};

      for (Map.Entry<String, Integer> result : expectedResults.entrySet()) {
         Iterable<ProviderMetadata> providersMetadata = Providers.boundedByIso3166Code(result.getKey());
         int providersFound = 0;

         for (ProviderMetadata providerMetadata : providersMetadata) {
            if (providerMetadata != null) {
               providersFound++;
            }
         }

         assertEquals(providersFound, result.getValue().intValue());
      }
   }

   @Test
   public void testCollocatedWith() {
      @SuppressWarnings("serial")
      Map<ProviderMetadata, Integer> expectedResults = new HashMap<ProviderMetadata, Integer>() {{
         put(testBlobstoreProvider, 1);
         put(testComputeProvider, 1);
         put(testYetAnotherComputeProvider, 0);
      }};

      for (Map.Entry<ProviderMetadata, Integer> result : expectedResults.entrySet()) {
         Iterable<ProviderMetadata> providersMetadata = Providers.collocatedWith(result.getKey());
         int providersFound = 0;

         for (ProviderMetadata providerMetadata : providersMetadata) {
            if (providerMetadata != null) {
               providersFound++;
            }
         }

         assertEquals(providersFound, result.getValue().intValue());
      }
   }
}
