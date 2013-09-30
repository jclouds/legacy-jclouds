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
package org.jclouds.hpcloud.objectstorage.internal;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageApi;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.QueueDispatcher;
import com.google.mockwebserver.RecordedRequest;

public class BaseHPCloudObjectStorageMockTest {

   public static HPCloudObjectStorageApi api(String uri) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");

      return ContextBuilder.newBuilder("hpcloud-objectstorage") //
            .credentials("jclouds:joe", "letmein") //
            .endpoint(uri) //
            .overrides(overrides) //
            .modules(ImmutableSet.<Module> of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()))) //
            .buildApi(HPCloudObjectStorageApi.class);
   }

   public static MockWebServer mockWebServer() throws IOException {
      MockWebServer server = new MockWebServer();
      server.play();
      URL url = server.getUrl("");
      server.setDispatcher(getURLReplacingQueueDispatcher(url));
      return server;
   }

   /**
    * there's no built-in way to defer evaluation of a response header, hence
    * this method, which allows us to send back links to the mock server.
    */
   public static QueueDispatcher getURLReplacingQueueDispatcher(final URL url) {
      final QueueDispatcher dispatcher = new QueueDispatcher() {
         protected final BlockingQueue<MockResponse> responseQueue = new LinkedBlockingQueue<MockResponse>();

         @Override
         public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            MockResponse response = responseQueue.take();
            if (response.getBody() != null) {
               String newBody = new String(response.getBody()).replace(":\"URL", ":\"" + url.toString());
               response = response.setBody(newBody);
            }
            return response;
         }

         @Override
         public void enqueueResponse(MockResponse response) {
            responseQueue.add(response);
         }
      };
      return dispatcher;
   }
}
