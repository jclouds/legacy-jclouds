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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.ec2.xml.KeyPairResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code AWSKeyPairAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSKeyPairAsyncClientTest")
public class AWSKeyPairAsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSKeyPairAsyncClient> {

   public void testCreateKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSKeyPairAsyncClient.class, "createKeyPairInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "mykey"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreateKeyPair&KeyName=mykey",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest importKeyPair = HttpRequest.builder().method("POST")
                                          .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                          .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                          .addFormParam("Action", "ImportKeyPair")
                                          .addFormParam("KeyName", "mykey")
                                          .addFormParam("PublicKeyMaterial", "c3NoLXJzYSBBQQ%3D%3D").build();

   public void testImportKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSKeyPairAsyncClient.class, "importKeyPairInRegion", String.class, String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "mykey", "ssh-rsa AA"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(importKeyPair).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSKeyPairAsyncClient.class, "deleteKeyPairInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "mykey"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteKeyPair&KeyName=mykey",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSKeyPairAsyncClient.class, "describeKeyPairsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeKeyPairs", "application/x-www-form-urlencoded",
            false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeKeyPairsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSKeyPairAsyncClient.class, "describeKeyPairsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeKeyPairs&KeyName.1=1&KeyName.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }
}
