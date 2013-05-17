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
package org.jclouds.openstack.swift.blobstore.strategy.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.QueueDispatcher;
import com.google.mockwebserver.RecordedRequest;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true)
public class SequentialMultipartUploadStrategyMockTest {

   String authRequestBody = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword("user", "password")
         .getPayload().getRawContent().toString();
   String authResponse = KeystoneFixture.INSTANCE.responseWithAccess().getPayload().getRawContent().toString()
         .replace("https://objects.jclouds.org/v1.0/40806637803162", "URL");
   String token = "Auth_4f173437e4b013bee56d1007";

   @Test
   public void testMPUDoesMultipart() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      AtomicReference<URL> url = setURLReplacingDispatcher(server);
      server.enqueue(new MockResponse().setResponseCode(200).setBody(authResponse));
      server.enqueue(new MockResponse().setResponseCode(200).addHeader("ETag", "a00"));
      server.enqueue(new MockResponse().setResponseCode(200).addHeader("ETag", "b00"));
      server.enqueue(new MockResponse().setResponseCode(200).addHeader("ETag", "fff"));
      server.play();
      url.set(server.getUrl("/"));

      byte[] bytes = "0123456789abcdef".getBytes(Charsets.US_ASCII);
      int partSize = bytes.length / 2;
      SequentialMultipartUploadStrategy api = mockSequentialMultipartUploadStrategy(url.get().toString(), partSize);

      try {
         assertEquals(api.execute("container", new BlobBuilderImpl().name("foo").payload(bytes).build()), "fff");
      } finally {
         RecordedRequest authRequest = server.takeRequest();
         assertEquals(authRequest.getRequestLine(), "POST /tokens HTTP/1.1");
         assertEquals(new String(authRequest.getBody()), authRequestBody);
         assertEquals(authRequest.getHeader("Content-Length"), String.valueOf(authRequestBody.length()));

         RecordedRequest part1 = server.takeRequest();
         assertEquals(part1.getRequestLine(), "PUT /container/foo/1 HTTP/1.1");
         assertEquals(part1.getHeader("X-Auth-Token"), token);
         assertEquals(part1.getHeader("Content-Length"), String.valueOf(partSize));
         assertEquals(new String(part1.getBody()), "01234567");

         RecordedRequest part2 = server.takeRequest();
         assertEquals(part2.getRequestLine(), "PUT /container/foo/2 HTTP/1.1");
         assertEquals(part2.getHeader("X-Auth-Token"), token);
         assertEquals(part2.getHeader("Content-Length"), String.valueOf(partSize));
         assertEquals(new String(part2.getBody()), "89abcdef");

         RecordedRequest manifest = server.takeRequest();
         assertEquals(manifest.getRequestLine(), "PUT /container/foo HTTP/1.1");
         assertEquals(manifest.getHeader("X-Auth-Token"), token);
         assertEquals(manifest.getHeader("Content-Length"), "0");

         server.shutdown();
      }
   }

   private static final Set<Module> modules = ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(),
         sameThreadExecutor()));

   static SequentialMultipartUploadStrategy mockSequentialMultipartUploadStrategy(String uri, int partSize) {
      Properties overrides = new Properties();
      // prevent expect-100 bug http://code.google.com/p/mockwebserver/issues/detail?id=6
      overrides.setProperty(PROPERTY_SO_TIMEOUT, "0");
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      overrides.setProperty("jclouds.mpu.parts.size", String.valueOf(partSize));
      return ContextBuilder.newBuilder("swift-keystone")
                           .credentials("user", "password").endpoint(uri)
                           .overrides(overrides)
                           .modules(modules)
                           .buildInjector().getInstance(SequentialMultipartUploadStrategy.class);
   }

   /**
    * there's no built-in way to defer evaluation of a response header, hence
    * this method, which allows us to send back links to the mock server.
    */
   private AtomicReference<URL> setURLReplacingDispatcher(MockWebServer server) {
      final AtomicReference<URL> url = new AtomicReference<URL>();

      final QueueDispatcher dispatcher = new QueueDispatcher() {
         protected final BlockingQueue<MockResponse> responseQueue = new LinkedBlockingQueue<MockResponse>();

         @Override
         public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            MockResponse response = responseQueue.take();
            if (response.getBody() != null) {
               String newBody = new String(response.getBody()).replace("URL", url.get().toString());
               response = response.setBody(newBody);
            }
            return response;
         }

         @Override
         public void enqueueResponse(MockResponse response) {
            responseQueue.add(response);
         }
      };
      server.setDispatcher(dispatcher);
      return url;
   }
}
