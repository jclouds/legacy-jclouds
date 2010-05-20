/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeLoadBalancersResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.RegisterInstancesWithLoadBalancerResponseHandlerTest")
public class RegisterInstancesWithLoadBalancerResponseHandlerTest extends BaseEC2HandlerTest {

   public void testParse() {
      InputStream is = getClass().getResourceAsStream(
               "/ec2/register_instances_with_loadbalancer.xml");

      Set<String> instanceIds = Sets.newHashSet();
      instanceIds.add("i-6055fa09");
      instanceIds.add("i-9055fa55");

      Set<String> result = parseXML(is);

      assertEquals(result, instanceIds);
   }

   private Set<String> parseXML(InputStream is) {
      RegisterInstancesWithLoadBalancerResponseHandler handler = injector
               .getInstance(RegisterInstancesWithLoadBalancerResponseHandler.class);
      Set<String> result = factory.create(handler).parse(is);
      return result;
   }

}
