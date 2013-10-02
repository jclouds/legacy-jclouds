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

import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.allocationState;
import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.endIp;
import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.gateway;
import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.name;
import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.netmask;
import static org.jclouds.cloudstack.options.UpdatePodOptions.Builder.startIp;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.domain.AllocationState;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code UpdatePodOptions}
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class UpdatePodOptionsTest {

   public void testName() {
      UpdatePodOptions options = new UpdatePodOptions().name("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("name"));
   }

   public void testNameStatic() {
      UpdatePodOptions options = name("fred");
      assertEquals(ImmutableList.of("fred"), options.buildQueryParameters().get("name"));
   }

   public void testStartIp() {
      UpdatePodOptions options = new UpdatePodOptions().startIp("192.168.42.42");
      assertEquals(ImmutableList.of("192.168.42.42"), options.buildQueryParameters().get("startip"));
   }

   public void testStartIpStatic() {
      UpdatePodOptions options = startIp("192.168.42.42");
      assertEquals(ImmutableList.of("192.168.42.42"), options.buildQueryParameters().get("startip"));
   }

   public void testEndIp() {
      UpdatePodOptions options = new UpdatePodOptions().endIp("192.168.42.52");
      assertEquals(ImmutableList.of("192.168.42.52"), options.buildQueryParameters().get("endip"));
   }

   public void testEndIpStatic() {
      UpdatePodOptions options = endIp("192.168.42.52");
      assertEquals(ImmutableList.of("192.168.42.52"), options.buildQueryParameters().get("endip"));
   }

   public void testGateway() {
      UpdatePodOptions options = new UpdatePodOptions().gateway("192.168.42.62");
      assertEquals(ImmutableList.of("192.168.42.62"), options.buildQueryParameters().get("gateway"));
   }

   public void testGatewayStatic() {
      UpdatePodOptions options = gateway("192.168.42.62");
      assertEquals(ImmutableList.of("192.168.42.62"), options.buildQueryParameters().get("gateway"));
   }

   public void testNetmask() {
      UpdatePodOptions options = new UpdatePodOptions().netmask("255.255.240.0");
      assertEquals(ImmutableList.of("255.255.240.0"), options.buildQueryParameters().get("netmask"));
   }

   public void testNetmaskStatic() {
      UpdatePodOptions options = netmask("255.255.240.0");
      assertEquals(ImmutableList.of("255.255.240.0"), options.buildQueryParameters().get("netmask"));
   }

   public void testAllocationState() {
      UpdatePodOptions options = new UpdatePodOptions().allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

   public void testAllocationStateStatic() {
      UpdatePodOptions options = allocationState(AllocationState.ENABLED);
      assertEquals(ImmutableList.of("Enabled"), options.buildQueryParameters().get("allocationstate"));
   }

}
