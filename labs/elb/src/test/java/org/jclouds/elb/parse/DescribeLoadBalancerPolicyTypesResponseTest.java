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

import org.jclouds.elb.domain.AttributeMetadata;
import org.jclouds.elb.domain.AttributeMetadata.Cardinality;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.xml.DescribeLoadBalancerPolicyTypesResultHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeLoadBalancerPolicyTypesResponseTest")
public class DescribeLoadBalancerPolicyTypesResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_policy_types.xml");

      Set<PolicyType> expected = expected();

      DescribeLoadBalancerPolicyTypesResultHandler handler = injector.getInstance(DescribeLoadBalancerPolicyTypesResultHandler.class);
      Set<PolicyType> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<PolicyType> expected() {
      return ImmutableSet.<PolicyType>builder()
            .add(PolicyType.builder()
                           .attributeMetadata(AttributeMetadata.<String>builder()
                                                               .name("PublicKey")
                                                               .type(String.class)
                                                               .rawType("String")
                                                               .cardinality(Cardinality.ONE)
                                                               .build())
                           .name("PublicKeyPolicyType")
                           .description("Policy containing a list of public keys to accept when authenticating the back-end server(s). This policy cannot be applied directly to back-end servers or listeners but must be part of a BackendServerAuthenticationPolicyType.")
                           .build())
            .add(PolicyType.builder()
                           .attributeMetadata(AttributeMetadata.<String>builder()
                                                               .name("CookieName")
                                                               .type(String.class)
                                                               .rawType("String")
                                                               .cardinality(Cardinality.ONE).build())
                           .name("AppCookieStickinessPolicyType")
                           .description("Stickiness policy with session lifetimes controlled by the lifetime of the application-generated cookie. This policy can be associated only with HTTP/HTTPS listeners.")
                           .build())
            .add(PolicyType.builder()
                           .attributeMetadata(AttributeMetadata.<Long>builder()
                                                               .name("CookieExpirationPeriod")
                                                               .type(Long.class)
                                                               .rawType("Long")
                                                               .cardinality(Cardinality.ZERO_OR_ONE).build())
                           .name("LBCookieStickinessPolicyType")
                           .description("Stickiness policy with session lifetimes controlled by the browser (user-agent) or a specified expiration period. This policy can be associated only with HTTP/HTTPS listeners.")
                           .build())
            .add(PolicyType.builder()
                           .attributeMetadata(AttributeMetadata.<Boolean>builder()
                                                               .name("Protocol-SSLv2")
                                                               .type(Boolean.class)
                                                               .rawType("Boolean")
                                                               .defaultValue(false)
                                                               .cardinality(Cardinality.ZERO_OR_ONE).build())
                           .attributeMetadata(AttributeMetadata.<Boolean>builder()
                                                               .name("Protocol-TLSv1")
                                                               .type(Boolean.class)
                                                               .rawType("Boolean")
                                                               .defaultValue(true)
                                                               .cardinality(Cardinality.ZERO_OR_ONE).build())
                           .name("SSLNegotiationPolicyType")
                           .description("Listener policy that defines the ciphers and protocols that will be accepted by the load balancer. This policy can be associated only with HTTPS/SSL listeners.")
                           .build())
            .add(PolicyType.builder()
                           .attributeMetadata(AttributeMetadata.<String>builder()
                                                               .name("PublicKeyPolicyName")
                                                               .type(String.class)
                                                               .rawType("PolicyName")
                                                               .cardinality(Cardinality.ONE_OR_MORE).build())
                           .name("BackendServerAuthenticationPolicyType")
                           .description("Policy that controls authentication to back-end server(s) and contains one or more policies, such as an instance of a PublicKeyPolicyType. This policy can be associated only with back-end servers that are using HTTPS/SSL.")
                           .build()).build();
   }
}
