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
package org.jclouds.trmk.ecloud;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.testng.annotations.Test;

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
public class TerremarkECloudClientMockTest {
   
   private static final Set<Module> modules = ImmutableSet.<Module> of(
         new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));

   static TerremarkECloudApi mockTerremarkECloudClient(String uri) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("trmk-ecloud")
                           .credentials("user", "password")
                           .endpoint(uri)
                           .overrides(overrides)
                           .modules(modules)
                           .buildApi(TerremarkECloudApi.class);
   }

   String versionXML = "<SupportedVersions><VersionInfo><Version>0.8b-ext2.8</Version><LoginUrl>URLv0.8/login</LoginUrl></VersionInfo></SupportedVersions>";

   @Test
   public void testLoginSetsContentLength() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.play();
      server.setDispatcher(replaceURLWithLocalhostPort(server.getPort()));
      server.enqueue(new MockResponse().setResponseCode(200).setBody(versionXML));
      server.enqueue(new MockResponse().setResponseCode(200)
                                       .addHeader("x-vcloud-authorization", "cookie")
                                       .setBody("<OrgList />"));

      TerremarkECloudApi api = mockTerremarkECloudClient(server.getUrl("/").toString());

      try {
         api.listOrgs();
         RecordedRequest getVersions = server.takeRequest();
         assertEquals(getVersions.getRequestLine(), "GET /versions HTTP/1.1");

         RecordedRequest login = server.takeRequest();
         assertEquals(login.getRequestLine(), "POST /v0.8/login HTTP/1.1");
         assertEquals(login.getHeader("Authorization"), "Basic dXNlcjpwYXNzd29yZA==");
         assertEquals(login.getHeader("Content-Length"), "0");
      } finally {
         server.shutdown();
      }
   }

    /**
     * this pattern is used for HATEOAS or similar apis which return urls for
     * further requests. If we don't replace here, the test cannot be bound to
     * the same MWS instance as it was created with.
     */
   private QueueDispatcher replaceURLWithLocalhostPort(final int port) {
      return new QueueDispatcher() {
         protected final BlockingQueue<MockResponse> responseQueue = new LinkedBlockingQueue<MockResponse>();

         @Override
         public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            MockResponse response = responseQueue.take();
            if (response.getBody() != null) {
               String newBody = new String(response.getBody()).replace("URL", "http://localhost:" + port + "/");
               response = response.setBody(newBody);
            }
            return response;
         }

         @Override
         public void enqueueResponse(MockResponse response) {
            responseQueue.add(response);
         }
      };
   }
}
