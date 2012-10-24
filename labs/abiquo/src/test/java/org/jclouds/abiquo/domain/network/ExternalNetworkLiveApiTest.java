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
import org.jclouds.abiquo.predicates.network.NetworkPredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;

/**
 * Live integration tests for the {@link ExternalNetwork} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ExternalNetworkLiveApiTest")
public class ExternalNetworkLiveApiTest extends BaseAbiquoApiLiveApiTest {
   private ExternalNetwork externalNetwork;

   @BeforeClass
   public void setupNetwork() {
      externalNetwork = createNetwork(env.externalNetwork, PREFIX + "-externalnetwork-test");
   }

   @AfterClass
   public void tearDownNetwork() {
      externalNetwork.delete();
   }

   public void testListIps() {
      ExternalIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listExternalIps(externalNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<ExternalIp> ips = externalNetwork.listIps();

      assertEquals(ips.size(), totalIps);
   }

   public void testListIpsWithOptions() {
      List<ExternalIp> ips = externalNetwork.listIps(IpOptions.builder().limit(5).build());
      assertEquals(ips.size(), 5);
   }

   public void testListUnusedIps() {
      ExternalIpsDto ipsDto = env.context.getApiContext().getApi().getInfrastructureApi()
            .listExternalIps(externalNetwork.unwrap(), IpOptions.builder().limit(1).build());
      int totalIps = ipsDto.getTotalSize();

      List<ExternalIp> ips = externalNetwork.listUnusedIps();
      assertEquals(ips.size(), totalIps);
   }

   public void testUpdateBasicInfo() {
      externalNetwork.setName("External network Updated");
      externalNetwork.setPrimaryDNS("8.8.8.8");
      externalNetwork.setSecondaryDNS("8.8.8.8");
      externalNetwork.update();

      assertEquals(externalNetwork.getName(), "External network Updated");
      assertEquals(externalNetwork.getPrimaryDNS(), "8.8.8.8");
      assertEquals(externalNetwork.getSecondaryDNS(), "8.8.8.8");

      // Refresh the external network
      ExternalNetwork en = env.enterprise.findExternalNetwork(env.datacenter,
            NetworkPredicates.<ExternalIp> name(externalNetwork.getName()));

      assertEquals(en.getId(), externalNetwork.getId());
      assertEquals(en.getName(), "External network Updated");
      assertEquals(en.getPrimaryDNS(), "8.8.8.8");
      assertEquals(en.getSecondaryDNS(), "8.8.8.8");
   }

   public void testUpdateReadOnlyFields() {
      ExternalNetwork toUpdate = createNetwork(externalNetwork, PREFIX + "-exttoupdate-test");

      try {
         toUpdate.setTag(20);
         toUpdate.setAddress("10.1.0.0");
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
      ExternalNetwork toUpdate = createNetwork(externalNetwork, PREFIX + "-exttoupdate-test");

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
      assertEquals(externalNetwork.getEnterprise().getId(), env.enterprise.getId());
   }

   public void testGetDatacenter() {
      assertEquals(externalNetwork.getDatacenter().getId(), env.datacenter.getId());
   }

   public void testGetNetworkFromIp() {
      ExternalIp ip = externalNetwork.findIp(IpPredicates.<ExternalIp> notUsed());
      ExternalNetwork network = ip.getNetwork();

      assertEquals(network.getId(), externalNetwork.getId());
   }

   private ExternalNetwork createNetwork(final ExternalNetwork from, final String name) {
      ExternalNetwork network = ExternalNetwork.Builder.fromExternalNetwork(from).build();
      network.setName(name);
      network.save();
      assertNotNull(network.getId());
      return network;
   }
}
