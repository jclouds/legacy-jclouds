/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.SortedSet;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
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
@Test(groups = "live", testName = "rimuhosting.RimuHostingClientLiveTest")
public class RimuHostingClientLiveTest {

   private RimuHostingClient connection;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String password = checkNotNull(System.getProperty("jclouds.test.key"),
            "jclouds.test.key");

      connection = (RimuHostingClient) RimuHostingContextFactory.createContext(
            password, new Log4JLoggingModule()).getProviderSpecificContext()
            .getApi();
   }

   @Test
   public void testPricingPlans() {
      SortedSet<PricingPlan> plans = connection.getPricingPlanList();
      for (PricingPlan plan : plans) {
         if (plan.getId().equalsIgnoreCase("miro1")) {
            assertTrue(true);
            return;
         }
      }
      assertTrue(false);
   }

   @Test
   public void testImages() {
      SortedSet<Image> images = connection.getImageList();
      for (Image image : images) {
         if (image.getId().equalsIgnoreCase("lenny")) {
            assertTrue(true);
            return;
         }
      }
      assertTrue(false, "lenny not found");
   }

   @Test
   public void testLifeCycle() {
      // Get the first image, we dont really care what it is in this test.
      NewServerResponse serverResponse = connection.createServer(
            "test.ivan.api.com", "lenny", "MIRO1B");
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
