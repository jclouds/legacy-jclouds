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
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

/**
 * The AWSEC2ProviderTest tests the org.jclouds.providers.AWSEC2Provider class.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test( groups = "unit" )
public class AWSEC2ProviderTest {

   private final ProviderMetadata awsEc2ProviderMetadata = new AWSEC2ProviderMetadata();

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata = Providers.withId(awsEc2ProviderMetadata.getId());

      assertEquals(awsEc2ProviderMetadata, providerMetadata);
   }

   @Test
   public void testOfType() {
      Iterable<ProviderMetadata> providersMetadata = Providers.ofType(ProviderMetadata.COMPUTE_TYPE);

      for (ProviderMetadata providerMetadata : providersMetadata) {
         assertEquals(awsEc2ProviderMetadata, providerMetadata);
      }

      providersMetadata = Providers.ofType(ProviderMetadata.BLOBSTORE_TYPE);

      assertFalse(providersMetadata.iterator().hasNext());
   }

   @Test
   public void testAll() {
      Iterable<ProviderMetadata> providersMetadata = Providers.all();

      for (ProviderMetadata providerMetadata : providersMetadata) {
         assertEquals(awsEc2ProviderMetadata, providerMetadata);
      }
   }

}