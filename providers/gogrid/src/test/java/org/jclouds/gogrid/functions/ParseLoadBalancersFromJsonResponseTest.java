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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.SortedSet;

import javax.inject.Singleton;

import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.IpPortPair;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.domain.LoadBalancerOs;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerState;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseLoadBalancersFromJsonResponseTest")
public class ParseLoadBalancersFromJsonResponseTest {

   @Test
   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_get_load_balancer_list.json");

      ParseLoadBalancerListFromJsonResponse parser = i.getInstance(ParseLoadBalancerListFromJsonResponse.class);
      SortedSet<LoadBalancer> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      Option dc = Option.createWithIdNameAndDescription(1l, "US-West-1", "US West 1 Datacenter");

      LoadBalancer loadBalancer = LoadBalancer.builder().id(6372L).name("Balancer")
            .virtualIp(IpPortPair.builder().ip(Ip.builder().id(1313082L)
                  .ip("204.51.240.181").subnet("204.51.240.176/255.255.255.240").isPublic(true).state(IpState.ASSIGNED).datacenter(dc).build()).port(80).build())
            .realIpList(
                  IpPortPair.builder().ip(Ip.builder().id(1313086L).ip("204.51.240.185").subnet("204.51.240.176/255.255.255.240")
                        .isPublic(true).state(IpState.ASSIGNED).datacenter(dc).build()).port(80).build(),
                  IpPortPair.builder().ip(Ip.builder().id(1313089L).ip("204.51.240.188").subnet("204.51.240.176/255.255.255.240")
                        .isPublic(true).state(IpState.ASSIGNED).datacenter(dc).build()).port(80).build())
            .type(LoadBalancerType.ROUND_ROBIN).persistence(LoadBalancerPersistenceType.NONE)
            .os(LoadBalancerOs.F5).state(LoadBalancerState.ON).datacenter(dc).build();

      assertEquals(Iterables.getOnlyElement(response), loadBalancer);
   }

   Injector i = Guice.createInjector(new GsonModule() {
      @Override
      protected void configure() {
         bind(DateAdapter.class).to(LongDateAdapter.class);
         super.configure();
      }

      @Provides
      @Singleton
      public Map<Type, Object> provideCustomAdapterBindings() {
         Map<Type, Object> bindings = Maps.newHashMap();
         bindings.put(LoadBalancerOs.class, new CustomDeserializers.LoadBalancerOsAdapter());
         bindings.put(LoadBalancerState.class, new CustomDeserializers.LoadBalancerStateAdapter());
         bindings.put(LoadBalancerPersistenceType.class, new CustomDeserializers.LoadBalancerPersistenceTypeAdapter());
         bindings.put(LoadBalancerType.class, new CustomDeserializers.LoadBalancerTypeAdapter());
         bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
         return bindings;
      }
   });
}
