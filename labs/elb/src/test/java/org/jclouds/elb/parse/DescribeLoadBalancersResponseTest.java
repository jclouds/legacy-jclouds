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
package org.jclouds.elb.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.collect.PaginatedSet;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.elb.domain.ListenerWithPolicies;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.Protocol;
import org.jclouds.elb.xml.DescribeLoadBalancersResultHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeLoadBalancersResponseTest")
public class DescribeLoadBalancersResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_loadbalancers.xml");

      PaginatedSet<LoadBalancer> expected = expected();

      DescribeLoadBalancersResultHandler handler = injector.getInstance(DescribeLoadBalancersResultHandler.class);
      PaginatedSet<LoadBalancer> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public PaginatedSet<LoadBalancer> expected() {
      return PaginatedSet.copyOf(ImmutableSet.of(
               LoadBalancer.builder()
                    .name("my-load-balancer")
                    .createdTime(new SimpleDateFormatDateService().iso8601DateParse("2010-03-03T20:54:45.110Z"))
                    .dnsName("my-load-balancer-1400212309.us-east-1.elb.amazonaws.com")
                    .instanceIds(ImmutableSet.of("i-5b33e630", "i-8f26d7e4", "i-5933e632"))
                    .listener(ListenerWithPolicies.builder().protocol(Protocol.HTTP).port(80).instancePort(80).build())
                    .listener(ListenerWithPolicies.builder().protocol(Protocol.TCP).port(443).instancePort(443).build())
                    .build()));
   }

}
