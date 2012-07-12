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

import java.net.URI;
import java.util.TimeZone;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.internal.BaseELBApiExpectTest;
import org.jclouds.elb.parse.DescribeLoadBalancersResponseTest;
import org.jclouds.elb.parse.GetLoadBalancerResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

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
                                       .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                "Action=DescribeLoadBalancers" +
                                                      "&LoadBalancerNames.member.1=name" +
                                                      "&Signature=EYzZgYDMGi9uFZU%2BVh%2FmmsJ9KmHxm5vEAF%2BhGF12BP4%3D" +
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
                                       .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                                       .headers(ImmutableMultimap.<String, String> builder()
                                                .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                                .build())
                                       .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeLoadBalancers" +
                                                   "&Signature=3pErfVJXXe4EndOr3nPMu2%2F5eO8aCvwcOaI%2BL64VMqg%3D" +
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

      assertEquals(apiWhenExist.getLoadBalancerApi().list().toString(), new DescribeLoadBalancersResponseTest().expected().toString());
   }

   // TODO: this should really be an empty set
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getLoadBalancerApi().list();
   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
                       .headers(ImmutableMultimap.<String, String>builder()
                                                 .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                                 .build())
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeLoadBalancers" +
                                                  "&Marker=MARKER" +
                                                  "&Signature=%2FJttkIXuYljhZLJOPYyn%2BYIkDhD9skmePH3LYEnqmes%3D" +
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
            .endpoint(URI.create("https://elasticloadbalancing.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                     .build())
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
