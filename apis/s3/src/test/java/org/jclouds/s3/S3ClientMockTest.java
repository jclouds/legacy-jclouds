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

import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.ETAG;
import static com.google.common.net.HttpHeaders.EXPECT;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.s3.domain.S3Object;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true)
public class S3ClientMockTest {

   private static final Set<Module> modules = ImmutableSet.<Module> of(
         new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));

   static S3Client getS3Client(URL server) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, "false");
      // prevent expect-100 bug http://code.google.com/p/mockwebserver/issues/detail?id=6
      overrides.setProperty(PROPERTY_SO_TIMEOUT, "0");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("s3")
                           .credentials("accessKey", "secretKey")
                           .endpoint(server.toString())
                           .modules(modules)
                           .overrides(overrides)
                           .buildApi(S3Client.class);
   }

   public void testZeroLengthPutHasContentLengthHeader() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().addHeader(ETAG, "ABCDEF"));
      // hangs on Java 7 without this additional response ?!?
      server.enqueue(new MockResponse().addHeader(ETAG, "ABCDEF"));
      server.play();

      S3Client client = getS3Client(server.getUrl("/"));
      S3Object nada = client.newS3Object();
      nada.getMetadata().setKey("object");
      nada.setPayload(new byte[] {});

      assertEquals(client.putObject("bucket", nada), "ABCDEF");

      RecordedRequest request = server.takeRequest();
      assertEquals(request.getRequestLine(), "PUT /bucket/object HTTP/1.1");
      assertEquals(request.getHeaders(CONTENT_LENGTH), ImmutableList.of("0"));
      // will fail unless -Dsun.net.http.allowRestrictedHeaders=true is set
      assertEquals(request.getHeaders(EXPECT), ImmutableList.of("100-continue"));
      server.shutdown();
   }

   public void testDirectorySeparator() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setBody("").addHeader(ETAG, "ABCDEF"));
      server.play();

      S3Client client = getS3Client(server.getUrl("/"));
      S3Object fileInDir = client.newS3Object();
      fileInDir.getMetadata().setKey("someDir/fileName");
      fileInDir.setPayload(new byte[] { 1, 2, 3, 4 });

      assertEquals(client.putObject("bucket", fileInDir), "ABCDEF");

      RecordedRequest request = server.takeRequest();
      assertEquals(request.getRequestLine(), "PUT /bucket/someDir/fileName HTTP/1.1");
      // will fail unless -Dsun.net.http.allowRestrictedHeaders=true is set
      assertEquals(request.getHeaders(EXPECT), ImmutableList.of("100-continue"));

      server.shutdown();
   }
}
