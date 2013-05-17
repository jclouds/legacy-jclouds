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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link FlavorApi}
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "live", testName = "FlavorApiLiveTest")
public class FlavorApiLiveTest extends BaseNovaApiLiveTest {

   /**
    * Tests the listing of Flavors.
    * 
    * @throws Exception
    */
   @Test(description = "GET /v${apiVersion}/{tenantId}/flavors")
   public void testListFlavors() throws Exception {
      for (String zoneId : zones) {
         FlavorApi flavorApi = api.getFlavorApiForZone(zoneId);
         Set<? extends Resource> response = flavorApi.list().concat().toSet();
         assertNotNull(response);
         assertFalse(response.isEmpty());
         for (Resource flavor : response) {
            assertNotNull(flavor.getId());
            assertNotNull(flavor.getName());
            assertNotNull(flavor.getLinks());
         }
      }
   }

   /**
    * Tests the listing of Flavors in detail.
    * 
    * @throws Exception
    */
   @Test(description = "GET /v${apiVersion}/{tenantId}/flavors/detail")
   public void testListFlavorsInDetail() throws Exception {
      for (String zoneId : zones) {
         FlavorApi flavorApi = api.getFlavorApiForZone(zoneId);
         Set<? extends Flavor> response = flavorApi.listInDetail().concat().toSet();
         assertNotNull(response);
         assertFalse(response.isEmpty());
         for (Flavor flavor : response) {
             assertNotNull(flavor.getId());
             assertNotNull(flavor.getName());
             assertNotNull(flavor.getLinks());
             assertTrue(flavor.getRam() > 0);
             assertTrue(flavor.getDisk() > 0);
             assertTrue(flavor.getVcpus() > 0);
         }
      }
   }

   /**
    * Tests getting Flavors by id.
    * 
    * @throws Exception
    */
   @Test(description = "GET /v${apiVersion}/{tenantId}/flavors/{id}", dependsOnMethods = { "testListFlavorsInDetail" })
   public void testGetFlavorById() throws Exception {
      for (String zoneId : zones) {
         FlavorApi flavorApi = api.getFlavorApiForZone(zoneId);
         Set<? extends Flavor> response = flavorApi.listInDetail().concat().toSet();
         for (Flavor flavor : response) {
            Flavor details = flavorApi.get(flavor.getId());
            assertNotNull(details);
            assertEquals(details.getId(), flavor.getId());
            assertEquals(details.getName(), flavor.getName());
            assertEquals(details.getLinks(), flavor.getLinks());
            assertEquals(details.getRam(), flavor.getRam());
            assertEquals(details.getDisk(), flavor.getDisk());
            assertEquals(details.getVcpus(), flavor.getVcpus());
         }
      }
   }

}
