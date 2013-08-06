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
package org.jclouds.aws.s3.blobstore;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.blobstore.S3BlobSignerExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * @author Diwaker Gupta
 */
@Test(groups = "unit", testName = "AWSS3BlobSignerExpectTest")
public class AWSS3BlobSignerExpectTest extends S3BlobSignerExpectTest {
   private static final String DATE = "Thu, 05 Jun 2008 16:38:19 GMT";

   public AWSS3BlobSignerExpectTest() {
      provider = "aws-s3";
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
         .endpoint("https://container.s3.amazonaws.com/name" +
            "?Expires=1212683902&AWSAccessKeyId=identity&Signature=Y4Ac4sZfBemGZmgfG78F7IX%2BIFg%3D")
         .addHeader("Host", "container.s3.amazonaws.com")
         .addHeader("Date", DATE).build();
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testSignGetBlobWithTime() {
      BlobStore getBlobWithTime = requestsSendResponses(init());
      HttpRequest compare = getBlobWithTime();
      assertEquals(getBlobWithTime.getContext().getSigner().signGetBlob(container, name, 3l /* seconds */),
         compare);
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
         .endpoint("https://container.s3.amazonaws.com/name" +
            "?Expires=1212683902&AWSAccessKeyId=identity&Signature=genkB2vLxe3AWV/bPvRTMqQts7E%3D")
         .addHeader("Expect", "100-continue")
         .addHeader("Host", "container.s3.amazonaws.com")
         .addHeader("Date", DATE).build();
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testSignPutBlobWithTime() throws Exception {
      BlobStore signPutBloblWithTime = requestsSendResponses(init());
      Blob blob = signPutBloblWithTime.blobBuilder(name).payload(text).contentType("text/plain").build();
      HttpRequest compare = putBlobWithTime();
      compare.setPayload(blob.getPayload());
      assertEquals(signPutBloblWithTime.getContext().getSigner().signPutBlob(container, blob, 3l /* seconds */),
         compare);
   }

   @Override
   protected Module createModule() {
      return new TestAWSS3RestClientModule();
   }

   @ConfiguresRestClient
   private static final class TestAWSS3RestClientModule extends AWSS3RestClientModule {
      @Override
      @TimeStamp
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return DATE;
      }
   }
}
