/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.functions;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP.IPVersion;
import org.jclouds.rackspace.cloudloadbalancers.functions.ConvertLB;
import org.jclouds.rackspace.cloudloadbalancers.functions.ParseLoadBalancer;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "UnwrapLoadBalancerTest")
public class ParseLoadBalancerTest extends BaseItemParserTest<LoadBalancer> {

   @Override
   public String resource() {
      return "/loadbalancer-get.json";
   }

   @Override
   public LoadBalancer expected() {
      return LoadBalancer
            .builder()
            .region("DFW")
            .id(2000)
            .name("sample-loadbalancer")
            .protocol("HTTP")
            .port(80)
            .algorithm("RANDOM")
            .status(Status.ACTIVE)
            .connectionLoggingEnabled(true)
            .nodeCount(2)
            .virtualIPs(ImmutableSet.of(
                  VirtualIP.builder().id(1000).address("206.10.10.210").type(VirtualIP.Type.PUBLIC).ipVersion(IPVersion.IPV4).build()))
            .nodes(ImmutableSet.of(
                  Node.builder().id(1041).address("10.1.1.1").port(80).condition(Node.Condition.ENABLED).status(Node.Status.ONLINE).build(), 
                  Node.builder().id(1411).address("10.1.1.2").port(80).condition(Node.Condition.ENABLED).status(Node.Status.ONLINE).build()))
            .sessionPersistenceType("HTTP_COOKIE")
            .clusterName("c1.dfw1")
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:42Z"))
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:44Z")).build();
   }

   // add factory binding as this is not default
   @Override
   protected Injector injector() {
      return super.injector().createChildInjector(new AbstractModule() {

         @Override
         protected void configure() {
            install(new FactoryModuleBuilder().build(ConvertLB.Factory.class));
         }

      });

   }

   @Override
   protected Function<HttpResponse, LoadBalancer> parser(Injector i) {
      return i.getInstance(ParseLoadBalancer.class).setRegion("DFW");
   }
}
