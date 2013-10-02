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

import static org.jclouds.cloudstack.options.AddClusterOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.AddClusterOptions.Builder.password;
import static org.jclouds.cloudstack.options.AddClusterOptions.Builder.podId;
import static org.jclouds.cloudstack.options.AddClusterOptions.Builder.url;
import static org.jclouds.cloudstack.options.AddClusterOptions.Builder.username;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code AddClusterOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class AddClusterOptionsTest {

   public void testAllocationState() {
      AddClusterOptions options = new AddClusterOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      AddClusterOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testPassword() {
      AddClusterOptions options = new AddClusterOptions().password("sekrit");
      assertEquals(ImmutableList.of("sekrit"), options.buildQueryParameters().get("password"));
   }

   public void testPasswordStatic() {
      AddClusterOptions options = password("sekrit");
      assertEquals(ImmutableList.of("sekrit"), options.buildQueryParameters().get("password"));
   }

   public void testPodId() {
      AddClusterOptions options = new AddClusterOptions().podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      AddClusterOptions options = podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testUrl() {
      AddClusterOptions options = new AddClusterOptions().url("http://example.com");
      assertEquals(ImmutableList.of("http://example.com"), options.buildQueryParameters().get("url"));
   }

   public void testUrlStatic() {
      AddClusterOptions options = url("http://example.com");
      assertEquals(ImmutableList.of("http://example.com"), options.buildQueryParameters().get("url"));
   }

   public void testUsername() {
      AddClusterOptions options = new AddClusterOptions().username("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("username"));
   }

   public void testUsernameStatic() {
      AddClusterOptions options = username("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("username"));
   }

}
