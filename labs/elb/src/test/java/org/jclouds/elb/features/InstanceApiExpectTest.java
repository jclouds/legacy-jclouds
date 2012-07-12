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
 * Unles required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either expres or implied.  See the License for the
 * specific language governing permisions and limitations
 * under the License.
 */
package org.jclouds.elb.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.internal.BaseELBApiExpectTest;
import org.jclouds.elb.parse.DescribeInstanceHealthResponseTest;
import org.jclouds.elb.parse.InstancesResultHandlerTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "InstanceApiExpectTest")
public class InstanceApiExpectTest extends BaseELBApiExpectTest {

   public InstanceApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                                .headers(ImmutableMultimap.<String, String> builder()
                                          .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                          .build())
                                .payload(
                                    payloadFromStringWithContentType(
                                          "Action=DescribeInstanceHealth" +
                                                "&LoadBalancerName=name" +
                                                "&Signature=zIwSuvkooYRNPLyDrPCF8%2BbMLA8t0n9hIlS6K2aahuA%3D" +
                                                "&SignatureMethod=HmacSHA256" +
                                                "&SignatureVersion=2" +
                                                "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                "&Version=2012-06-01" +
                                                "&AWSAccessKeyId=identity",
                                          "application/x-www-form-urlencoded"))
                                .build();
   
   
   public void testGetHealthOfInstancesOfLoadBalancerIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_instancehealth.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getInstanceApi().getHealthOfInstancesOfLoadBalancer("name").toString(), new DescribeInstanceHealthResponseTest().expected().toString());
   }

   public void testGetHealthOfInstancesOfLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenDontExist.getInstanceApi().getHealthOfInstancesOfLoadBalancer("name"), ImmutableSet.of());
   }

   HttpRequest registerInstanceWithLoadBalancer = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                                    .headers(ImmutableMultimap.<String, String> builder()
                                             .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                             .build())
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=RegisterInstancesWithLoadBalancer" +
                                                   "&Instances.member.1.InstanceId=i-6055fa09" +
                                                   "&LoadBalancerName=name" +
                                                   "&Signature=YRYjrZGMNoeyghtfKvbMZbRrbIgCuxsCQeYdtai0chY%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
   
   
   public void testRegisterInstanceWithLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/instances.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            registerInstanceWithLoadBalancer, getResponse);

      assertEquals(apiWhenExist.getInstanceApi().registerInstanceWithLoadBalancer("i-6055fa09", "name").toString(), new InstancesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRegisterInstanceWithLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            registerInstanceWithLoadBalancer, getResponse);

      apiWhenDontExist.getInstanceApi().registerInstanceWithLoadBalancer("i-6055fa09", "name");
   }
   
   HttpRequest registerInstancesWithLoadBalancer = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                     .build())
            .payload(
               payloadFromStringWithContentType(
                     "Action=RegisterInstancesWithLoadBalancer" +
                           "&Instances.member.1.InstanceId=i-6055fa09" +
                           "&Instances.member.2.InstanceId=i-9055fa55" +
                           "&LoadBalancerName=name" +
                           "&Signature=Yfqg8TxL1J1Ug8SimY%2F30rnbt%2FUVygTEa0vhMT5Fz1Y%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();


   public void testRegisterInstancesWithLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/instances.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(registerInstancesWithLoadBalancer, getResponse);

      assertEquals(
               apiWhenExist.getInstanceApi().registerInstancesWithLoadBalancer(ImmutableSet.of("i-6055fa09", "i-9055fa55"), "name")
                        .toString(), new InstancesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRegisterInstancesWithLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(registerInstancesWithLoadBalancer, getResponse);

      apiWhenDontExist.getInstanceApi().registerInstancesWithLoadBalancer(ImmutableSet.of("i-6055fa09", "i-9055fa55"), "name");
   }   
   

   HttpRequest deregisterInstanceFromLoadBalancer = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                                    .headers(ImmutableMultimap.<String, String> builder()
                                             .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                             .build())
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=DeregisterInstancesFromLoadBalancer" +
                                                   "&Instances.member.1.InstanceId=i-6055fa09" +
                                                   "&LoadBalancerName=name" +
                                                   "&Signature=d%2BK6b2ggJLEekW8wLyRnm%2FpcEpZvc8VNI%2FW0bpYBGUk%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
   
   
   public void testDeregisterInstanceFromLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/instances.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            deregisterInstanceFromLoadBalancer, getResponse);

      assertEquals(apiWhenExist.getInstanceApi().deregisterInstanceFromLoadBalancer("i-6055fa09", "name").toString(), new InstancesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeregisterInstanceFromLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            deregisterInstanceFromLoadBalancer, getResponse);

      apiWhenDontExist.getInstanceApi().deregisterInstanceFromLoadBalancer("i-6055fa09", "name");
   }
   
   HttpRequest deregisterInstancesFromLoadBalancer = HttpRequest.builder()
            .method("POST")
            .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                     .build())
            .payload(
               payloadFromStringWithContentType(
                     "Action=DeregisterInstancesFromLoadBalancer" +
                           "&Instances.member.1.InstanceId=i-6055fa09" +
                           "&Instances.member.2.InstanceId=i-9055fa55" +
                           "&LoadBalancerName=name" +
                           "&Signature=nqn8iH70979k%2Fu%2FKXEcMlT1Zd%2FPaNK6ZBwFDjvbuMRo%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();


   public void testDeregisterInstancesFromLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/instances.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(deregisterInstancesFromLoadBalancer, getResponse);

      assertEquals(
               apiWhenExist.getInstanceApi().deregisterInstancesFromLoadBalancer(ImmutableSet.of("i-6055fa09", "i-9055fa55"), "name")
                        .toString(), new InstancesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testDeregisterInstancesFromLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(deregisterInstancesFromLoadBalancer, getResponse);

      apiWhenDontExist.getInstanceApi().deregisterInstancesFromLoadBalancer(ImmutableSet.of("i-6055fa09", "i-9055fa55"), "name");
   }   
}
