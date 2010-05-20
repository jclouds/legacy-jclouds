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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.AppCookieStickinessPolicy;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.LBCookieStickinessPolicy;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.LoadBalancerListener;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeLoadBalancersResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeLoadBalancersResponseHandlerTest")
public class DescribeLoadBalancerResponseHandlerTest extends BaseEC2HandlerTest {

   public void testParse() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_loadbalancers.xml");
      
      Set<ElasticLoadBalancer> contents = Sets.newHashSet();
      ElasticLoadBalancer dummy = new ElasticLoadBalancer(defaultRegion, "my-load-balancer",
              ImmutableSet.of("i-5b33e630", "i-8f26d7e4", "i-5933e632"), ImmutableSet.of("us-east-1a"),
      "my-load-balancer-1400212309.us-east-1.elb.amazonaws.com");
      contents.add(dummy);

      Set<ElasticLoadBalancer> result = parseLoadBalancers(is);

      assertEquals(result, contents);
   }


   private Set<ElasticLoadBalancer> parseLoadBalancers(InputStream is) {
       DescribeLoadBalancersResponseHandler handler = injector
                .getInstance(DescribeLoadBalancersResponseHandler.class);
       addDefaultRegionToHandler(handler);
       Set<ElasticLoadBalancer> result = factory.create(handler).parse(is);
       return result;
    }

    private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
       GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
       expect(request.getArgs()).andReturn(new Object[] { null });
       replay(request);
       handler.setContext(request);
    }
}
