/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Date;

import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.options.RequestSpotInstancesOptions;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReturnStringIf2xx;
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
   public void testRequestSpotInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("requestSpotInstancesInRegion", String.class,
            String.class, String.class, int.class, float.class, RequestSpotInstancesOptions[].class);
      HttpRequest request = processor.createRequest(method, null, null, "ami-voo", 1, 0.01);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Version=2010-11-15&Action=RequestSpotInstances&LaunchSpecification.ImageId=ami-voo&InstanceCount=1&SpotPrice=0.01",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRequestSpotInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("requestSpotInstancesInRegion", String.class,
            String.class, String.class, int.class, float.class, RequestSpotInstancesOptions[].class);
      HttpRequest request = processor.createRequest(
            method,
            "eu-west-1",
            "eu-west-1a",
            "ami-voo",
            1,
            0.01,
            new RequestSpotInstancesOptions()
                  .validFrom(from)
                  .validUntil(to)
                  .availabilityZoneGroup("availabilityZoneGroup")
                  .launchGroup("launchGroup")
                  .launchSpecification(
                        RunInstancesOptions.Builder.withKernelId("kernelId").withSecurityGroups("group1", "group2")));

      assertRequestLineEquals(request, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Version=2010-11-15&Action=RequestSpotInstances&LaunchSpecification.ImageId=ami-voo&InstanceCount=1&SpotPrice=0.01&ValidFrom=1970-05-23T21%3A21%3A18.910Z&ValidUntil=2009-02-13T23%3A31%3A31.011Z&AvailabilityZoneGroup=availabilityZoneGroup&LaunchGroup=launchGroup&LaunchSpecification.KernelId=kernelId&LaunchSpecification.SecurityGroup.1=group1&LaunchSpecification.SecurityGroup.2=group2&LaunchSpecification.Placement.AvailabilityZone=eu-west-1a",
            "application/x-www-form-urlencoded", false);
      
      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCancelSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("cancelSpotInstanceRequestsInRegion", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, null, "id");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2010-11-15&Action=CancelSpotInstanceRequests&SpotInstanceRequestId.1=id",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeSpotInstanceRequests() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotInstanceRequestsInRegion", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2010-11-15&Action=DescribeSpotInstanceRequests",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

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
            "Version=2010-11-15&Action=DescribeSpotInstanceRequests&SpotInstanceRequestId.1=1&SpotInstanceRequestId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeSpotPriceHistory() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SpotInstanceAsyncClient.class.getMethod("describeSpotPriceHistoryInRegion", String.class,
            DescribeSpotPriceHistoryOptions[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2010-11-15&Action=DescribeSpotPriceHistory",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

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
            "Version=2010-11-15&Action=DescribeSpotPriceHistory&StartTime=1970-05-23T21%3A21%3A18.910Z&EndTime=2009-02-13T23%3A31%3A31.011Z&ProductDescription=description&InstanceType.1=m1.small",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SpotInstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SpotInstanceAsyncClient>>() {
      };
   }

}
