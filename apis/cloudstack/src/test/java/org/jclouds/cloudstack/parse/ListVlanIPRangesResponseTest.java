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

import org.jclouds.cloudstack.domain.VlanIPRange;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListVlanIPRangesResponseTest extends BaseSetParserTest<VlanIPRange> {

   @Override
   public String resource() {
      return "/listvlaniprangesresponse.json";
   }

   @Override
   @SelectJson("vlaniprange")
   public Set<VlanIPRange> expected() {
      VlanIPRange range1 = VlanIPRange.builder()
         .id("1")
         .forVirtualNetwork(true)
         .zoneId("1")
         .vlan("127")
         .account("system")
         .domainId("1")
         .domain("ROOT")
         .gateway("10.27.27.254")
         .netmask("255.255.255.0")
         .startIP("10.27.27.50")
         .endIP("10.27.27.100")
         .networkId("200")
         .build();
      VlanIPRange range2 = VlanIPRange.builder()
         .id("2")
         .forVirtualNetwork(false)
         .zoneId("2")
         .vlan("untagged")
         .account("system")
         .domainId("1")
         .domain("ROOT")
         .podId("2")
         .podName("Dev Pod 2")
         .gateway("10.22.22.254")
         .netmask("255.255.255.0")
         .startIP("10.22.22.51")
         .endIP("10.22.22.100")
         .networkId("209")
         .build();
      return ImmutableSet.of(range1, range2);
   }
}
