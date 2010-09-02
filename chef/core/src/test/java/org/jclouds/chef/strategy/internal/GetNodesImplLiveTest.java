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

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.ChefClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code GetNodesImpl} strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.GetNodesImplLiveTest")
public class GetNodesImplLiveTest extends BaseChefStrategyLiveTest {
   private ListNodesImpl strategy;
   private CreateNodeAndPopulateAutomaticAttributesImpl creater;
   private ChefClient chef;

   @BeforeTest(groups = "live", dependsOnMethods = "setupClient")
   void setupStrategy() {
      this.creater = injector.getInstance(CreateNodeAndPopulateAutomaticAttributesImpl.class);
      this.strategy = injector.getInstance(ListNodesImpl.class);
      this.chef = injector.getInstance(ChefClient.class);
   }

   @BeforeTest(groups = "live", dependsOnMethods = "setupStrategy")
   void setupNodes() {
      creater.execute(prefix, ImmutableSet.<String> of());
      creater.execute(prefix + 1, ImmutableSet.<String> of());
   }

   @AfterTest(groups = { "live" })
   @Override
   public void teardownClient() throws IOException {
      chef.deleteNode(prefix);
      chef.deleteNode(prefix + 1);
      super.teardownClient();
   }

   @Test
   public void testExecute() {
      assert size(strategy.execute()) > 0;
   }

   @Test
   public void testExecutePredicateOfString() {
      assertEquals(size(strategy.execute(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith(prefix);
         }

      })), 2);
   }

   @Test
   public void testExecuteIterableOfString() {
      assertEquals(size(strategy.execute(ImmutableSet.of(prefix, prefix + 1))), 2);
   }

}
