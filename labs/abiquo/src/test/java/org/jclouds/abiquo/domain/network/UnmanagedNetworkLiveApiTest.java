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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.jclouds.abiquo.predicates.network.NetworkPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;

/**
 * Live integration tests for the {@link UnmanagedNetwork} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "UnmanagedNetworkLiveApiTest")
public class UnmanagedNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private UnmanagedNetwork unmanagedNetwork;

   @BeforeClass
   public void setupNetwork() {
      unmanagedNetwork = createNetwork(env.unmanagedNetwork, PREFIX + "-unmanagednetwork-test");
   }

   @AfterClass
   public void tearDownNetwork() {
      unmanagedNetwork.delete();
   }

   public void testListIps() {
      UnmanagedIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listUnmanagedIps(unmanagedNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<UnmanagedIp> ips = unmanagedNetwork.listIps();

      assertEquals(ips.size(), totalIps);
   }

   public void testListIpsWithOptions() {
      List<UnmanagedIp> ips = unmanagedNetwork.listIps(IpOptions.builder().limit(5).build());
      // Unmanaged networks do not have IPs until attached to VMs
      assertEquals(ips.size(), 0);
   }

   public void testListUnusedIps() {
      UnmanagedIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listUnmanagedIps(unmanagedNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<UnmanagedIp> ips = unmanagedNetwork.listUnusedIps();
      assertEquals(ips.size(), totalIps);
   }

   public void testUpdateBasicInfo() {
      unmanagedNetwork.setName("Unmanaged network Updated");
      unmanagedNetwork.setPrimaryDNS("8.8.8.8");
      unmanagedNetwork.setSecondaryDNS("8.8.8.8");
      unmanagedNetwork.update();

      assertEquals(unmanagedNetwork.getName(), "Unmanaged network Updated");
      assertEquals(unmanagedNetwork.getPrimaryDNS(), "8.8.8.8");
      assertEquals(unmanagedNetwork.getSecondaryDNS(), "8.8.8.8");

      // Refresh the unmanaged network
      UnmanagedNetwork en = env.enterprise.findUnmanagedNetwork(env.datacenter,
            NetworkPredicates.<UnmanagedIp> name(unmanagedNetwork.getName()));

      assertEquals(en.getId(), unmanagedNetwork.getId());
      assertEquals(en.getName(), "Unmanaged network Updated");
      assertEquals(en.getPrimaryDNS(), "8.8.8.8");
      assertEquals(en.getSecondaryDNS(), "8.8.8.8");
   }

   public void testUpdateReadOnlyFields() {
      UnmanagedNetwork toUpdate = createNetwork(unmanagedNetwork, PREFIX + "-umtoupdate-test");

      try {
         toUpdate.setTag(20);
         toUpdate.setAddress("10.2.0.0");
         toUpdate.setMask(16);
         toUpdate.update();

         fail("Tag field should not be editable");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "VLAN-19");
      } finally {
         toUpdate.delete();
      }
   }

   public void testUpdateWithInvalidValues() {
      UnmanagedNetwork toUpdate = createNetwork(unmanagedNetwork, PREFIX + "-umtoupdate-test");

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

   public void testGetEnterprise() {
      assertEquals(unmanagedNetwork.getEnterprise().getId(), env.enterprise.getId());
   }

   public void testGetDatacenter() {
      assertEquals(unmanagedNetwork.getDatacenter().getId(), env.datacenter.getId());
   }

   public void testGetNetworkFromIp() {
      UnmanagedIp ip = unmanagedNetwork.findIp(IpPredicates.<UnmanagedIp> notUsed());
      // Unmanaged networks do not have IPs until attached to VMs
      assertNull(ip);
   }

   private UnmanagedNetwork createNetwork(final UnmanagedNetwork from, final String name) {
      UnmanagedNetwork network = UnmanagedNetwork.Builder.fromUnmanagedNetwork(from).build();
      network.setName(name);
      network.save();
      assertNotNull(network.getId());
      return network;
   }
}
