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
package org.jclouds.cloudstack.options;

import static org.jclouds.cloudstack.options.ListPodsOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.ListPodsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListPodsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListPodsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListPodsOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListPodsOptions}
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListPodsOptionsTest {

   public void testId() {
      ListPodsOptions options = new ListPodsOptions().id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListPodsOptions options = id("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("id"));
   }

   public void testAllocationState() {
      ListPodsOptions options = new ListPodsOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      ListPodsOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testKeyword() {
      ListPodsOptions options = new ListPodsOptions().keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListPodsOptions options = keyword("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("keyword"));
   }

   public void testName() {
      ListPodsOptions options = new ListPodsOptions().name("bob");
      assertEquals(ImmutableList.of("bob"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListPodsOptions options = name("bob");
      assertEquals(ImmutableList.of("bob"), options.buildQueryParameters().get("name"));
   }

   public void testZoneId() {
      ListPodsOptions options = new ListPodsOptions().zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListPodsOptions options = zoneId("6");
      assertEquals(ImmutableList.of("6"), options.buildQueryParameters().get("zoneid"));
   }

}
