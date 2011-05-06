/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPortForwardingRulesResponseTest extends BaseSetParserTest<PortForwardingRule> {

   @Override
   public Class<PortForwardingRule> type() {
      return PortForwardingRule.class;
   }

   @Override
   public String resource() {
      return "/listportforwardingrulesresponse.json";
   }

   @Override
   public Set<PortForwardingRule> expected() {
      return ImmutableSet.<PortForwardingRule> of(
            PortForwardingRule.builder().id(15).privatePort(22).protocol("tcp").publicPort(2022).virtualMachineId(3)
                  .virtualMachineName("i-3-3-VM").IPAddressId(3).IPAddress("72.52.126.32").state("Active").build(),
            PortForwardingRule.builder().id(18).privatePort(22).protocol("tcp").publicPort(22).virtualMachineId(89)
                  .virtualMachineName("i-3-89-VM").IPAddressId(34).IPAddress("72.52.126.63").state("Active").build());
   }

}
