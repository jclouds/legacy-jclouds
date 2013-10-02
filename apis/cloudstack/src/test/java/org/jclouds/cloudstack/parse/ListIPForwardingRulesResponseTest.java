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

import org.jclouds.cloudstack.domain.IPForwardingRule;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListIPForwardingRulesResponseTest extends BaseSetParserTest<IPForwardingRule> {

   @Override
   public String resource() {
      return "/listipforwardingrulesresponse.json";
   }

   @Override
   @SelectJson("ipforwardingrule")
   public Set<IPForwardingRule> expected() {
      return ImmutableSet.<IPForwardingRule> of(
         IPForwardingRule.builder().id("66").protocol("tcp").startPort(22).endPort(22).virtualMachineId("58")
            .virtualMachineDisplayName("i-4-58-VM").virtualMachineName("i-4-58-VM")
            .IPAddressId("15").IPAddress("10.27.27.64").state("Active").build());
   }

}
