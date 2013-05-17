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

import static org.jclouds.cloudstack.domain.NetworkOfferingAvailabilityType.REQUIRED;

import java.util.Set;

import org.jclouds.cloudstack.domain.NetworkOffering;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListNetworkOfferingsResponseTest extends BaseSetParserTest<NetworkOffering> {

   @Override
   public String resource() {
      return "/listnetworkofferingsresponse.json";
   }

   @Override
   @SelectJson("networkoffering")
   public Set<NetworkOffering> expected() {
      return ImmutableSet.<NetworkOffering>of(
         NetworkOffering.builder().id("7").name("DefaultDirectNetworkOffering").displayText("Direct")
            .trafficType(TrafficType.PUBLIC).isDefault(true).supportsVLAN(false).availability(REQUIRED)
            .networkRate(200).build(), NetworkOffering.builder().id("6").name("DefaultVirtualizedNetworkOffering")
            .displayText("Virtual Vlan").trafficType(TrafficType.GUEST).isDefault(true).supportsVLAN(false)
            .availability(REQUIRED).networkRate(200).build());
   }

}
