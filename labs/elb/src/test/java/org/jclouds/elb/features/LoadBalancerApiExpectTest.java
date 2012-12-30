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

import static org.jclouds.elb.options.ListLoadBalancersOptions.Builder.afterMarker;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.TimeZone;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.internal.BaseELBApiExpectTest;
import org.jclouds.elb.parse.DescribeLoadBalancersResponseTest;
import org.jclouds.elb.parse.GetLoadBalancerResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LoadBalancerApiExpectTest")
public class LoadBalancerApiExpectTest extends BaseELBApiExpectTest {

   public LoadBalancerApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest get = HttpRequest.builder()
                               .method("POST")
                               .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                               .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                               .payload(
                                  payloadFromStringWithContentType(
                                       "Action=DescribeLoadBalancers" +
                                             "&LoadBalancerNames.member.1=name" +
                                             "&Signature=EYzZgYDMGi9uFZU%2BVh/mmsJ9KmHxm5vEAF%2BhGF12BP4%3D" +
                                             "&SignatureMethod=HmacSHA256" +
                                             "&SignatureVersion=2" +
                                             "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                             "&Version=2012-06-01" +
                                             "&AWSAccessKeyId=identity",
                                       "application/x-www-form-urlencoded"))
                               .build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_loadbalancers.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getLoadBalancerApi().get("name").toString(), new GetLoadBalancerResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getLoadBalancerApi().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                 .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                 .payload(
                                    payloadFromStringWithContentType(
                                             "Action=DescribeLoadBalancers" +
                                             "&Signature=3pErfVJXXe4EndOr3nPMu2/5eO8aCvwcOaI%2BL64VMqg%3D" +
                                             "&SignatureMethod=HmacSHA256" +
                                             "&SignatureVersion=2" +
                                             "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                             "&Version=2012-06-01" +
                                             "&AWSAccessKeyId=identity",
                                          "application/x-www-form-urlencoded"))
                                 .build();
   
   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_loadbalancers.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getLoadBalancerApi().list().get(0).toString(), new DescribeLoadBalancersResponseTest().expected().toString());
   }

   
   public void testList2PagesWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse1 = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_loadbalancers_marker.xml", "text/xml")).build();
     
      HttpRequest list2 = HttpRequest.builder()
               .method("POST")
               .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
               .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=DescribeLoadBalancers" +
                           "&Marker=MARKER" +
                           "&Signature=/JttkIXuYljhZLJOPYyn%2BYIkDhD9skmePH3LYEnqmes%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse listResponse2 = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_loadbalancers.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestsSendResponses(
            list, listResponse1, list2, listResponse2);

      LoadBalancer lb1 = new GetLoadBalancerResponseTest().expected().toBuilder().name("my-load-balancer-1").build();
      LoadBalancer lb2 = new GetLoadBalancerResponseTest().expected();
      
      assertEquals(apiWhenExist.getLoadBalancerApi().list().concat().toSet(), ImmutableSet.of(lb1, lb2));
   }
   
   public void testList2PagesWhenResponseIs2xxInEU() throws Exception {

      HttpRequest list = HttpRequest.builder()
               .method("POST")
               .endpoint("https://elasticloadbalancing.eu-west-1.amazonaws.com/")
               .addHeader("Host", "elasticloadbalancing.eu-west-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=DescribeLoadBalancers" +
                           "&Signature=/T6QECRsE52DT6mA7AkBy4%2Bdnvy4RXU3nNt56td0GTo%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();

      HttpResponse listResponse1 = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_loadbalancers_marker.xml", "text/xml")).build();
      
      HttpRequest list2 = HttpRequest.builder()
               .method("POST")
               .endpoint("https://elasticloadbalancing.eu-west-1.amazonaws.com/")
               .addHeader("Host", "elasticloadbalancing.eu-west-1.amazonaws.com")
               .payload(
                  payloadFromStringWithContentType(
                           "Action=DescribeLoadBalancers" +
                           "&Marker=MARKER" +
                           "&Signature=jiNCvpqj2fTKbput%2BhBtYMM6KpWAFzBeW20FyWeoyZw%3D" +
                           "&SignatureMethod=HmacSHA256" +
                           "&SignatureVersion=2" +
                           "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                           "&Version=2012-06-01" +
                           "&AWSAccessKeyId=identity",
                        "application/x-www-form-urlencoded"))
               .build();
      
      HttpResponse listResponse2 = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_loadbalancers.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestsSendResponses(list, listResponse1, list2, listResponse2);

      LoadBalancer lb1 = new GetLoadBalancerResponseTest().expected().toBuilder().name("my-load-balancer-1").build();
      LoadBalancer lb2 = new GetLoadBalancerResponseTest().expected();
      
      assertEquals(ImmutableSet.copyOf(Iterables.concat(apiWhenExist.getLoadBalancerApiForRegion("eu-west-1").list())), ImmutableSet.of(lb1, lb2));
   }
   
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenDontExist.getLoadBalancerApi().list().get(0).toSet(), ImmutableSet.of());

   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                       .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeLoadBalancers" +
                                                  "&Marker=MARKER" +
                                                  "&Signature=/JttkIXuYljhZLJOPYyn%2BYIkDhD9skmePH3LYEnqmes%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-06-01" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_loadbalancers.xml", "text/xml")).build();

      ELBApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getLoadBalancerApi().list(afterMarker("MARKER")).toString(),
               new DescribeLoadBalancersResponseTest().expected().toString());
   }
   
   HttpRequest delete = HttpRequest.builder()
                                   .method("POST")
                                   .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                   .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                   .payload(
                                      payloadFromStringWithContentType(
                                               "Action=DeleteLoadBalancer" +
                                               "&LoadBalancerName=name" +
                                               "&Signature=LPfcRnIayHleMt9Z8QiGTMXoafF2ABKGeah3UO1eD0k%3D" +
                                               "&SignatureMethod=HmacSHA256" +
                                               "&SignatureVersion=2" +
                                               "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                               "&Version=2012-06-01" +
                                               "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                                   .build();

   public void testDeleteWhenResponseIs2xx() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200).build();

      ELBApi apiWhenExist = requestSendsResponse(delete, deleteResponse);

      apiWhenExist.getLoadBalancerApi().delete("name");
   }

   public void testDeleteWhenResponseIs404() throws Exception {

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(delete, deleteResponse);

      apiWhenDontExist.getLoadBalancerApi().delete("name");
   }
}
