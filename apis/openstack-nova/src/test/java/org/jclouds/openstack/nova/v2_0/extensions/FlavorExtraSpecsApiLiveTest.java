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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests behavior of FlavorExtraSpecsApi
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "FlavorExtraSpecsApiLiveTest", singleThreaded = true)
public class FlavorExtraSpecsApiLiveTest extends BaseNovaApiLiveTest {
   private FlavorApi flavorApi;
   private Optional<? extends FlavorExtraSpecsApi> apiOption;
   private String zone;

   private Resource testFlavor;
   private Map<String, String> testSpecs = ImmutableMap.of("jclouds-test", "some data", "jclouds-test2", "more data!");

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      flavorApi = api.getFlavorApiForZone(zone);
      apiOption = api.getFlavorExtraSpecsExtensionForZone(zone);
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (apiOption.isPresent() && testFlavor != null) {
         for(String key : testSpecs.keySet()) {
            assertTrue(apiOption.get().deleteMetadataKey(testFlavor.getId(), key));
         }
      }
      super.tearDown();
   }

   public void testCreateExtraSpecs() {
      if (apiOption.isPresent()) {
         FlavorExtraSpecsApi api = apiOption.get();
         testFlavor = Iterables.getLast(flavorApi.list().concat());
         Map<String, String> before = api.getMetadata(testFlavor.getId());
         assertNotNull(before);
         Map<String, String> specs = Maps.newHashMap(before);
         specs.putAll(testSpecs);
         assertTrue(api.updateMetadata(testFlavor.getId(), specs));
         assertEquals(api.getMetadata(testFlavor.getId()), specs);
         for (Map.Entry<String, String> entry : specs.entrySet()) {
            assertEquals(api.getMetadataKey(testFlavor.getId(), entry.getKey()), entry.getValue());
         }
      }
   }

   @Test(dependsOnMethods = "testCreateExtraSpecs")
   public void testListExtraSpecs() {
      if (apiOption.isPresent()) {
         FlavorExtraSpecsApi api = apiOption.get();
         for (String key : testSpecs.keySet()) {
            assertTrue(api.getMetadata(testFlavor.getId()).containsKey(key));
         }
         for (Resource flavor : flavorApi.list().concat()) {
            Map<String, String> specs = api.getMetadata(flavor.getId());
            assertNotNull(specs);
            for (Map.Entry<String, String> entry : specs.entrySet()) {
               assertEquals(api.getMetadataKey(flavor.getId(), entry.getKey()), entry.getValue());
            }
         }
      }
   }

   @Test(dependsOnMethods = "testCreateExtraSpecs")
   public void testTwiddleIndividualSpecs() {
      if (apiOption.isPresent()) {
         FlavorExtraSpecsApi api = apiOption.get();
         for (String key : testSpecs.keySet()) {
            assertTrue(api.updateMetadataEntry(testFlavor.getId(), key, "new value"));
         }
         for (String key : testSpecs.keySet()) {
            assertEquals(api.getMetadataKey(testFlavor.getId(), key), "new value");
         }
         for (Resource flavor : flavorApi.list().concat()) {
            Map<String, String> specs = api.getMetadata(flavor.getId());
            assertNotNull(specs);
            for (Map.Entry<String, String> entry : specs.entrySet()) {
               assertEquals(api.getMetadataKey(flavor.getId(), entry.getKey()), entry.getValue());
            }
         }
      }
   }
}
