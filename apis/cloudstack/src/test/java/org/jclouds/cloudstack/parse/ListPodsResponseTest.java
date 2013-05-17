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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListPodsResponseTest extends BaseSetParserTest<Pod> {

   @Override
   public String resource() {
      return "/listpodsresponse.json";
   }

   @Override
   @SelectJson("pod")
   public Set<Pod> expected() {
      Pod pod1 = Pod.builder()
         .id("1")
         .name("Dev Pod 1")
         .zoneId("1")
         .zoneName("Dev Zone 1")
         .gateway("10.26.26.254")
         .netmask("255.255.255.0")
         .startIp("10.26.26.50")
         .endIp("10.26.26.100")
         .allocationState(AllocationState.ENABLED)
         .build();
      Pod pod2 = Pod.builder()
         .id("2")
         .name("Dev Pod 2")
         .zoneId("2")
         .zoneName("Dev Zone 2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .startIp("10.22.22.25")
         .endIp("10.22.22.50")
         .allocationState(AllocationState.ENABLED)
         .build();
      return ImmutableSet.of(pod1, pod2);
   }
}
