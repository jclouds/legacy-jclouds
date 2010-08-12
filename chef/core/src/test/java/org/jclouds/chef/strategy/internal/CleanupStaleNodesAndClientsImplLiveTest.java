/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.strategy.internal;

import org.jclouds.chef.ChefClient;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CleanupStaleNodesAndClientsImpl} strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.CleanupStaleNodesAndClientsImplLiveTest")
public class CleanupStaleNodesAndClientsImplLiveTest extends BaseChefStrategyLiveTest {
   private CreateNodeAndPopulateAutomaticAttributesImpl creater;
   private CleanupStaleNodesAndClientsImpl strategy;
   private ChefClient chef;

   @BeforeTest(groups = "live", dependsOnMethods = "setupClient")
   void setupStrategy() {
      this.creater = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(CleanupStaleNodesAndClientsImpl.class);
      this.chef = injector.getInstance(ChefClient.class);
   }

   @Test
   public void testExecute() throws InterruptedException {
      try {
         creater.execute(prefix, ImmutableSet.<String> of());
         // http://tickets.corp.opscode.com/browse/PL-522
         // assert chef.nodeExists(prefix);
         assert chef.getNode(prefix) != null;
         strategy.execute(prefix, 10);
         assert chef.getNode(prefix) != null;
         Thread.sleep(1000);
         strategy.execute(prefix, 1);
         assert chef.getNode(prefix) == null;
      } finally {
         chef.deleteNode(prefix);
      }
   }

}
