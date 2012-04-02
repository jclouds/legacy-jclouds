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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.logging.Logger;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit")
public abstract class BaseProviderMetadataTest {

   private final ProviderMetadata toTest;
   private final ApiType expectedType;
   private final ApiMetadata expectedApi;

   public BaseProviderMetadataTest(ProviderMetadata toTest, ApiMetadata expectedApi) {
      this.toTest = checkNotNull(toTest, "toTest must be defined");
      this.expectedApi = checkNotNull(expectedApi, "expectedApi must be defined");
      this.expectedType = expectedApi.getType();
   }

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata = Providers.withId(toTest.getId());

      assertEquals(toTest, providerMetadata);
      assert providerMetadata.getLinkedServices().contains(toTest.getId());
   }
   
   @Test
   public void testOfApiContains() {
      if (expectedApi == null)
         Logger.getAnonymousLogger().warning("please update your test class");
      ImmutableSet<ProviderMetadata> ofApi = ImmutableSet.copyOf(Providers.ofApi(expectedApi));
      assert ofApi.contains(toTest) : String.format("%s not found in %s", toTest, ofApi);
   }

   // it is ok to have multiple services in the same classpath (ex. ec2 vs elb)
   @Test
   public void testOfTypeContains() {
      ImmutableSet<ProviderMetadata> ofType = ImmutableSet.copyOf(Providers.ofType(expectedType));
      assert ofType.contains(toTest) : String.format("%s not found in %s", toTest, ofType);
   }

   @Test
   public void testAllContains() {
      ImmutableSet<ProviderMetadata> all = ImmutableSet.copyOf(Providers.all());
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

   @Test
   public void testInRestProperties() {
      Iterable<String> providers = org.jclouds.rest.Providers.getSupportedProviders();
      assert Iterables.contains(providers, toTest.getId()) : providers;
   }
}