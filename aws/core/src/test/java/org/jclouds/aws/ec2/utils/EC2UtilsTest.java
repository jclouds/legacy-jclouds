/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.aws.ec2.utils;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.util.EC2Utils;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EC2UtilsTest {

   public void testGetLoadBalancerNameAndRegionFromDnsName() {
      assertEquals(
               ImmutableMap.<String, String> of("us-east-1", "my-load-balancer"),
               EC2Utils
                        .getLoadBalancerNameAndRegionFromDnsName("my-load-balancer-1277832914.us-east-1.elb.amazonaws.com"));
      assertEquals(
               ImmutableMap.<String, String> of("us-east-1", "ec2lb"),
               EC2Utils
                        .getLoadBalancerNameAndRegionFromDnsName("ec2lb-915583419.us-east-1.elb.amazonaws.com"));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testGetLoadBalancerNameAndRegionFromDnsNameFail() {
      EC2Utils
               .getLoadBalancerNameAndRegionFromDnsName("my-load-balancer-1277832914.us-east-1.microsoft.com");
   }

}
