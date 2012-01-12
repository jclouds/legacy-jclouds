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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Host;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.options.UpdateHostOptions.Builder.*;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code UpdateHostOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class UpdateHostOptionsTest {

   public void testAllocationState() {
      UpdateHostOptions options = new UpdateHostOptions().allocationState(Host.AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      UpdateHostOptions options = allocationState(Host.AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testHostTags() {
      UpdateHostOptions options = new UpdateHostOptions().hostTags(ImmutableSet.<String>of("foo", "bar", "baz"));
      assertEquals(ImmutableList.of("foo,bar,baz"), options.buildQueryParameters().get("hosttags"));
   }

   public void testHostTagsStatic() {
      UpdateHostOptions options = hostTags(ImmutableSet.<String>of("foo", "bar", "baz"));
      assertEquals(ImmutableList.of("foo,bar,baz"), options.buildQueryParameters().get("hosttags"));
   }

   public void testOsCategoryId() {
      UpdateHostOptions options = new UpdateHostOptions().osCategoryId(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("oscategoryid"));
   }

   public void testOsCategoryIdStatic() {
      UpdateHostOptions options = osCategoryId(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("oscategoryid"));
   }

}
