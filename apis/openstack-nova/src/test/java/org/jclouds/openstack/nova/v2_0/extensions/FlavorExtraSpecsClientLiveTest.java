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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsClient;
import org.jclouds.openstack.nova.v2_0.features.FlavorClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * Tests behavior of FlavorExtraSpecsClient
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "FlavorExtraSpecsClientLiveTest", singleThreaded = true)
public class FlavorExtraSpecsClientLiveTest extends BaseNovaClientLiveTest {
   private FlavorClient flavorClient;
   private Optional<FlavorExtraSpecsClient> clientOption;
   private String zone;

   private Resource testFlavor;
   private Map<String, String> testSpecs = ImmutableMap.of("jclouds-test", "some data", "jclouds-test2", "more data!");

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      flavorClient = novaContext.getApi().getFlavorClientForZone(zone);
      clientOption = novaContext.getApi().getFlavorExtraSpecsExtensionForZone(zone);
   }

   @AfterGroups(groups = "live")
   @Override
   public void tearDown() {
      if (clientOption.isPresent() && testFlavor != null) {
         for(String key : testSpecs.keySet()) {
            assertTrue(clientOption.get().deleteExtraSpec(testFlavor.getId(), key));
         }
      }
      super.tearDown();
   }

   public void testCreateExtraSpecs() {
      if (clientOption.isPresent()) {
         FlavorExtraSpecsClient client = clientOption.get();
         testFlavor = Iterables.getLast(flavorClient.listFlavors());
         Map<String, String> before = client.getAllExtraSpecs(testFlavor.getId());
         assertNotNull(before);
         Map<String, String> specs = Maps.newHashMap(before);
         specs.putAll(testSpecs);
         assertTrue(client.setAllExtraSpecs(testFlavor.getId(), specs));
         assertEquals(client.getAllExtraSpecs(testFlavor.getId()), specs);
         for (Map.Entry<String, String> entry : specs.entrySet()) {
            assertEquals(client.getExtraSpec(testFlavor.getId(), entry.getKey()), entry.getValue());
         }
      }
   }

   @Test(dependsOnMethods = "testCreateExtraSpecs")
   public void testListExtraSpecs() {
      if (clientOption.isPresent()) {
         FlavorExtraSpecsClient client = clientOption.get();
         for (String key : testSpecs.keySet()) {
            assertTrue(client.getAllExtraSpecs(testFlavor.getId()).containsKey(key));
         }
         for (Resource flavor : flavorClient.listFlavors()) {
            Map<String, String> specs = client.getAllExtraSpecs(flavor.getId());
            assertNotNull(specs);
            for (Map.Entry<String, String> entry : specs.entrySet()) {
               assertEquals(client.getExtraSpec(flavor.getId(), entry.getKey()), entry.getValue());
            }
         }
      }
   }

   @Test(dependsOnMethods = "testCreateExtraSpecs")
   public void testTwiddleIndividualSpecs() {
      if (clientOption.isPresent()) {
         FlavorExtraSpecsClient client = clientOption.get();
         for (String key : testSpecs.keySet()) {
            assertTrue(client.setExtraSpec(testFlavor.getId(), key, "new value"));
         }
         for (String key : testSpecs.keySet()) {
            assertEquals(client.getExtraSpec(testFlavor.getId(), key), "new value");
         }
         for (Resource flavor : flavorClient.listFlavors()) {
            Map<String, String> specs = client.getAllExtraSpecs(flavor.getId());
            assertNotNull(specs);
            for (Map.Entry<String, String> entry : specs.entrySet()) {
               assertEquals(client.getExtraSpec(flavor.getId(), entry.getKey()), entry.getValue());
            }
         }
      }
   }
}
