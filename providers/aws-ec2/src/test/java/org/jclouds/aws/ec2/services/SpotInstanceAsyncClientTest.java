/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.util.Date;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.ec2.options.DescribeSpotPriceHistoryOptions;
import org.jclouds.aws.ec2.xml.DescribeSpotPriceHistoryResponseHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

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
