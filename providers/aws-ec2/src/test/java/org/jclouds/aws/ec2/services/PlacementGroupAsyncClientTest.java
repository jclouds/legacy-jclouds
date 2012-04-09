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

import org.jclouds.aws.ec2.xml.DescribePlacementGroupsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PlacementGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "PlacementGroupAsyncClientTest")
public class PlacementGroupAsyncClientTest extends BaseAWSEC2AsyncClientTest<PlacementGroupAsyncClient> {

   public void testDeletePlacementGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PlacementGroupAsyncClient.class.getMethod("deletePlacementGroupInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "name");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeletePlacementGroup&GroupName=name",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreatePlacementGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PlacementGroupAsyncClient.class.getMethod("createPlacementGroupInRegion", String.class,
               String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "name", "cluster");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreatePlacementGroup&Strategy=cluster&GroupName=name",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreatePlacementGroupDefault() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PlacementGroupAsyncClient.class.getMethod("createPlacementGroupInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "name");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreatePlacementGroup&Strategy=cluster&GroupName=name",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribePlacementGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PlacementGroupAsyncClient.class.getMethod("describePlacementGroupsInRegion", String.class,
               String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribePlacementGroups",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribePlacementGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribePlacementGroupsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PlacementGroupAsyncClient.class.getMethod("describePlacementGroupsInRegion", String.class,
               String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribePlacementGroups&GroupName.1=1&GroupName.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribePlacementGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<PlacementGroupAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<PlacementGroupAsyncClient>>() {
      };
   }

}
