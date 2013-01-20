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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.util.Date;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeSpotPriceHistoryResponseHandler;
import org.jclouds.aws.ec2.xml.SpotInstanceHandler;
import org.jclouds.aws.ec2.xml.SpotInstancesHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code SpotInstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SpotInstanceAsyncClientTest")
public class SpotInstanceAsyncClientTest extends BaseAWSEC2AsyncClientTest<SpotInstanceAsyncClient> {

   HttpRequest requestSpotInstances = HttpRequest.builder().method("POST")
                                                 .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                 .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                 .addFormParam("Action", "RequestSpotInstances")
                                                 .addFormParam("LaunchSpecification.ImageId", "m1.small")
                                                 .addFormParam("LaunchSpecification.InstanceType", "ami-voo")
                                                 .addFormParam("Signature", "5PZRT8xXMugx1ku/NxQpaGWqYLLbKwJksBbeldGLO2s=")
                                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                                 .addFormParam("SignatureVersion", "2")
                                                 .addFormParam("SpotPrice", "0.01")
                                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                 .addFormParam("Version", "2011-05-15")
                                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRequestSpotInstance() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "requestSpotInstanceInRegion", String.class,
            float.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, 0.01f, "m1.small", "ami-voo"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, requestSpotInstances.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstanceHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest requestSpotInstancesOptions = HttpRequest.builder().method("POST")
                                                        .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                        .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                        .addFormParam("Action", "RequestSpotInstances")
                                                        .addFormParam("AvailabilityZoneGroup", "availabilityZoneGroup")
                                                        .addFormParam("InstanceCount", "3")
                                                        .addFormParam("LaunchGroup", "launchGroup")
                                                        .addFormParam("LaunchSpecification.ImageId", "ami-voo")
                                                        .addFormParam("LaunchSpecification.InstanceType", "m1.small")
                                                        .addFormParam("LaunchSpecification.KernelId", "kernelId")
                                                        .addFormParam("LaunchSpecification.Placement.AvailabilityZone", "eu-west-1a")
                                                        .addFormParam("LaunchSpecification.SecurityGroup.1", "group1")
                                                        .addFormParam("Signature", "94pCsdmfYVMbMzofCeTvfvpQozIY6iDu0LewXvHl1ao=")
                                                        .addFormParam("SignatureMethod", "HmacSHA256")
                                                        .addFormParam("SignatureVersion", "2")
                                                        .addFormParam("SpotPrice", "0.01")
                                                        .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                        .addFormParam("ValidFrom", "1970-05-23T21%3A21%3A18Z")
                                                        .addFormParam("ValidUntil", "2009-02-13T23%3A31%3A31Z")
                                                        .addFormParam("Version", "2011-05-15")
                                                        .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRequestSpotInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "requestSpotInstancesInRegion", String.class,
            float.class, int.class, LaunchSpecification.class, RequestSpotInstancesOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("eu-west-1", 0.01, 3,
            LaunchSpecification.builder().instanceType("m1.small").imageId("ami-voo").availabilityZone("eu-west-1a")
                  .kernelId("kernelId").securityGroupName("group1").build(), new RequestSpotInstancesOptions().validFrom(from)
                  .validUntil(to).availabilityZoneGroup("availabilityZoneGroup").launchGroup("launchGroup")));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(request, requestSpotInstancesOptions.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCancelSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "cancelSpotInstanceRequestsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "id"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CancelSpotInstanceRequests&SpotInstanceRequestId.1=id",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "describeSpotInstanceRequestsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSpotInstanceRequests",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotInstanceRequestsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "describeSpotInstanceRequestsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=1&SpotInstanceRequestId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotPriceHistory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "describeSpotPriceHistoryInRegion", String.class,
            DescribeSpotPriceHistoryOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSpotPriceHistory",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSpotPriceHistoryResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   Date from = new Date(12345678910l);
   Date to = new Date(1234567891011l);

   public void testDescribeSpotPriceHistoryArgs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SpotInstanceAsyncClient.class, "describeSpotPriceHistoryInRegion", String.class,
            DescribeSpotPriceHistoryOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, DescribeSpotPriceHistoryOptions.Builder.from(from)
            .to(to).productDescription("description").instanceType("m1.small")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=DescribeSpotPriceHistory&StartTime=1970-05-23T21%3A21%3A18.910Z&EndTime=2009-02-13T23%3A31%3A31.011Z&ProductDescription=description&InstanceType.1=m1.small",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSpotPriceHistoryResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }
}
