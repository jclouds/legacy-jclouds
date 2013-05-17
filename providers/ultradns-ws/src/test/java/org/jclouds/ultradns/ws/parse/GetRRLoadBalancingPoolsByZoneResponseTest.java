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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.RoundRobinPool;
import org.jclouds.ultradns.ws.xml.RoundRobinPoolListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetRRLoadBalancingPoolsByZoneResponseTest")
public class GetRRLoadBalancingPoolsByZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/rrpools.xml");

      FluentIterable<RoundRobinPool> expected = expected();

      RoundRobinPoolListHandler handler = injector.getInstance(RoundRobinPoolListHandler.class);
      FluentIterable<RoundRobinPool> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<RoundRobinPool> expected() {
      return FluentIterable.from(ImmutableList.<RoundRobinPool> builder()
                           .add(RoundRobinPool.builder()
                                              .zoneId("0000000000000001")
                                              .id("000000000000002")
                                              .name("uswest1")
                                              .dname("app-uswest1.jclouds.org.").build())
                           .add(RoundRobinPool.builder()
                                              .zoneId("0000000000000001")
                                              .id("000000000000003")
                                              .name("uswest2")
                                              .dname("app-uswest2.jclouds.org.").build())
                           .add(RoundRobinPool.builder()
                                              .zoneId("0000000000000001")
                                              .id("000000000000004")
                                              .name("euwest")
                                              .dname("app-euwest.jclouds.org.").build()).build());
   }
}
