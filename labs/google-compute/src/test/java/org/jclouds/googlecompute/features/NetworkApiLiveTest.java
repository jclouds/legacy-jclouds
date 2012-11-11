/*
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

package org.jclouds.googlecompute.features;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.internal.BaseGoogleComputeApiLiveTest;
import org.jclouds.googlecompute.options.ListOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author David Alves
 */
public class NetworkApiLiveTest extends BaseGoogleComputeApiLiveTest {

   private static final String NETWORK_NAME = "network-api-live-test-network";
   private static final int TIME_WAIT = 10;

   private Network network;

   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      super.setupContext();
      network = Network.builder()
              .name(NETWORK_NAME)
              .IPv4Range("10.0.0.0/8")
              .build();
   }

   private NetworkApi api() {
      return context.getApi().getNetworkApi();
   }

   @Test(groups = "live")
   public void testInsertNetwork() {

      assertOperationDoneSucessfully(api().insert(getUserProject(), network), TIME_WAIT);

   }

   @Test(groups = "live", dependsOnMethods = "testInsertNetwork")
   public void testGetNetwork() {

      Network network = api().get(getUserProject(), NETWORK_NAME);
      assertNotNull(network);
      assertNetworkEquals(network, this.network);
   }

   @Test(groups = "live", dependsOnMethods = "testGetNetwork")
   public void testListNetwork() {

      PagedIterable<Network> networks = api().list(getUserProject(), ListOptions.builder()
              .filter("name eq " + NETWORK_NAME)
              .build());

      List<Network> networksAsList = Lists.newArrayList(networks.concat());

      assertEquals(networksAsList.size(), 1);

      assertNetworkEquals(Iterables.getOnlyElement(networksAsList), network);

   }

   @Test(groups = "live", dependsOnMethods = "testListNetwork")
   public void testDeleteNetwork() {

      assertOperationDoneSucessfully(api().delete(getUserProject(), NETWORK_NAME), TIME_WAIT);
   }

   private void assertNetworkEquals(Network result, Network expected) {
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getIPv4Range(), expected.getIPv4Range());
   }

}
