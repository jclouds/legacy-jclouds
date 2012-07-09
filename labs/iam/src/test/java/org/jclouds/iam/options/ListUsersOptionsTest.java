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
package org.jclouds.iam.options;

import static org.jclouds.iam.options.ListUsersOptions.Builder.afterMarker;
import static org.jclouds.iam.options.ListUsersOptions.Builder.maxItems;
import static org.jclouds.iam.options.ListUsersOptions.Builder.pathPrefix;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListUsersOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "maxItems", testName = "ListUsersOptionsTest")
public class ListUsersOptionsTest {

   public void testMarker() {
      ListUsersOptions options = new ListUsersOptions().afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("Marker"));
   }

   public void testMarkerStatic() {
      ListUsersOptions options = afterMarker("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("Marker"));
   }

   public void testMaxItems() {
      ListUsersOptions options = new ListUsersOptions().maxItems(1000);
      assertEquals(ImmutableSet.of("1000"), options.buildFormParameters().get("MaxItems"));
   }

   public void testMaxItemsStatic() {
      ListUsersOptions options = maxItems(1000);
      assertEquals(ImmutableSet.of("1000"), options.buildFormParameters().get("MaxItems"));
   }

   public void testPathPrefix() {
      ListUsersOptions options = new ListUsersOptions().pathPrefix("/division_abc/subdivision_xyz/");
      assertEquals(ImmutableSet.of("/division_abc/subdivision_xyz/"), options.buildFormParameters().get("PathPrefix"));
   }

   public void testPathPrefixStatic() {
      ListUsersOptions options = pathPrefix("/division_abc/subdivision_xyz/");
      assertEquals(ImmutableSet.of("/division_abc/subdivision_xyz/"), options.buildFormParameters().get("PathPrefix"));
   }

}
