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

import static org.jclouds.elb.options.ListPoliciesOptions.Builder.loadBalancerName;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.TimeZone;

import org.jclouds.elb.ELBApi;
import org.jclouds.elb.internal.BaseELBApiExpectTest;
import org.jclouds.elb.parse.DescribeLoadBalancerPoliciesResponseTest;
import org.jclouds.elb.parse.DescribeLoadBalancerPolicyTypesResponseTest;
import org.jclouds.elb.parse.GetPolicyResponseTest;
import org.jclouds.elb.parse.GetPolicyTypeResponseTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PolicyApiExpectTest")
public class PolicyApiExpectTest extends BaseELBApiExpectTest {

   public PolicyApiExpectTest() {
      TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
   }

   HttpRequest get = HttpRequest.builder()
                                .method("POST")
                                .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                .payload(
                                    payloadFromStringWithContentType(
                                          "Action=DescribeLoadBalancerPolicies" +
                                                "&PolicyNames.member.1=name" +
                                                "&Signature=kroGA7XRZYqiw4zgAXkWRdF9ff3RcnZKgvfcPG5f%2Bjs%3D" +
                                                "&SignatureMethod=HmacSHA256" +
                                                "&SignatureVersion=2" +
                                                "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                "&Version=2012-06-01" +
                                                "&AWSAccessKeyId=identity",
                                          "application/x-www-form-urlencoded"))
                                .build();
   
   
   public void testGetWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_policy.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            get, getResponse);

      assertEquals(apiWhenExist.getPolicyApi().get("name").toString(), new GetPolicyResponseTest().expected().toString());
   }

   public void testGetWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            get, getResponse);

      assertNull(apiWhenDontExist.getPolicyApi().get("name"));
   }

   HttpRequest list = HttpRequest.builder()
                                 .method("POST")
                                 .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                 .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                 .payload(
                                    payloadFromStringWithContentType(
                                             "Action=DescribeLoadBalancerPolicies" +
                                             "&Signature=0LPrgeysYoQe6PyK2nh3mCgo0lxPNiERxm46W/N5GpU%3D" +
                                             "&SignatureMethod=HmacSHA256" +
                                             "&SignatureVersion=2" +
                                             "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                             "&Version=2012-06-01" +
                                             "&AWSAccessKeyId=identity",
                                          "application/x-www-form-urlencoded"))
                                 .build();

   public void testListWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_policies.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            list, listResponse);

      assertEquals(apiWhenExist.getPolicyApi().list().toString(), new DescribeLoadBalancerPoliciesResponseTest().expected().toString());
   }

   public void testListWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            list, listResponse);

      apiWhenDontExist.getPolicyApi().list();
   }
   
   public void testListWithOptionsWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                       .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeLoadBalancerPolicies" +
                                                  "&LoadBalancerName=moo" +
                                                  "&Signature=c8PG1b5wI5YMU0motVEo5Mz7d5w8gy8u51kfCR6SnRI%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-06-01" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_policies.xml", "text/xml")).build();

      ELBApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getPolicyApi().list(loadBalancerName("moo")).toString(),
               new DescribeLoadBalancerPoliciesResponseTest().expected().toString());
   }
   

   HttpRequest getType = HttpRequest.builder()
                                    .method("POST")
                                    .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                    .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                    .payload(
                                       payloadFromStringWithContentType(
                                             "Action=DescribeLoadBalancerPolicyTypes" +
                                                   "&PolicyTypeNames.member.1=name" +
                                                   "&Signature=WC5tQK0TacaxSRrCEKqbpIPFrrrgsBV4I1%2B9W2Lx58M%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                             "application/x-www-form-urlencoded"))
                                    .build();
   
   
   public void testGetTypeWhenResponseIs2xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_policy_type.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            getType, getResponse);

      assertEquals(apiWhenExist.getPolicyApi().getType("name").toString(), new GetPolicyTypeResponseTest().expected().toString());
   }

   public void testGetTypeWhenResponseIs404() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            getType, getResponse);

      assertNull(apiWhenDontExist.getPolicyApi().getType("name"));
   }

   HttpRequest listTypes = HttpRequest.builder()
                                      .method("POST")
                                      .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                                      .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                                      .payload(
                                          payloadFromStringWithContentType(
                                                   "Action=DescribeLoadBalancerPolicyTypes" +
                                                   "&Signature=/1mMjugJD8Zvb%2BK/QgOZMYVenlveCKtvGBiHaZVc%2B9w%3D" +
                                                   "&SignatureMethod=HmacSHA256" +
                                                   "&SignatureVersion=2" +
                                                   "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                   "&Version=2012-06-01" +
                                                   "&AWSAccessKeyId=identity",
                                                "application/x-www-form-urlencoded"))
                                      .build();

   public void testListTypeWhenResponseIs2xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_policy_types.xml", "text/xml")).build();

      ELBApi apiWhenExist = requestSendsResponse(
            listTypes, listResponse);

      assertEquals(apiWhenExist.getPolicyApi().listTypes().toString(), new DescribeLoadBalancerPolicyTypesResponseTest().expected().toString());
   }

   public void testListTypesWhenResponseIs404() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      ELBApi apiWhenDontExist = requestSendsResponse(
            listTypes, listResponse);

      apiWhenDontExist.getPolicyApi().listTypes();
   }
   
   public void testListTypesByNamesWhenResponseIs2xx() throws Exception {
      HttpRequest listWithOptions =
            HttpRequest.builder()
                       .method("POST")
                       .endpoint("https://elasticloadbalancing.us-east-1.amazonaws.com/")
                       .addHeader("Host", "elasticloadbalancing.us-east-1.amazonaws.com")
                       .payload(payloadFromStringWithContentType(
                                                  "Action=DescribeLoadBalancerPolicyTypes" +
                                                  "&PolicyTypeNames.member.1=moo" +
                                                  "&Signature=cX8twwn2E6%2B7V3CGZ4ac69NhyolJLsV1nzpQl3wQXW8%3D" +
                                                  "&SignatureMethod=HmacSHA256" +
                                                  "&SignatureVersion=2" +
                                                  "&Timestamp=2009-11-08T15%3A54%3A08.897Z" +
                                                  "&Version=2012-06-01" +
                                                  "&AWSAccessKeyId=identity",
                                            "application/x-www-form-urlencoded"))
                       .build();
      
      HttpResponse listWithOptionsResponse = HttpResponse.builder().statusCode(200)
               .payload(payloadFromResourceWithContentType("/describe_policy_types.xml", "text/xml")).build();

      ELBApi apiWhenWithOptionsExist = requestSendsResponse(listWithOptions,
               listWithOptionsResponse);

      assertEquals(apiWhenWithOptionsExist.getPolicyApi().listTypes(ImmutableSet.of("moo")).toString(),
               new DescribeLoadBalancerPolicyTypesResponseTest().expected().toString());
   }
}
