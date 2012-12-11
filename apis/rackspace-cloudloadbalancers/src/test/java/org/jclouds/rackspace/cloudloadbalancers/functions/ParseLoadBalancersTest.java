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

import java.util.Set;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.BaseIterableWithMarkerParserTest;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP;
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
@Test(groups = "unit")
public class ParseLoadBalancersTest extends BaseIterableWithMarkerParserTest<LoadBalancer> {

   public Set<LoadBalancer> data() {
      return ImmutableSet.of(
            LoadBalancer
                  .builder()
                  .region("DFW")
                  .name("lb-site1")
                  .id(71)
                  .protocol("HTTP")
                  .port(80)
                  .algorithm("RANDOM")
                  .status(Status.ACTIVE)
                  .nodeCount(1)
                  .virtualIPs(ImmutableSet.of(
                        VirtualIP.builder().id(403).address("206.55.130.1")
                              .type(VirtualIP.Type.PUBLIC).ipVersion(VirtualIP.IPVersion.IPV4).build()))
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:42Z"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:44Z")).build(),
            LoadBalancer
                  .builder()
                  .region("DFW")
                  .name("lb-site2")
                  .id(166)
                  .protocol("HTTP")
                  .port(80)
                  .algorithm("RANDOM")
                  .status(Status.ACTIVE)
                  .nodeCount(1)
                  .virtualIPs(ImmutableSet.of(
                        VirtualIP.builder().id(401).address("206.55.130.2")
                              .type(VirtualIP.Type.PUBLIC).ipVersion(VirtualIP.IPVersion.IPV4).build()))
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:42Z"))
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2010-11-30T03:23:44Z")).build());
   }
   
   @Override
   public String resource() {
      return "/loadbalancers-list.json";
   }

   @Override
   public IterableWithMarker<LoadBalancer> expected() {
      return IterableWithMarkers.from(data());
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
   protected Function<HttpResponse, IterableWithMarker<LoadBalancer>> parser(Injector i) {
      return i.getInstance(ParseLoadBalancers.class).setRegion("DFW");
   }
}
