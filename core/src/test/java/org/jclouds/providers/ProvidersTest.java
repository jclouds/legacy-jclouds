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
         assertEquals(testComputeProvider, providerMetadata);
      }

      providersMetadata = Providers.ofType("fake-type");

      assertEquals(false, providersMetadata.iterator().hasNext());
   }

   @Test
   public void testAll() {
      Iterable<ProviderMetadata> providersMetadata = Providers.all();

      for (ProviderMetadata providerMetadata : providersMetadata) {
         if (providerMetadata.getName().equals("Test Blobstore Provider")) {
            assertEquals(testBlobstoreProvider, providerMetadata);
         } else {
            assertEquals(testComputeProvider, providerMetadata);
         }
      }
   }

}
