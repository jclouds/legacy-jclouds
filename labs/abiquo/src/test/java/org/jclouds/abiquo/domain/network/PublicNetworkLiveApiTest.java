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

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.network.PublicIpsDto;

/**
 * Live integration tests for the {@link PublicNetwork} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "PublicNetworkLiveApiTest")
public class PublicNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private PublicNetwork publicNetwork;

   @BeforeClass
   public void setupNetwork() {
      publicNetwork = createNetwork(env.publicNetwork, PREFIX + "-publicnetwork-test");
   }

   @AfterClass
   public void tearDownNetwork() {
      publicNetwork.delete();
   }

   public void testListIps() {
      PublicIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listPublicIps(publicNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<PublicIp> ips = publicNetwork.listIps();

      assertEquals(ips.size(), totalIps);
   }

   public void testListIpsWithOptions() {
      List<PublicIp> ips = publicNetwork.listIps(IpOptions.builder().limit(5).build());
      assertEquals(ips.size(), 5);
   }

   public void testListUnusedIps() {
      PublicIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listPublicIps(publicNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<PublicIp> ips = publicNetwork.listUnusedIps();
      assertEquals(ips.size(), totalIps);
   }

   public void testUpdateBasicInfo() {
      publicNetwork.setName("Public network Updated");
      publicNetwork.setPrimaryDNS("8.8.8.8");
      publicNetwork.setSecondaryDNS("8.8.8.8");
      publicNetwork.update();

      assertEquals(publicNetwork.getName(), "Public network Updated");
      assertEquals(publicNetwork.getPrimaryDNS(), "8.8.8.8");
      assertEquals(publicNetwork.getSecondaryDNS(), "8.8.8.8");

      // Refresh the public network
      PublicNetwork pn = env.datacenter.getNetwork(publicNetwork.getId()).toPublicNetwork();

      assertEquals(pn.getId(), publicNetwork.getId());
      assertEquals(pn.getName(), "Public network Updated");
      assertEquals(pn.getPrimaryDNS(), "8.8.8.8");
      assertEquals(pn.getSecondaryDNS(), "8.8.8.8");
   }

   public void testUpdateReadOnlyFields() {
      PublicNetwork toUpdate = createNetwork(publicNetwork, PREFIX + "-pubtoupdate-test");

      try {
         toUpdate.setTag(20);
         toUpdate.setAddress("80.81.81.0");
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
      PublicNetwork toUpdate = createNetwork(publicNetwork, PREFIX + "-pubtoupdate-test");

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

   public void testGetDatacenter() {
      assertEquals(publicNetwork.getDatacenter().getId(), env.datacenter.getId());
   }

   public void testGetNetworkFromIp() {
      PublicIp ip = publicNetwork.findIp(IpPredicates.<PublicIp> notUsed());
      PublicNetwork network = ip.getNetwork();

      assertEquals(network.getId(), publicNetwork.getId());
   }

   private PublicNetwork createNetwork(final PublicNetwork from, final String name) {
      PublicNetwork network = PublicNetwork.Builder.fromPublicNetwork(from).build();
      network.setName(name);
      network.save();
      assertNotNull(network.getId());
      return network;
   }
}
