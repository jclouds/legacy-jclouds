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

package org.jclouds.aws.elb;

import org.jclouds.elb.ELBClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ELBClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "AWSELBClientLiveTest")
public class AWSELBClientLiveTest extends ELBClientLiveTest {
   public AWSELBClientLiveTest() {
      provider = "aws-elb";
   }

   @Test
   public void testCreateLoadBalancer() {
      for (String region : AWSELBPropertiesBuilder.DEFAULT_REGIONS) {
         createLoadBalancerInRegionZone(region, region + "a", name);
      }
   }

   @Test(dependsOnMethods = "testCreateLoadBalancer")
   public void testDescribeLoadBalancers() {
      for (String region : AWSELBPropertiesBuilder.DEFAULT_REGIONS) {
         describeLoadBalancerInRegion(region);
      }
   }

   @Test
   public void testDeleteLoadBalancer() {
      for (String region : AWSELBPropertiesBuilder.DEFAULT_REGIONS) {
         deleteLoadBalancerInRegion(region);
      }
   }

}
