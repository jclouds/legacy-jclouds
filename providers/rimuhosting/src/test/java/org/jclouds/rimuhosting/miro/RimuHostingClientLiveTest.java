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
package org.jclouds.rimuhosting.miro;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.Set;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.rimuhosting.miro.domain.Image;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.PricingPlan;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.ServerInfo;
import org.jclouds.rimuhosting.miro.domain.internal.RunningState;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code RimuHostingClient}
 * 
 * @author Ivan Meredith
 */
@Test(groups = "live", singleThreaded = true, testName = "RimuHostingClientLiveTest")
public class RimuHostingClientLiveTest
      extends
      BaseComputeServiceContextLiveTest {

   public RimuHostingClientLiveTest() {
      provider = "rimuhosting";
   }
   
   private RimuHostingClient connection;
   private RestContext<RimuHostingClient, RimuHostingAsyncClient> restContext;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      restContext = view.unwrap();
      this.connection = restContext.getApi();

   }

   @Test
   public void testPricingPlans() {
      Set<? extends PricingPlan> plans = connection.getPricingPlanList();
      for (PricingPlan plan : plans) {
         if (plan.getId().equalsIgnoreCase("MIRO4B")) {
            return;
         }
      }
      fail("MIRO4B not found");
   }

   @Test
   public void testImages() {
      Set<? extends Image> images = connection.getImageList();
      for (Image image : images) {
         if (image.getId().equalsIgnoreCase("lenny")) {
            return;
         }
      }
      fail("lenny not found");
   }

   @Test
   public void testLifeCycle() {
      // Get the first image, we dont really care what it is in this test.
      NewServerResponse serverResponse = connection.createServer("test.ivan.api.com", "lenny", "MIRO4B");
      Server server = serverResponse.getServer();
      // Now we have the server, lets restart it
      assertNotNull(server.getId());
      ServerInfo serverInfo = connection.restartServer(server.getId());

      // Should be running now.
      assertEquals(serverInfo.getState(), RunningState.RUNNING);
      assertEquals(server.getName(), "test.ivan.api.com");
      assertEquals(server.getImageId(), "lenny");
      connection.destroyServer(server.getId());
   }
}
