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
import java.util.Set;

import org.jclouds.elb.domain.Policy;
import org.jclouds.elb.xml.DescribeLoadBalancerPoliciesResultHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribePoliciesResponseTest")
public class DescribeLoadBalancerPoliciesResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_policies.xml");

      Set<Policy> expected = expected();

      DescribeLoadBalancerPoliciesResultHandler handler = injector.getInstance(DescribeLoadBalancerPoliciesResultHandler.class);
      Set<Policy> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<Policy> expected() {
      return ImmutableSet.of(
               Policy.builder()
                     .name("ELBSample-OpenSSLDefaultNegotiationPolicy")
                     .typeName("SSLNegotiationPolicyType")
                     .attribute("Protocol-SSLv2", false)
                     .attribute("ADH-AES256-SHA", false)
                     .attribute("DHE-RSA-AES256-SHA", true)
                     .build(),
               Policy.builder()
                     .name("ELBSample-ELBDefaultNegotiationPolicy")
                     .typeName("SSLNegotiationPolicyType")
                     .attribute("Protocol-SSLv2", false)
                     .attribute("EDH-DSS-DES-CBC3-SHA", false)
                     .build()
               );
   }
}
