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

package org.jclouds.ec2.compute.predicates;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.BaseEC2ComputeServiceExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.predicates.PredicateWithResult;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Injector;

/**
 * 
 * @author David Alves
 */
@Test(groups = "unit", testName = "GetImageWhenStatusAvailablePredicateWithResultExpectTest")
public class GetImageWhenStatusAvailablePredicateWithResultExpectTest extends BaseEC2ComputeServiceExpectTest<Injector> {

   protected HttpRequest describeRegionsRequest = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://ec2.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.of("Host", "ec2.us-east-1.amazonaws.com"))
            .payload(payloadFromStringWithContentType(
                     "Action=DescribeRegions&Signature=s5OXKqaaeKhJW5FVrRntuMsUL4Ed5fjzgUWeukU96ko%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded")).build();

   protected HttpRequest describeImagesRequest0 = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://ec2.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.of("Host", "ec2.us-east-1.amazonaws.com"))
            .payload(payloadFromStringWithContentType(
                     "Action=DescribeImages&ImageId.1=ami-0&Signature=k9douTXFWkAZecPiZfBLUm3LIS3bTLanMV%2F%2BWrB1jFA%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded")).build();

   protected HttpRequest describeImagesRequest1 = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://ec2.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.of("Host", "ec2.us-east-1.amazonaws.com"))
            .payload(payloadFromStringWithContentType(
                     "Action=DescribeImages&ImageId.1=ami-1&Signature=IVunQEvp8vTKTIxXex2Uh5SWQY1PJCx0ExUe9FRujBY%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded")).build();

   protected HttpRequest describeImagesRequest2 = HttpRequest
            .builder()
            .method("POST")
            .endpoint(URI.create("https://ec2.us-east-1.amazonaws.com/"))
            .headers(ImmutableMultimap.of("Host", "ec2.us-east-1.amazonaws.com"))
            .payload(payloadFromStringWithContentType(
                     "Action=DescribeImages&ImageId.1=ami-2&Signature=8TfP8BJlg1hiY6EqUbS73A7PQO7dlpqnRMyi7hPu76U%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2012-04-16T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
                     "application/x-www-form-urlencoded")).build();

   protected HttpResponse describeRegionsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/regionEndpoints.xml", MediaType.APPLICATION_XML)).build();

   protected HttpResponse describeImagesResponse0 = HttpResponse
            .builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_images_imageextension0.xml",
                     MediaType.APPLICATION_XML)).build();

   protected HttpResponse describeImagesResponse1 = HttpResponse
            .builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_images_imageextension1.xml",
                     MediaType.APPLICATION_XML)).build();

   protected HttpResponse describeImagesResponse2 = HttpResponse
            .builder()
            .statusCode(200)
            .payload(payloadFromResourceWithContentType("/describe_images_imageextension2.xml",
                     MediaType.APPLICATION_XML)).build();

   private final Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(describeRegionsRequest, describeRegionsResponse).put(describeImagesRequest0, describeImagesResponse0)
            .put(describeImagesRequest1, describeImagesResponse1).put(describeImagesRequest2, describeImagesResponse2)
            .build();

   public void testReturnsFalseOnQueuedAndSavingAndTrueOnActive() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<String, Image> predicate = injector
               .getInstance(GetImageWhenStatusAvailablePredicateWithResult.class);
      assertTrue(predicate.apply("us-east-1/ami-0"));
      assertFalse(predicate.apply("us-east-1/ami-2"));
   }

   @Test(groups = "unit", testName = "GetImageWhenStatusAvailablePredicateWithResultExpectTest", expectedExceptions = IllegalStateException.class)
   public void testFailsOnOtherStatuses() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<String, Image> predicate = injector
               .getInstance(GetImageWhenStatusAvailablePredicateWithResult.class);
      predicate.apply("us-east-1/ami-1");
   }

   @Override
   public Injector apply(ComputeServiceContext input) {
      return input.utils().injector();
   }

}
