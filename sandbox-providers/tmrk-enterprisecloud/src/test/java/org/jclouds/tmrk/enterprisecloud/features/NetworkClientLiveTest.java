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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.network.NetworkReference;
import org.jclouds.tmrk.enterprisecloud.domain.network.Networks;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code NetworkClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "NetworkClientLiveTest")
public class NetworkClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getNetworkClient();
   }

   private NetworkClient client;

   public void testGetNetworks() throws Exception {
      Networks networks = client.getNetworks(URI.create("/cloudapi/ecloud/networks/environments/77"));
      assertNotNull(networks);
      for(NetworkReference network: networks.getNetworks()) {
         testGetNetwork(network.getHref());
      }
   }

   private void testGetNetwork(URI uri) throws Exception {
      NetworkReference network = client.getNetwork(uri);
      assertNotNull(network);
      assertNotNull(network.getAddress());
      assertNotNull(network.getNetworkType());
      assertNotNull(network.getBroadcastAddress());
      assertNotNull(network.getGatewayAddress());
   }
}