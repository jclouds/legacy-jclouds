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

import org.jclouds.cloudstack.domain.FirewallRule;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListFirewallRulesResponseTest extends BaseSetParserTest<FirewallRule> {

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
      return "/listfirewallrulesresponse.json";
   }

   @Override
   @SelectJson("firewallrule")
   public Set<FirewallRule> expected() {
      Set<String> CIDRs = ImmutableSet.of("0.0.0.0/0");
      return ImmutableSet.of(
         FirewallRule.builder().id("2017").protocol(FirewallRule.Protocol.TCP).startPort(30)
            .endPort(35).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE).CIDRs(CIDRs).build(),
         FirewallRule.builder().id("2016").protocol(FirewallRule.Protocol.TCP).startPort(22)
            .endPort(22).ipAddressId("2").ipAddress("10.27.27.51").state(FirewallRule.State.ACTIVE).CIDRs(CIDRs).build(),
         FirewallRule.builder().id("10").protocol(FirewallRule.Protocol.TCP).startPort(22)
            .endPort(22).ipAddressId("8").ipAddress("10.27.27.57").state(FirewallRule.State.ACTIVE).CIDRs(CIDRs).build()
      );
   }

}
