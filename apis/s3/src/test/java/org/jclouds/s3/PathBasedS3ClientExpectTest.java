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
package org.jclouds.s3;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.internal.BaseS3ClientExpectTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PathBasedS3ClientExpectTest")
public class PathBasedS3ClientExpectTest extends BaseS3ClientExpectTest {

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      return overrides;
   }

   @Test
   public void testBucketExistsReturnsTrueOn200AndFalseOn404() {
      
      HttpRequest bucketFooExists = HttpRequest.builder().method("GET")
                                               .endpoint("https://s3.amazonaws.com/foo?max-keys=0")
                                               .addHeader("Date", CONSTANT_DATE)
                                               .addHeader("Authorization", "AWS identity:p32RsBr2inawMBeCkkiA228BT2w=")
                                               .build();
                                    
      S3Client clientWhenBucketExists = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(200).build());
      assert clientWhenBucketExists.bucketExists("foo");
      
      S3Client clientWhenBucketDoesntExist = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(404).build());
      assert !clientWhenBucketDoesntExist.bucketExists("foo");
      
   }

   @Test
   public void testPutBucketReturnsTrueOn200() {
      
      HttpRequest bucketFooExists = HttpRequest.builder().method("PUT")
                                               .endpoint("https://s3.amazonaws.com/foo")
                                               .addHeader("Date", CONSTANT_DATE)
                                               .addHeader("Authorization", "AWS identity:GeP4OqEL/eM+gQt+4Vtcm02gebc=")
                                               .build();
                                    
      S3Client clientWhenBucketExists = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(200).build());
      assert clientWhenBucketExists.putBucketInRegion(null, "foo");
      
   }
   

   @Test
   public void testPutObjectReturnsETagOn200() {
      
      HttpRequest bucketFooExists = HttpRequest.builder().method("PUT")
                                               .endpoint("https://s3.amazonaws.com/bucket/object")
                                               .addHeader("Expect", "100-continue")
                                               .addHeader("Date", CONSTANT_DATE)
                                               .addHeader("Authorization", "AWS identity:6gC0m7SYFDPwkUqY5EHV/6i9DfM=")
                                               .payload("hello world")
                                               .build();
                                    
      S3Client clientWhenBucketExists = requestSendsResponse(bucketFooExists, HttpResponse.builder().statusCode(200).addHeader(HttpHeaders.ETAG, "etag").build());
      S3Object object = clientWhenBucketExists.newS3Object();
      object.getMetadata().setKey("object");
      object.setPayload("hello world");
      Assert.assertEquals(clientWhenBucketExists.putObject("bucket", object), "etag");
   }
}
