/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.options;

import static org.jclouds.rackspace.cloudloadbalancers.v1.options.ListOptions.Builder.limit;
import static org.jclouds.rackspace.cloudloadbalancers.v1.options.ListOptions.Builder.marker;
import static org.testng.Assert.assertEquals;

import org.jclouds.rackspace.cloudloadbalancers.v1.options.ListOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListOptionsTest {

   public void testMarker() {
      ListOptions options = new ListOptions().marker("1");
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("marker"));
   }

   public void testLimit() {
      int limit = 1;
      ListOptions options = new ListOptions().limit(limit);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }

   public void testMarkerStatic() {
      ListOptions options = marker("1");
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("marker"));
   }

   public void testLimitStatic() {
      ListOptions options = limit(1);
      assertEquals(ImmutableList.of("1"), options.buildQueryParameters().get("limit"));
   }
}
