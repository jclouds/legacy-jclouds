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
package org.jclouds.elb.options;

import static org.jclouds.elb.options.ListPoliciesOptions.Builder.loadBalancerName;
import static org.jclouds.elb.options.ListPoliciesOptions.Builder.name;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ListPoliciesOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ListPoliciesOptionsTest")
public class ListPoliciesOptionsTest {
   
   public void testLoadBalancerName() {
      ListPoliciesOptions options = new ListPoliciesOptions().loadBalancerName("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("LoadBalancerName"));
   }

   public void testLoadBalancerNameStatic() {
      ListPoliciesOptions options = loadBalancerName("FFFFF");
      assertEquals(ImmutableSet.of("FFFFF"), options.buildFormParameters().get("LoadBalancerName"));
   }

   public void testName() {
      ListPoliciesOptions options = new ListPoliciesOptions().name("my-load-balancer");
      assertEquals(ImmutableSet.of("my-load-balancer"), options.buildFormParameters().get("PolicyNames.member.1"));
   }

   public void testNameStatic() {
      ListPoliciesOptions options = name("my-load-balancer");
      assertEquals(ImmutableSet.of("my-load-balancer"), options.buildFormParameters().get("PolicyNames.member.1"));
   }

}
