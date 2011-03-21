/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.savvis.vpdc.features;

import static org.jclouds.savvis.vpdc.options.GetVAppOptions.Builder.withPowerState;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.VApp;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live")
public class BrowsingClientLiveTest extends BaseVPDCClientLiveTest {

   private BrowsingClient client;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getBrowsingClient();
   }

   @Test
   public void testOrg() throws Exception {
      for (Resource org : context.getApi().listOrgs()) {
         Org response = client.getOrg(org.getId());

         assertNotNull(response);
         assertNotNull(response.getId());
         assertNotNull(response.getHref());
         assertNotNull(response.getName());
         // savvis leaves this null for some reason
         assertEquals(response.getType(), null);
         assert response.getImages().size() >= 0;
         assert response.getDescription() != null;
         assert response.getVDCs().size() >= 1;
         assertEquals(client.getOrg(response.getId()).toString(), response.toString());
      }
   }

   @Test
   public void testVDC() throws Exception {
      for (Resource org1 : context.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC response = client.getVDCInOrg(org.getId(), vdc.getId());
            assertNotNull(response);
            assertNotNull(response.getId());
            assertNotNull(response.getHref());
            assertNotNull(response.getName());
            assertNotNull(response.getStatus());
            assertEquals(response.getType(), "application/vnd.vmware.vcloud.vdc+xml");
            assertNotNull(response.getDescription());
            assertNotNull(response.getResourceEntities());
            assertNotNull(response.getAvailableNetworks());
            assertEquals(client.getVDCInOrg(org.getId(), response.getId()).toString(), response.toString());
         }
      }
   }

   @Test
   public void testNetwork() throws Exception {
      for (Resource org1 : context.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = client.getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vApp : VDC.getAvailableNetworks()) {
               Network response = client.getNetworkInOrgAndVDC(org.getId(), vdc.getId(), vApp.getId());
               assertNotNull(response);
               assertNotNull(response.getId());
               assertNotNull(response.getHref());
               assertNotNull(response.getName());
               assertEquals(response.getType(), VCloudMediaType.NETWORK_XML);
               assertNotNull(response.getNetmask());
               assertNotNull(response.getGateway());
               assertNotNull(response.getInternalToExternalNATRules());
               assertEquals(client.getNetworkInOrgAndVDC(org.getId(), vdc.getId(), response.getId()).toString(),
                        response.toString());
            }
         }
      }
   }

   @Test
   public void testVApp() throws Exception {
      for (Resource org1 : context.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = client.getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vApp : Iterables.filter(VDC.getResourceEntities(), new Predicate<Resource>() {

               @Override
               public boolean apply(Resource arg0) {
                  return VCloudMediaType.VAPP_XML.equals(arg0.getType());
               }

            })) {
               VApp response = client.getVAppInOrgAndVDC(org.getId(), vdc.getId(), vApp.getId());
               assertNotNull(response);
               assertNotNull(response.getId());
               assertNotNull(response.getHref());
               assertNotNull(response.getName());
               assertEquals(response.getType(), "application/vnd.vmware.vcloud.vApp+xml");
               assertNotNull(response.getIpAddress());
               assertNotNull(response.getStatus());
               assertNotNull(response.getOsDescripton());
               assertNotNull(response.getOsType());
               assertNotNull(response.getNetworkSection());
               assertNotNull(response.getResourceAllocations());
               // power state is the only thing that should change
               assertEquals(client.getVAppInOrgAndVDC(org.getId(), vdc.getId(), response.getId(), withPowerState())
                        .toString().replaceFirst("status=[A-Z]+", ""), response.toString().replaceFirst(
                        "status=[A-Z]+", ""));
            }

         }
      }
   }
}