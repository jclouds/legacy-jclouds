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
package org.jclouds.s3.blobstore;

import org.jclouds.blobstore.internal.BaseBlobSignerExpectTest;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.config.S3RestClientModule;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;

/**
 * Tests behavior of {@code S3BlobRequestSigner}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "S3BlobSignerExpectTest")
public class S3BlobSignerExpectTest extends BaseBlobSignerExpectTest {

   public S3BlobSignerExpectTest() {
      provider = "s3";
   }

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
                        .endpoint("https://container.s3.amazonaws.com/name")
                        .addHeader("Host", "container.s3.amazonaws.com")
                        .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
                        .addHeader("Authorization", "AWS identity:0uvBv1wEskuhFHYJF/L6kEV9A7o=").build();
   }

   @Override
   @Test
   public void testSignGetBlobWithTime() {
      throw new SkipException("not yet implemented");
   }

   //TODO
   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:0uvBv1wEskuhFHYJF/L6kEV9A7o=").build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Range", "bytes=0-1")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:0uvBv1wEskuhFHYJF/L6kEV9A7o=").build();
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Expect", "100-continue")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:j9Dy/lmmvlCKjA4lkqZenLxMkR4=").build();
   }

   @Override
   @Test
   public void testSignPutBlobWithTime() throws Exception {
      throw new SkipException("not yet implemented");
   }

   //TODO
   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Expect", "100-continue")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:0uvBv1wEskuhFHYJF/L6kEV9A7o=").build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("https://container.s3.amazonaws.com/name")
            .addHeader("Host", "container.s3.amazonaws.com")
            .addHeader("Date", "Thu, 05 Jun 2008 16:38:19 GMT")
            .addHeader("Authorization", "AWS identity:4FnyjdX/ULdDMRbVlLNjZfEo9RQ=").build();
   }

   @Override
   protected Module createModule() {
      return new TestS3RestClientModule();
   }

   @ConfiguresRestClient
   private static final class TestS3RestClientModule extends S3RestClientModule<S3Client, S3AsyncClient> {

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "Thu, 05 Jun 2008 16:38:19 GMT";
      }
   }

}
