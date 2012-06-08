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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.features.FlavorClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code FlavorClient}
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "live", testName = "FlavorClientLiveTest")
public class FlavorClientLiveTest extends BaseNovaClientLiveTest {

   /**
    * Tests the listing of Flavors (getFlavor() is tested too!)
    * 
    * @throws Exception
    */
   @Test
   public void testListFlavors() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         FlavorClient client = novaContext.getApi().getFlavorClientForZone(zoneId);
         Set<Resource> response = client.listFlavors();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Resource flavor : response) {
            Flavor newDetails = client.getFlavor(flavor.getId());
            assertEquals(newDetails.getId(), flavor.getId());
            assertEquals(newDetails.getName(), flavor.getName());
            assertEquals(newDetails.getLinks(), flavor.getLinks());
         }
      }
   }

   /**
    * Tests the listing of Flavors in detail (getFlavor() is tested too!)
    * 
    * @throws Exception
    */
   @Test
   public void testListFlavorsInDetail() throws Exception {
      for (String zoneId : novaContext.getApi().getConfiguredZones()) {
         FlavorClient client = novaContext.getApi().getFlavorClientForZone(zoneId);
         Set<Flavor> response = client.listFlavorsInDetail();
         assert null != response;
         assertTrue(response.size() >= 0);
         for (Flavor flavor : response) {
            Flavor newDetails = client.getFlavor(flavor.getId());
            assertEquals(newDetails.getId(), flavor.getId());
            assertEquals(newDetails.getName(), flavor.getName());
            assertEquals(newDetails.getLinks(), flavor.getLinks());
            assertEquals(newDetails.getRam(), flavor.getRam());
            assertEquals(newDetails.getDisk(), flavor.getDisk());
            assertEquals(newDetails.getVcpus(), flavor.getVcpus());
         }
      }

   }

}
