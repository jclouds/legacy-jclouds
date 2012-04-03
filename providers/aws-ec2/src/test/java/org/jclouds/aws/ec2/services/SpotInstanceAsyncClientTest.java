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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeSpotPriceHistoryResponseHandler;
import org.jclouds.aws.ec2.xml.SpotInstanceHandler;
import org.jclouds.aws.ec2.xml.SpotInstancesHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SpotInstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SpotInstanceAsyncClientTest")
public class SpotInstanceAsyncClientTest extends BaseAWSEC2AsyncClientTest<SpotInstanceAsyncClient> {
   public void testRequestSpotInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("requestSpotInstanceInRegion", String.class,
            float.class, String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, 0.01f, "m1.small", "ami-voo");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RequestSpotInstances&LaunchSpecification.ImageId=m1.small&SpotPrice=0.01&LaunchSpecification.InstanceType=ami-voo",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstanceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRequestSpotInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("requestSpotInstancesInRegion", String.class,
            float.class, int.class, LaunchSpecification.class, RequestSpotInstancesOptions[].class);
      HttpRequest request = processor.createRequest(method, "eu-west-1", 0.01, 3,
            LaunchSpecification.builder().instanceType("m1.small").imageId("ami-voo").availabilityZone("eu-west-1a")
                  .kernelId("kernelId").securityGroupName("group1").build(), new RequestSpotInstancesOptions().validFrom(from)
                  .validUntil(to).availabilityZoneGroup("availabilityZoneGroup").launchGroup("launchGroup"));

      assertRequestLineEquals(request, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RequestSpotInstances&InstanceCount=3&SpotPrice=0.01&ValidFrom=1970-05-23T21%3A21%3A18Z&ValidUntil=2009-02-13T23%3A31%3A31Z&AvailabilityZoneGroup=availabilityZoneGroup&LaunchGroup=launchGroup&LaunchSpecification.ImageId=ami-voo&LaunchSpecification.Placement.AvailabilityZone=eu-west-1a&LaunchSpecification.SecurityGroup.1=group1&LaunchSpecification.InstanceType=m1.small&LaunchSpecification.KernelId=kernelId",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCancelSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("cancelSpotInstanceRequestsInRegion", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, null, "id");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CancelSpotInstanceRequests&SpotInstanceRequestId.1=id",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotInstanceRequestsInRegion", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSpotInstanceRequests",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotInstanceRequestsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotInstanceRequestsInRegion", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=1&SpotInstanceRequestId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SpotInstancesHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSpotPriceHistory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotPriceHistoryInRegion", String.class,
            DescribeSpotPriceHistoryOptions[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSpotPriceHistory",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSpotPriceHistoryResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   Date from = new Date(12345678910l);
   Date to = new Date(1234567891011l);

   public void testDescribeSpotPriceHistoryArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotPriceHistoryInRegion", String.class,
            DescribeSpotPriceHistoryOptions[].class);
      HttpRequest request = processor.createRequest(method, null, DescribeSpotPriceHistoryOptions.Builder.from(from)
            .to(to).productDescription("description").instanceType("m1.small"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=DescribeSpotPriceHistory&StartTime=1970-05-23T21%3A21%3A18.910Z&EndTime=2009-02-13T23%3A31%3A31.011Z&ProductDescription=description&InstanceType.1=m1.small",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSpotPriceHistoryResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SpotInstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SpotInstanceAsyncClient>>() {
      };
   }

}
