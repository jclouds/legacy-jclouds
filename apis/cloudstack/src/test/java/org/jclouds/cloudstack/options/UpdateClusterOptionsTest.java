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

import static org.jclouds.cloudstack.options.UpdateClusterOptions.Builder.allocationState;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Cluster;
import org.jclouds.cloudstack.domain.Host;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code UpdateClusterOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class UpdateClusterOptionsTest {

   public void testAllocationState() {
      UpdateClusterOptions options = new UpdateClusterOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      UpdateClusterOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testClusterName() {
      UpdateClusterOptions options = new UpdateClusterOptions().clusterName("My Cluster");
      assertEquals(ImmutableList.of("My Cluster"), options.buildQueryParameters().get("clustername"));
   }

   public void testClusterType() {
      UpdateClusterOptions options = new UpdateClusterOptions().clusterType(Host.ClusterType.CLOUD_MANAGED);
      assertEquals(ImmutableList.of("CloudManaged"), options.buildQueryParameters().get("clustertype"));
   }

   public void testHypervisor() {
      UpdateClusterOptions options = new UpdateClusterOptions().hypervisor("XenServer");
      assertEquals(ImmutableList.of("XenServer"), options.buildQueryParameters().get("hypervisor"));
   }

   public void testManagedState() {
      UpdateClusterOptions options = new UpdateClusterOptions().managedState(Cluster.ManagedState.PREPARE_UNMANAGED);
      assertEquals(ImmutableList.of("PrepareUnmanaged"), options.buildQueryParameters().get("managedstate"));
   }

}
