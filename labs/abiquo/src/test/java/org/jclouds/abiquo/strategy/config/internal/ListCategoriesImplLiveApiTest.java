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

package org.jclouds.abiquo.strategy.config.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.config.Category;
import org.jclouds.abiquo.predicates.config.CategoryPredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;

/**
 * Live tests for the {@link ListPropertiesImpl} strategy.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "ListCategoriesImplLiveApiTest")
public class ListCategoriesImplLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListCategoriesImpl strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListCategoriesImpl.class);
   }

   public void testExecute() {
      Iterable<Category> categories = strategy.execute();
      assertNotNull(categories);
      assertTrue(size(categories) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<Category> categories = strategy.execute(CategoryPredicates.name("Unexisting category"));
      assertNotNull(categories);
      assertEquals(size(categories), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<Category> categories = strategy.execute(CategoryPredicates.name("Applications servers"));
      assertNotNull(categories);
      assertEquals(size(categories), 1);
   }

   public void testExecuteNotPredicateWithResults() {
      Iterable<Category> categories = strategy.execute(Predicates.not(CategoryPredicates.name("Applications servers")));

      Iterable<Category> allProperties = strategy.execute();

      assertNotNull(categories);
      assertNotNull(allProperties);
      assertEquals(size(categories), size(allProperties) - 1);
   }
}
