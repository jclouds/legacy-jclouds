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
package org.jclouds.trmk.ecloud;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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

   static TerremarkECloudClient mockTerremarkECloudClient(String uri) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("trmk-ecloud")
                           .credentials("user", "password")
                           .endpoint(uri)
                           .overrides(overrides)
                           .modules(modules)
                           .build(TerremarkECloudApiMetadata.CONTEXT_TOKEN).getApi();
   }

   String versionXML = "<SupportedVersions><VersionInfo><Version>0.8b-ext2.8</Version><LoginUrl>URLv0.8/login</LoginUrl></VersionInfo></SupportedVersions>";

   @Test
   public void testLoginSetsContentLength() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      AtomicReference<URL> url = setURLReplacingDispatcher(server);
      server.enqueue(new MockResponse().setResponseCode(200).setBody(versionXML));
      server.enqueue(new MockResponse().setResponseCode(200)
                                       .addHeader("x-vcloud-authorization", "cookie")
                                       .setBody("<OrgList />"));
      server.play();
      url.set(server.getUrl("/"));

      TerremarkECloudClient api = mockTerremarkECloudClient(url.get().toString());

      try {
         api.listOrgs();
      } finally {
         RecordedRequest getVersions = server.takeRequest();
         assertEquals(getVersions.getRequestLine(), "GET /versions HTTP/1.1");

         RecordedRequest login = server.takeRequest();
         assertEquals(login.getRequestLine(), "POST /v0.8/login HTTP/1.1");
         assertEquals(login.getHeader("Authorization"), "Basic dXNlcjpwYXNzd29yZA==");
         assertEquals(login.getHeader("Content-Length"), "0");

         server.shutdown();
      }
   }

   /**
    * there's no built-in way to defer evaluation of a response header, hence this
    * method, which allows us to send back links to the mock server.
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
