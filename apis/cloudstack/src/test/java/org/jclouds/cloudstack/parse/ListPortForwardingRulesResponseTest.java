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

import org.jclouds.cloudstack.domain.PortForwardingRule;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListPortForwardingRulesResponseTest extends BaseSetParserTest<PortForwardingRule> {

   @Override
   protected Injector injector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });
   }

   @Override
   public String resource() {
      return "/listportforwardingrulesresponse.json";
   }

   @Override
   @SelectJson("portforwardingrule")
   public Set<PortForwardingRule> expected() {
      Set<String> cidrs = ImmutableSet.of("0.0.0.0/1", "128.0.0.0/1");
      return ImmutableSet.<PortForwardingRule> of(
         PortForwardingRule.builder().id("15").privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
            .publicPort(2022).virtualMachineId("3").virtualMachineName("i-3-3-VM").IPAddressId("3")
            .IPAddress("72.52.126.32").state(PortForwardingRule.State.ACTIVE).CIDRs(cidrs).build(),
         PortForwardingRule.builder().id("18").privatePort(22).protocol(PortForwardingRule.Protocol.TCP)
            .publicPort(22).virtualMachineId("89").virtualMachineName("i-3-89-VM").IPAddressId("34")
            .IPAddress("72.52.126.63").state(PortForwardingRule.State.ACTIVE).build());
   }

}
