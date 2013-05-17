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
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer.Status;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ConvertLB;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ParseLoadBalancer;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "UnwrapLoadBalancerWhenDeletedTest")
public class ParseLoadBalancerWhenDeletedTest extends BaseItemParserTest<LoadBalancer> {

   @Override
   public String resource() {
      return "/loadbalancer-get-deleted.json";
   }

   @Override
   public LoadBalancer expected() {
      return LoadBalancer.builder().region("LON").id(4865).name("adriancole-LON").status(Status.DELETED).nodeCount(0)
            .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-12-05T18:03:23Z"))
            .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-12-05T18:04:04Z"))
            .uri(URI.create("https://lon.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/4865")).build();
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
      return i.getInstance(ParseLoadBalancer.class).setEndpointAndRegion(URI.create("https://lon.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/4865"));
   }
}
