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

package org.jclouds.abiquo.domain.config;

import static org.jclouds.abiquo.reference.AbiquoTestConstants.PREFIX;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.config.CategoryPredicates;
import org.testng.annotations.Test;

/**
 * Live integration tests for the {@link Category} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "CategoryLiveApiTest")
public class CategoryLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testCreateAndGet() {
      Category category = Category.builder(env.context.getApiContext()).name(PREFIX + "-test-category").build();
      category.save();

      Category apiCategory = env.context.getAdministrationService().findCategory(
            CategoryPredicates.name(PREFIX + "-test-category"));
      assertNotNull(apiCategory);
      assertEquals(category.getName(), apiCategory.getName());

      apiCategory.delete();
   }

   @Test(dependsOnMethods = "testCreateAndGet")
   public void testUpdate() {
      Iterable<Category> categories = env.context.getAdministrationService().listCategories();
      assertNotNull(categories);

      Category category = categories.iterator().next();
      String name = category.getName();

      category.setName(PREFIX + "-test-category-updated");
      category.update();

      Category apiCategory = env.context.getAdministrationService().findCategory(
            CategoryPredicates.name(PREFIX + "-test-category-updated"));

      assertNotNull(apiCategory);
      assertEquals(PREFIX + "-test-category-updated", apiCategory.getName());

      category.setName(name);
      category.update();
   }
}
