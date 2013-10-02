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

import static org.jclouds.cloudstack.options.AddHostOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.AddHostOptions.Builder.clusterId;
import static org.jclouds.cloudstack.options.AddHostOptions.Builder.clusterName;
import static org.jclouds.cloudstack.options.AddHostOptions.Builder.hostTags;
import static org.jclouds.cloudstack.options.AddHostOptions.Builder.podId;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AddHostOptions}
 *
 * @author Richard Downer
 */
@Test(groups = "unit")
public class AddHostOptionsTest {

   public void testAllocationState() {
      AddHostOptions options = new AddHostOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      AddHostOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testClusterId() {
      AddHostOptions options = new AddHostOptions().clusterId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("clusterid"));
   }

   public void testClusterIdStatic() {
      AddHostOptions options = clusterId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("clusterid"));
   }

   public void testClusterName() {
      AddHostOptions options = new AddHostOptions().clusterName("Cluster Name");
      assertEquals(ImmutableList.of("Cluster Name"), options.buildQueryParameters().get("clustername"));
   }

   public void testClusterNameStatic() {
      AddHostOptions options = clusterName("Cluster Name");
      assertEquals(ImmutableList.of("Cluster Name"), options.buildQueryParameters().get("clustername"));
   }

   public void testHostTags() {
      AddHostOptions options = new AddHostOptions().hostTags(ImmutableSet.<String>of("foo", "bar", "baz"));
      assertEquals(ImmutableList.of("foo,bar,baz"), options.buildQueryParameters().get("hosttags"));
   }

   public void testHostTagsStatic() {
      AddHostOptions options = hostTags(ImmutableSet.<String>of("foo", "bar", "baz"));
      assertEquals(ImmutableList.of("foo,bar,baz"), options.buildQueryParameters().get("hosttags"));
   }

   public void testPodId() {
      AddHostOptions options = new AddHostOptions().podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

   public void testPodIdStatic() {
      AddHostOptions options = podId("42");
      assertEquals(ImmutableList.of("42"), options.buildQueryParameters().get("podid"));
   }

}
