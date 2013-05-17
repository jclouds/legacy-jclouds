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
package org.jclouds.s3.handlers;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.internal.BaseS3ClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "S3RedirectionRetryHandlerExpectTest")
public class S3RedirectionRetryHandlerExpectTest extends BaseS3ClientExpectTest {

   public void testRedirectOnHeadBucketChangesRequestToGetBucket() {

      HttpRequest bucketFooExists = HttpRequest.builder().method("HEAD").endpoint(
               URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).headers(
               ImmutableMultimap.<String, String> builder().put("Host", "foo.s3.amazonaws.com").put("Date",
                        CONSTANT_DATE).put("Authorization", "AWS identity:86P4BBb7xT+gBqq7jxM8Tc28ktY=").build())
               .build();

      HttpResponse redirectResponse = HttpResponse.builder().statusCode(301).build();

      HttpRequest bucketFooExistsNowUsesGET = HttpRequest.builder().method("GET").endpoint(
               URI.create("https://foo.s3.amazonaws.com/?max-keys=0")).headers(
               ImmutableMultimap.<String, String> builder().put("Host", "foo.s3.amazonaws.com").put("Date",
                        CONSTANT_DATE).put("Authorization", "AWS identity:ZWVz2v/jGB+ZMmijoyfH9mFMPo0=").build())
               .build();

      HttpResponse success = HttpResponse.builder().statusCode(200).build();

      S3Client clientWhenBucketExists = requestsSendResponses(bucketFooExists, redirectResponse, bucketFooExistsNowUsesGET, success);
      
      assert clientWhenBucketExists.bucketExists("foo");

   }
}
