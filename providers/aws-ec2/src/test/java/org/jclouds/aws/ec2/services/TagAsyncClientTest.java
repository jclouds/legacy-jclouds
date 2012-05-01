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
import java.util.Map;

import org.jclouds.aws.ec2.util.TagFilters;
import org.jclouds.aws.ec2.xml.DescribeTagsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TagsAsyncClient}
 * 
 * @author grkvlt@apache.org
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TagsAsyncClientTest")
public class TagAsyncClientTest extends BaseAWSEC2AsyncClientTest<TagAsyncClient> {
   public void testDeleteTags() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagAsyncClient.class.getMethod("deleteTagsInRegion", String.class, Iterable.class, Map.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.<String>builder().add("xxx").build(), ImmutableMap.<String, String>builder().put("yyy", "zzz").build());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteTags&Tag.1.Key=yyy&Tag.1.Value=zzz&ResourceId.1=xxx",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateTags() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagAsyncClient.class.getMethod("createTagsInRegion", String.class, Iterable.class, Map.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.<String>builder().add("xxx").build(), ImmutableMap.<String, String>builder().put("yyy", "zzz").build());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreateTags&Tag.1.Key=yyy&Tag.1.Value=zzz&ResourceId.1=xxx",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeTags() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagAsyncClient.class.getMethod("describeTagsInRegion", String.class, Map.class);
      HttpRequest request = processor.createRequest(method, null, TagFilters.filters().build());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeTags",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeTagsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeTagsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagAsyncClient.class.getMethod("describeTagsInRegion", String.class, Map.class);
      HttpRequest request = processor.createRequest(method, null, TagFilters.filters().key("one").key("two").build());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeTags&Filter.1.Name=key&Filter.1.Value.1=one&Filter.1.Value.2=two",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeTagsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TagAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TagAsyncClient>>() {
      };
   }
}
