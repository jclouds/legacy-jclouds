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
package org.jclouds.scriptbuilder.domain.chef;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link RunList} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "RunListTest")
public class RunListTest {

   public void testToStringEmptyRunlist() {
      assertEquals(RunList.builder().build().toString(), "[]");
   }

   public void testToStringWithRecipe() {
      assertEquals(RunList.builder().recipe("apache2").build().toString(), "[\"recipe[apache2]\"]");
   }

   public void testToStringWithRole() {
      assertEquals(RunList.builder().role("webserver").build().toString(), "[\"role[webserver]\"]");
   }

   public void testToStringWithRecipeAndRole() {
      assertEquals(RunList.builder().recipe("apache2").role("webserver").build().toString(),
            "[\"recipe[apache2]\",\"role[webserver]\"]");
   }

}
