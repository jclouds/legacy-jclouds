/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.network;

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;

/**
 * Live integration tests for the {@link PrivateNetwork} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "PrivateNetworkLiveApiTest")
public class PrivateNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private PrivateNetwork privateNetwork;

   @BeforeClass
   public void setupNetwork() {
      privateNetwork = createNetwork(env.virtualDatacenter, env.privateNetwork, PREFIX + "-privatenetwork-test");
   }

   @AfterClass
   public void tearDownNetwork() {
      privateNetwork.delete();
   }

   public void testListIps() {
      PrivateIpsDto ipsDto = env.context.getApiContext().getApi().getCloudApi()
            .listPrivateNetworkIps(privateNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<PrivateIp> ips = privateNetwork.listIps();

      assertEquals(ips.size(), totalIps);
   }

   public void testListIpsWithOptions() {
      List<PrivateIp> ips = privateNetwork.listIps(IpOptions.builder().limit(5).build());
      assertEquals(ips.size(), 5);
   }

   public void testListUnusedIps() {
      PrivateIpsDto ipsDto = env.context.getApiContext().getApi().getCloudApi()
            .listPrivateNetworkIps(privateNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<PrivateIp> ips = privateNetwork.listUnusedIps();
      assertEquals(ips.size(), totalIps);
   }

   public void testUpdateBasicInfo() {
      privateNetwork.setName("Private network Updated");
      privateNetwork.setPrimaryDNS("8.8.8.8");
      privateNetwork.setSecondaryDNS("8.8.8.8");
      privateNetwork.update();

      assertEquals(privateNetwork.getName(), "Private network Updated");
      assertEquals(privateNetwork.getPrimaryDNS(), "8.8.8.8");
      assertEquals(privateNetwork.getSecondaryDNS(), "8.8.8.8");

      // Refresh the private network
      PrivateNetwork pn = env.virtualDatacenter.getPrivateNetwork(privateNetwork.getId());

      assertEquals(pn.getName(), "Private network Updated");
      assertEquals(pn.getPrimaryDNS(), "8.8.8.8");
      assertEquals(pn.getSecondaryDNS(), "8.8.8.8");
   }

   public void testUpdateReadOnlyFields() {
      PrivateNetwork toUpdate = createNetwork(env.virtualDatacenter, privateNetwork, PREFIX + "-privtoupdate-test");

      try {
         toUpdate.setTag(20);
         toUpdate.setAddress("10.1.1.0");
         toUpdate.setMask(16);
         toUpdate.update();

         fail("Tag field should not be editable");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "VLAN-10");
      } finally {
         toUpdate.delete();
      }
   }

   public void testUpdateWithInvalidValues() {
      PrivateNetwork toUpdate = createNetwork(env.virtualDatacenter, privateNetwork, PREFIX + "-privtoupdate-test");

      try {
         toUpdate.setMask(60);
         toUpdate.update();

         fail("Invalid mask value");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.BAD_REQUEST, "CONSTR-MAX");
      } finally {
         toUpdate.delete();
      }
   }

   public void testGetNetworkFromIp() {
      PrivateIp ip = privateNetwork.findIp(IpPredicates.<PrivateIp> notUsed());
      PrivateNetwork network = ip.getNetwork();

      assertEquals(network.getId(), privateNetwork.getId());
   }

   private PrivateNetwork createNetwork(final VirtualDatacenter vdc, final PrivateNetwork from, final String name) {
      PrivateNetwork network = PrivateNetwork.Builder.fromPrivateNetwork(from).virtualDatacenter(vdc).build();
      network.setName(name);
      network.save();
      assertNotNull(network.getId());
      return network;
   }
}
