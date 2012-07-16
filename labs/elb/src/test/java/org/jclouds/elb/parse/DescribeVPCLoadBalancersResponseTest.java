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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.elb.domain.HealthCheck;
import org.jclouds.elb.domain.ListenerWithPolicies;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.domain.Protocol;
import org.jclouds.elb.domain.Scheme;
import org.jclouds.elb.xml.DescribeLoadBalancersResultHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeVPCLoadBalancersResponseTest")
public class DescribeVPCLoadBalancersResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_loadbalancers_vpc.xml");

      IterableWithMarker<LoadBalancer> expected = expected();

      DescribeLoadBalancersResultHandler handler = injector.getInstance(DescribeLoadBalancersResultHandler.class);
      IterableWithMarker<LoadBalancer> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public IterableWithMarker<LoadBalancer> expected() {
      return IterableWithMarkers.from(ImmutableSet.of(
               LoadBalancer.builder()
                    .name("tests")
                    .createdTime(new SimpleDateFormatDateService().iso8601DateParse("2012-07-08T19:54:24.190Z"))
                    .dnsName("tests-144598086.us-east-1.elb.amazonaws.com")
                    .healthCheck(HealthCheck.builder()
                                            .interval(30)
                                            .target("TCP:22")
                                            .healthyThreshold(10)
                                            .timeout(5)
                                            .unhealthyThreshold(2).build())
                    .instanceIds(ImmutableSet.of("i-64bd081c"))
                    .listener(ListenerWithPolicies.builder()
                                                  .policyName("AWSConsolePolicy-1")
                                                  .protocol(Protocol.HTTP)
                                                  .port(80).build())
                    .listener(ListenerWithPolicies.builder()
                                                  .protocol(Protocol.TCP)
                                                  .port(25)
                                                  .instancePort(22).build())
                    .availabilityZone("us-east-1e")
                    .VPCId("vpc-56e10e3d")
                    .scheme(Scheme.INTERNET_FACING)
                    .subnet("subnet-28e10e43")
                    .securityGroup("sg-6ba54204")
                    .securityGroup("sg-99a641f6")
                    .hostedZoneName("tests-144598086.us-east-1.elb.amazonaws.com")
                    .hostedZoneId("Z3DZXE0Q79N41H")
                    .build()));
   }

}
