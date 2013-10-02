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
package org.jclouds.rackspace.cloudloadbalancers.v1.functions;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.AccessRuleWithId;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.ConnectionThrottle;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HealthMonitor;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SSLTermination;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SessionPersistence;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.SourceAddresses;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIPWithId;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HealthMonitor.Type;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer.Algorithm;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ConvertLB;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancer;
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
      Metadata metadata = new Metadata();
      metadata.put("color", "red");
      metadata.putId("color", 1);
      metadata.put("label", "web-load-balancer");
      metadata.putId("label", 2);
      
      return LoadBalancer
            .builder()
            .region("DFW")
            .id(2000)
            .name("sample-loadbalancer")
            .protocol("HTTP")
            .port(80)
            .algorithm(Algorithm.RANDOM)
            .status(Status.ACTIVE)
            .connectionLogging(true)
            .contentCaching(true)
            .nodeCount(2)
            .halfClosed(false)
            .healthMonitor(HealthMonitor.builder().type(Type.CONNECT).delay(10).timeout(5).attemptsBeforeDeactivation(2).build())
            .sslTermination(SSLTermination.builder().enabled(true).secureTrafficOnly(false).securePort(443).build())
            .sourceAddresses(SourceAddresses.builder().ipv6Public("2001:4800:7901::5/64").ipv4Public("174.143.139.137").ipv4Servicenet("10.183.250.137").build())
            .connectionThrottle(ConnectionThrottle.builder().maxConnections(100).minConnections(10).maxConnectionRate(50).rateInterval(60).build())
            .accessRules(ImmutableSet.of(
                  new AccessRuleWithId(22215, "1.2.3.4/32", AccessRule.Type.DENY),
                  new AccessRuleWithId(22217, "12.0.0.0/8", AccessRule.Type.ALLOW)))
            .virtualIPs(ImmutableSet.of(
                  new VirtualIPWithId(VirtualIP.Type.PUBLIC, VirtualIP.IPVersion.IPV4, 1000, "206.10.10.210"),
                  new VirtualIPWithId(VirtualIP.Type.PUBLIC, VirtualIP.IPVersion.IPV6, 1001, "2001:4800:7901:0000:9a32:3c2a:0000:0001")))
            .nodes(ImmutableSet.of(
                  Node.builder().id(1041).address("10.1.1.1").port(80).condition(Node.Condition.ENABLED).status(Node.Status.ONLINE).build(), 
                  Node.builder().id(1411).address("10.1.1.2").port(80).condition(Node.Condition.ENABLED).status(Node.Status.ONLINE).build()))
            .sessionPersistenceType(SessionPersistence.HTTP_COOKIE)
            .clusterName("c1.dfw1")
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:42Z"))
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:44Z"))
            .metadata(metadata)
            .uri(URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000")).build();
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
      return i.getInstance(ParseLoadBalancer.class).setEndpointAndRegion(URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000"));
   }
}
