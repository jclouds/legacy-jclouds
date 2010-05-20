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
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.aws.ec2.xml.RegisterInstancesWithLoadBalancerResponseHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ElasticLoadBalancerAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.ElasticLoadBalancerAsyncClientTest")
public class ElasticLoadBalancerAsyncClientTest extends
         BaseEC2AsyncClientTest<ElasticLoadBalancerAsyncClient> {

   public void testRegisterInstancesWithLoadBalancer() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticLoadBalancerAsyncClient.class.getMethod(
               "registerInstancesWithLoadBalancerInRegion", String.class, String.class, String[].class);

      GeneratedHttpRequest<ElasticLoadBalancerAsyncClient> httpMethod = processor.createRequest(
               method, null, "ReferenceAP1", "i-6055fa09");

      assertRequestLineEquals(httpMethod,
               "POST https://elasticloadbalancing.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 89\nContent-Type: application/x-www-form-urlencoded\nHost: elasticloadbalancing.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-25&Action=RegisterInstancesWithLoadBalancer&LoadBalancerName=ReferenceAP1&Instances.member.1.InstanceId=i-6055fa09");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method,
               RegisterInstancesWithLoadBalancerResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ElasticLoadBalancerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ElasticLoadBalancerAsyncClient>>() {
      };
   }

}
