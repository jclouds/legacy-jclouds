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

import org.jclouds.cloudstack.domain.LoadBalancerRule;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class ListLoadBalancerRulesResponseTest extends BaseSetParserTest<LoadBalancerRule> {

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
      return "/listloadbalancerrulesresponse.json";
   }

   @Override
   @SelectJson("loadbalancerrule")
   public Set<LoadBalancerRule> expected() {
      return ImmutableSet.<LoadBalancerRule> of(LoadBalancerRule.builder()
         .id("93").account("admin").algorithm(LoadBalancerRule.Algorithm.ROUNDROBIN)
         .description("null").domain("ROOT").domainId("1").name("Ranny").privatePort(80)
         .publicIP("10.27.27.59").publicIPId("10").publicPort(80).state(LoadBalancerRule.State.ADD)
         .CIDRs(Sets.<String>newHashSet()).zoneId(null)
         .build());
   }

}
