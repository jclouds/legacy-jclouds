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

import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit")
public abstract class BaseProviderMetadataTest {
   protected Set<String> allTypes = ImmutableSet.of(ProviderMetadata.BLOBSTORE_TYPE, ProviderMetadata.COMPUTE_TYPE);
   private final ProviderMetadata toTest;
   private final String expectedType;

   public BaseProviderMetadataTest(ProviderMetadata toTest, String expectedType) {
      this.toTest = toTest;
      this.expectedType = expectedType;
   }

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata = Providers.withId(toTest.getId());

      assertEquals(toTest, providerMetadata);
      assert providerMetadata.getLinkedServices().contains(toTest.getId());
   }

   @Test
   public void testOfType() {
      assertEquals(Iterables.getOnlyElement(Providers.ofType(expectedType)), toTest);

      for (String type : Sets.difference(allTypes, ImmutableSet.of(expectedType)))
         assertEquals(Iterables.size(Providers.ofType(type)), 0);
   }

   @Test
   public void testAll() {
      Iterable<ProviderMetadata> providersMetadata = Providers.all();

      for (ProviderMetadata providerMetadata : providersMetadata) {
         assertEquals(toTest, providerMetadata);
      }
   }

   @Test
   public void testInRestProperties() {
      Iterable<String> providers = org.jclouds.rest.Providers.getSupportedProviders();
      assert Iterables.contains(providers, toTest.getId()) : providers;
   }
}