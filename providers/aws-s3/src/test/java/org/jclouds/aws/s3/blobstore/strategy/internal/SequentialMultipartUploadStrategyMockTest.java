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
package org.jclouds.aws.s3.blobstore.strategy.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true)
public class SequentialMultipartUploadStrategyMockTest {

   @Test
   public void testMPUDoesMultipart() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody("<UploadId>upload-id</UploadId>"));
      server.enqueue(new MockResponse().setResponseCode(200).addHeader("ETag", "a00"));
      server.enqueue(new MockResponse().setResponseCode(200).addHeader("ETag", "b00"));
      server.enqueue(new MockResponse().setResponseCode(200).setBody("<ETag>fff</ETag>"));
      server.play();

      byte[] bytes = "0123456789abcdef".getBytes(Charsets.US_ASCII);
      int partSize = bytes.length / 2;

      SequentialMultipartUploadStrategy api = mockSequentialMultipartUploadStrategy(server.getUrl("/").toString(),
            partSize);

      try {
         assertEquals(api.execute("container", new BlobBuilderImpl().name("foo").payload(bytes)
            .contentDisposition("inline; filename=foo.mp4")
            .contentType(MediaType.MP4_VIDEO.toString())
            .build()), "fff");
      } finally {

         RecordedRequest initiate = server.takeRequest();
         assertEquals(initiate.getRequestLine(), "POST /container/foo?uploads HTTP/1.1");
         assertEquals(initiate.getHeader("Content-Length"), "0");
         assertEquals(initiate.getHeader(HttpHeaders.CONTENT_TYPE), MediaType.MP4_VIDEO.toString());
         assertEquals(initiate.getHeader(HttpHeaders.CONTENT_DISPOSITION), "inline; filename=foo.mp4");

         RecordedRequest part1 = server.takeRequest();
         assertEquals(part1.getRequestLine(), "PUT /container/foo?partNumber=1&uploadId=upload-id HTTP/1.1");
         assertEquals(part1.getHeader("Content-Length"), String.valueOf(partSize));
         assertEquals(new String(part1.getBody()), "01234567");

         RecordedRequest part2 = server.takeRequest();
         assertEquals(part2.getRequestLine(), "PUT /container/foo?partNumber=2&uploadId=upload-id HTTP/1.1");
         assertEquals(part2.getHeader("Content-Length"), String.valueOf(partSize));
         assertEquals(new String(part2.getBody()), "89abcdef");

         RecordedRequest manifest = server.takeRequest();
         assertEquals(manifest.getRequestLine(), "POST /container/foo?uploadId=upload-id HTTP/1.1");
         assertEquals(manifest.getHeader("Content-Length"), "161");
         assertEquals(
               new String(manifest.getBody()),
               "<CompleteMultipartUpload><Part><PartNumber>1</PartNumber><ETag>a00</ETag></Part><Part><PartNumber>2</PartNumber><ETag>b00</ETag></Part></CompleteMultipartUpload>");

         server.shutdown();
      }
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testMPUAbortsOnProblem() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody("<UploadId>upload-id</UploadId>"));
      server.enqueue(new MockResponse().setResponseCode(400));
      server.enqueue(new MockResponse().setResponseCode(200));
      server.play();

      byte[] bytes = "0123456789abcdef".getBytes(Charsets.US_ASCII);
      int partSize = bytes.length / 2;

      SequentialMultipartUploadStrategy api = mockSequentialMultipartUploadStrategy(server.getUrl("/").toString(),
            partSize);

      try {
         assertEquals(api.execute("container", new BlobBuilderImpl().name("foo").payload(bytes).build()), "fff");
      } finally {

         RecordedRequest initiate = server.takeRequest();
         assertEquals(initiate.getRequestLine(), "POST /container/foo?uploads HTTP/1.1");
         assertEquals(initiate.getHeader("Content-Length"), "0");

         RecordedRequest part1 = server.takeRequest();
         assertEquals(part1.getRequestLine(), "PUT /container/foo?partNumber=1&uploadId=upload-id HTTP/1.1");
         assertEquals(part1.getHeader("Content-Length"), String.valueOf(partSize));
         assertEquals(new String(part1.getBody()), "01234567");

         RecordedRequest abort = server.takeRequest();
         assertEquals(abort.getRequestLine(), "DELETE /container/foo?uploadId=upload-id HTTP/1.1");

         server.shutdown();
      }
   }

   private static final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   static SequentialMultipartUploadStrategy mockSequentialMultipartUploadStrategy(String uri, int partSize) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      // prevent expect-100 bug http://code.google.com/p/mockwebserver/issues/detail?id=6
      overrides.setProperty(PROPERTY_SO_TIMEOUT, "0");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      overrides.setProperty("jclouds.mpu.parts.size", String.valueOf(partSize));
      return ContextBuilder.newBuilder("aws-s3")
                           .credentials("accessKey", "secretKey")
                           .endpoint(uri)
                           .overrides(overrides)
                           .modules(modules)
                           .buildInjector().getInstance(SequentialMultipartUploadStrategy.class);
   }
}
