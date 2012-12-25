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

import java.util.TimeZone;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.internal.BaseELBApiExpectTest;
import org.jclouds.elb.parse.AvailabilityZonesResultHandlerTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AvailabilityZoneApiExpectTest")
public class AvailabilityZoneApiExpectTest extends BaseELBApiExpectTest {

   public AvailabilityZoneApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }
   
   HttpRequest addZoneToLoadBalancer = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                    .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=EnableAvailabilityZonesForLoadBalancer" +
                                                   "&AvailabilityZones.member.1=us-east-1a" +
                                                   "&LoadBalancerName=name" +
                                                   "&Signature=lay8JNIpYsgWjiTbA4/rgKrQPWhFKToPxw/fCLld4SE%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
   
   
   public void testAddZoneToLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/zones.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            addZoneToLoadBalancer, getResponse);

      assertEquals(apiWhenExist.getAvailabilityZoneApi().addAvailabilityZoneToLoadBalancer("us-east-1a", "name").toString(), new AvailabilityZonesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddZoneToLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            addZoneToLoadBalancer, getResponse);

      apiWhenDontExist.getAvailabilityZoneApi().addAvailabilityZoneToLoadBalancer("us-east-1a", "name");
   }
   
   HttpRequest addZonesToLoadBalancer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
            .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                     "Action=EnableAvailabilityZonesForLoadBalancer" +
                           "&AvailabilityZones.member.1=us-east-1a" +
                           "&AvailabilityZones.member.2=us-east-1b" +
                           "&LoadBalancerName=name" +
                           "&Signature=RAX1VLJU30B47RFUiywtknhgD2DxZygJ2niOO4UnW3U%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();


   public void testAddZonesToLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/zones.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(addZonesToLoadBalancer, getResponse);

      assertEquals(
               apiWhenExist.getAvailabilityZoneApi().addAvailabilityZonesToLoadBalancer(ImmutableSet.of("us-east-1a", "us-east-1b"), "name")
                        .toString(), new AvailabilityZonesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testAddZonesToLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(addZonesToLoadBalancer, getResponse);

      apiWhenDontExist.getAvailabilityZoneApi().addAvailabilityZonesToLoadBalancer(ImmutableSet.of("us-east-1a", "us-east-1b"), "name");
   }   
   

   HttpRequest removeZoneFromLoadBalancer = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                    .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=DisableAvailabilityZonesForLoadBalancer" +
                                                   "&AvailabilityZones.member.1=us-east-1a" +
                                                   "&LoadBalancerName=name" +
                                                   "&Signature=tjzaFDhUghKwTpe/9OC8JK%2BJsRMCkF3Kh5YkvPEDPbg%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
   
   
   public void testRemoveZoneFromLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/zones.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            removeZoneFromLoadBalancer, getResponse);

      assertEquals(apiWhenExist.getAvailabilityZoneApi().removeAvailabilityZoneFromLoadBalancer("us-east-1a", "name").toString(), new AvailabilityZonesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveZoneFromLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            removeZoneFromLoadBalancer, getResponse);

      apiWhenDontExist.getAvailabilityZoneApi().removeAvailabilityZoneFromLoadBalancer("us-east-1a", "name");
   }
   
   HttpRequest removeZonesFromLoadBalancer = HttpRequest.builder()
            .method("POST")
            .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
            .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
            .payload(
               payloadFromStringWithContentType(
                     "Action=DisableAvailabilityZonesForLoadBalancer" +
                           "&AvailabilityZones.member.1=us-east-1a" +
                           "&AvailabilityZones.member.2=us-east-1b" +
                           "&LoadBalancerName=name" +
                           "&Signature=5yUJQXjfntl0ptL%2BDv3p2jYpDSr%2BmV8hASIS7wtvkOI%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded"))
            .build();


   public void testRemoveZonesFromLoadBalancerWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/zones.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(removeZonesFromLoadBalancer, getResponse);

      assertEquals(
               apiWhenExist.getAvailabilityZoneApi().removeAvailabilityZonesFromLoadBalancer(ImmutableSet.of("us-east-1a", "us-east-1b"), "name")
                        .toString(), new AvailabilityZonesResultHandlerTest().expected().toString());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testRemoveZonesFromLoadBalancerWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(removeZonesFromLoadBalancer, getResponse);

      apiWhenDontExist.getAvailabilityZoneApi().removeAvailabilityZonesFromLoadBalancer(ImmutableSet.of("us-east-1a", "us-east-1b"), "name");
   }   
}
