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
package org.jclouds.rds.options;

import static org.jclouds.rds.options.ListSubnetGroupsOptions.Builder.afterMarker;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListSubnetGroupsOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListSubnetGroupsOptionsTest")
public class ListSubnetGroupsOptionsTest {

   public void testMarker() {
      ListSubnetGroupsOptions options = new ListSubnetGroupsOptions().afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("Marker"));
   }

   public void testMarkerStatic() {
      ListSubnetGroupsOptions options = afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("Marker"));
   }

}
