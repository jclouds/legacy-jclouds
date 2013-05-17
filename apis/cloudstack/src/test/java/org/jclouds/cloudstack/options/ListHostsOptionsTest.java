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

import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.clusterId;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.page;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.pageSize;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.state;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.type;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.virtualMachineId;
import static org.jclouds.cloudstack.options.ListHostsOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Host;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListHostsOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListHostsOptionsTest {

   public void testId() {
      ListHostsOptions options = new ListHostsOptions().id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListHostsOptions options = id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testAllocationState() {
      ListHostsOptions options = new ListHostsOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      ListHostsOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testClusterId() {
      ListHostsOptions options = new ListHostsOptions().clusterId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("clusterid"));
   }

   public void testClusterIdStatic() {
      ListHostsOptions options = clusterId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("clusterid"));
   }

   public void testKeyword() {
      ListHostsOptions options = new ListHostsOptions().keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListHostsOptions options = keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testName() {
      ListHostsOptions options = new ListHostsOptions().name("Host Name");
      assertEquals(ImmutableList.of("Host Name"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListHostsOptions options = name("Host Name");
      assertEquals(ImmutableList.of("Host Name"), options.buildQueryParameters().get("name"));
   }

   public void testPage() {
      ListHostsOptions options = new ListHostsOptions().page(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("page"));
   }

   public void testPageStatic() {
      ListHostsOptions options = page(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("page"));
   }

   public void testPageSize() {
       ListHostsOptions options = new ListHostsOptions().pageSize(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("pagesize"));
   }

   public void testPageSizeStatic() {
      ListHostsOptions options = pageSize(42L);
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("pagesize"));
   }

   public void testPodId() {
      ListHostsOptions options = new ListHostsOptions().podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListHostsOptions options = podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testState() {
      ListHostsOptions options = new ListHostsOptions().state("Up");
      assertEquals(ImmutableList.of("Up"), options.buildQueryParameters().get("state"));
   }

   public void testStateStatic() {
      ListHostsOptions options = state("Up");
      assertEquals(ImmutableList.of("Up"), options.buildQueryParameters().get("state"));
   }

   public void testType() {
      ListHostsOptions options = new ListHostsOptions().type(Host.Type.ROUTING);
      assertEquals(ImmutableList.of("Routing"), options.buildQueryParameters().get("type"));
   }

   public void testTypeStatic() {
      ListHostsOptions options = type(Host.Type.ROUTING);
      assertEquals(ImmutableList.of("Routing"), options.buildQueryParameters().get("type"));
   }

   public void testVirtualMachineId() {
      ListHostsOptions options = new ListHostsOptions().virtualMachineId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testVirtualMachineIdStatic() {
      ListHostsOptions options = virtualMachineId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("virtualmachineid"));
   }

   public void testZoneId() {
      ListHostsOptions options = new ListHostsOptions().zoneId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListHostsOptions options = zoneId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("zoneid"));
   }

}
