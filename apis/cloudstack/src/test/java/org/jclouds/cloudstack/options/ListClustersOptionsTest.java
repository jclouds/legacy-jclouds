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

import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.clusterType;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.hypervisor;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.id;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.keyword;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.managedState;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.name;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.podId;
import static org.jclouds.cloudstack.options.ListClustersOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code ListClustersOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListClustersOptionsTest {

   public void testAllocationState() {
      ListClustersOptions options = new ListClustersOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      ListClustersOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testClusterType() {
      ListClustersOptions options = new ListClustersOptions().clusterType(Host.ClusterType.CLOUD_MANAGED);
      assertEquals(ImmutableList.of("CloudManaged"), options.buildQueryParameters().get("clustertype"));
   }

   public void testClusterTypeStatic() {
      ListClustersOptions options = clusterType(Host.ClusterType.CLOUD_MANAGED);
      assertEquals(ImmutableList.of("CloudManaged"), options.buildQueryParameters().get("clustertype"));
   }

   public void testHypervisor() {
      ListClustersOptions options = new ListClustersOptions().hypervisor("XenServer");
      assertEquals(ImmutableList.of("XenServer"), options.buildQueryParameters().get("hypervisor"));
   }

   public void testHypervisorStatic() {
      ListClustersOptions options = hypervisor("XenServer");
      assertEquals(ImmutableList.of("XenServer"), options.buildQueryParameters().get("hypervisor"));
   }

   public void testId() {
      ListClustersOptions options = new ListClustersOptions().id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testIdStatic() {
      ListClustersOptions options = id("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("id"));
   }

   public void testKeyword() {
      ListClustersOptions options = new ListClustersOptions().keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testKeywordStatic() {
      ListClustersOptions options = keyword("Enabled");
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("keyword"));
   }

   public void testManagedState() {
      ListClustersOptions options = new ListClustersOptions().managedState(Cluster.ManagedState.PREPARE_UNMANAGED);
      assertEquals(ImmutableList.of("PrepareUnmanaged"), options.buildQueryParameters().get("managedstate"));
   }

   public void testManagedStateStatic() {
      ListClustersOptions options = managedState(Cluster.ManagedState.PREPARE_UNMANAGED);
      assertEquals(ImmutableList.of("PrepareUnmanaged"), options.buildQueryParameters().get("managedstate"));
   }

   public void testName() {
      ListClustersOptions options = new ListClustersOptions().name("Host Name");
      assertEquals(ImmutableList.of("Host Name"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      ListClustersOptions options = name("Host Name");
      assertEquals(ImmutableList.of("Host Name"), options.buildQueryParameters().get("name"));
   }

   public void testPodId() {
      ListClustersOptions options = new ListClustersOptions().podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      ListClustersOptions options = podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testZoneId() {
      ListClustersOptions options = new ListClustersOptions().zoneId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("zoneid"));
   }

   public void testZoneIdStatic() {
      ListClustersOptions options = zoneId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("zoneid"));
   }
}
