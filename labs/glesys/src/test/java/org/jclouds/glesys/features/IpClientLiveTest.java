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
package org.jclouds.glesys.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.glesys.internal.BaseGleSYSClientLiveTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code IpClient}
 *
 * @author Adrian Cole, Mattias Holmqvist
 */
@Test(groups = "live", testName = "IpClientLiveTest")
public class IpClientLiveTest extends BaseGleSYSClientLiveTest {

   @BeforeGroups(groups = {"live"})
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getIpClient();
   }

   private IpClient client;

   @Test
   public void testListFree() throws Exception {
      Set<String> freeIps = client.listFree("4", "Falkenberg", "Xen");
      assertTrue(freeIps.size() >= 1);
   }

   @Test
   public void testGetOpenVZDetails() throws Exception {
      Set<String> openVzIps = client.listFree("4", "Falkenberg", "OpenVZ");
      assertTrue(openVzIps.size() >= 1);
      String openVzIp = openVzIps.iterator().next();
      IpDetails ipDetails = client.getIpDetails(openVzIp);
      assertEquals(ipDetails.getDatacenter(), "Falkenberg");
      assertEquals(ipDetails.getPlatform(), "OpenVZ");
      assertEquals(ipDetails.getIpversion(), "4");
      
      // TODO: Ask Glesys to include address in response for OpenVZ?
      // assertEquals(ipDetails.getAddress(), openVzIp);
   }

   @Test
   public void testGetXenDetails() throws Exception {
      Set<String> xenVzIps = client.listFree("4", "Falkenberg", "Xen");
      assertTrue(xenVzIps.size() >= 1);
      String xenIp = xenVzIps.iterator().next();
      IpDetails ipDetails = client.getIpDetails(xenIp);
      assertEquals(ipDetails.getDatacenter(), "Falkenberg");
      assertEquals(ipDetails.getPlatform(), "Xen");
      assertEquals(ipDetails.getIpversion(), "4");
      assertEquals(ipDetails.getAddress(), xenIp);
      assertNotNull(ipDetails.getPtr());
      assertNotNull(ipDetails.getBroadcast());
      assertNotNull(ipDetails.getGateway());
      assertNotNull(ipDetails.getNetmask());
      List<String> nameServers = ipDetails.getNameServers();
      assertNotNull(nameServers);
   }

}
