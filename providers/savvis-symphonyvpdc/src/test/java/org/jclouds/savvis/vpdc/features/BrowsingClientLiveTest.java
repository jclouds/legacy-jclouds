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
package org.jclouds.savvis.vpdc.features;

import static org.jclouds.savvis.vpdc.options.GetVMOptions.Builder.withPowerState;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Set;

import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.NetworkConnectionSection;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.jclouds.util.InetAddresses2;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;

@Test(groups = "live")
public class BrowsingClientLiveTest extends BaseVPDCClientLiveTest {

   private BrowsingClient client;

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = restContext.getApi().getBrowsingClient();
   }

   @Test
   public void testOrg() throws Exception {
      for (Resource org : restContext.getApi().listOrgs()) {
         Org response = client.getOrg(org.getId());

         assertNotNull(response);
         assertNotNull(response.getId());
         assertNotNull(response.getHref());
         assertNotNull(response.getName());
         // savvis leaves this null for some reason
         assertEquals(response.getType(), null);
         assert response.getImages().size() >= 0;
         assert response.getDescription() != null;
         assert response.getVDCs().size() >= 0;
         assertEquals(client.getOrg(response.getId()).toString(), response.toString());
      }
   }

   @Test
   public void testVDC() throws Exception {
      for (Resource org1 : restContext.getApi().listOrgs()) {
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
      for (Resource org1 : restContext.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            VDC VDC = client.getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vApp : VDC.getAvailableNetworks()) {
               Network response = client.getNetworkInVDC(org.getId(), vdc.getId(), vApp.getId());
               assertNotNull(response);
               assertNotNull(response.getId());
               assertNotNull(response.getHref());
               assertNotNull(response.getName());
               assertEquals(response.getType(), VCloudMediaType.NETWORK_XML);
               assertNotNull(response.getNetmask());
               assertNotNull(response.getGateway());
               assertNotNull(response.getInternalToExternalNATRules());
               assertEquals(client.getNetworkInVDC(org.getId(), vdc.getId(), response.getId()).toString(), response
                        .toString());
            }
         }
      }
   }

   // test for a single vm, as savvis response times are very slow. So if there are multiple vpdc's with numerous vm's,
   // test execution will invariably take a long time
   @Test
   public void testVM() throws Exception {
      for (Resource org1 : restContext.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         VDC_LOOP : for (Resource vdc : org.getVDCs()) {
            VDC VDC = client.getVDCInOrg(org.getId(), vdc.getId());
            for (Resource vApp : Iterables.filter(VDC.getResourceEntities(), new Predicate<Resource>() {

               @Override
               public boolean apply(Resource arg0) {
                  return VCloudMediaType.VAPP_XML.equals(arg0.getType());
               }

            })) {
               VM response = client.getVMInVDC(org.getId(), vdc.getId(), vApp.getId());
               assertNotNull(response);
               assertNotNull(response.getId());
               assertNotNull(response.getHref());
               assertNotNull(response.getName());
               assertEquals(response.getType(), "application/vnd.vmware.vcloud.vApp+xml");
               assert (response.getNetworkConnectionSections().size() > 0) : response;
               for (NetworkConnectionSection networkConnection : response.getNetworkConnectionSections())
                  assertNotNull(networkConnection.getIpAddress());
               assertNotNull(response.getStatus());
               assertNotNull(response.getOperatingSystemSection().getDescription());
               assertNotNull(response.getOperatingSystemSection().getId());
               assertNotNull(response.getNetworkSection());
               assertNotNull(response.getVirtualHardwareSections());
               // power state is the only thing that should change
               assertEquals(client.getVMInVDC(org.getId(), vdc.getId(), response.getId(), withPowerState()).toString()
                        .replaceFirst("status=[A-Z]+", ""), response.toString().replaceFirst("status=[A-Z]+", ""));

               // check one ip is valid
               String ip = Iterables.get(response.getNetworkConnectionSections(), 0).getIpAddress();
               assert HostSpecifier.isValid(ip) : response;
               if (InetAddresses2.isPrivateIPAddress(ip)) {
            	   // get public ip
                  ip = Iterables.get(response.getNetworkConfigSections(), 0).getInternalToExternalNATRules().get(ip);
                  // could be null
                  if(ip != null){
                	  assert HostSpecifier.isValid(ip) : response;
                  }
               }
               break VDC_LOOP;
            }
         }
      }
   }

   @Test
   public void testGetFirewallRules() throws Exception {
      for (Resource org1 : restContext.getApi().listOrgs()) {
         Org org = client.getOrg(org1.getId());
         for (Resource vdc : org.getVDCs()) {
            FirewallService response = client.listFirewallRules(org.getId(), vdc.getId());
            Set<FirewallRule> firewallRules = response.getFirewallRules();
            if (firewallRules != null) {
               Iterator<FirewallRule> iter = firewallRules.iterator();
               while (iter.hasNext()) {
                  FirewallRule firewallRule = iter.next();
                  assertNotNull(firewallRule);
                  // these are null for firewall rules
                  assertEquals(response.getHref(), null);
                  assertEquals(response.getType(), null);
                  assertNotNull(firewallRule.getFirewallType());
                  assertNotNull(firewallRule.getProtocol());
                  assertNotNull(firewallRule.getSource());
                  assertNotNull(firewallRule.getDestination());
               }
            }
         }
      }
   }
}