/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.http.handlers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;

@Test(groups = "unit", testName = "core.BackoffLimitedRetryHandler")
public class BackoffLimitedRetryHandlerTest {
   private static final URI END_POINT = URI.create("http://localhost:8080");

   BackoffLimitedRetryHandler handler = new BackoffLimitedRetryHandler(5);

   @Test
   void testExponentialBackoffDelay() throws InterruptedException {
      long acceptableDelay = 25; // Delay to forgive if tests run long.

      long startTime = System.currentTimeMillis();
      handler.imposeBackoffExponentialDelay(1, "TEST FAILURE: 1");
      long elapsedTime = System.currentTimeMillis() - startTime;
      assertTrue(elapsedTime >= 50);
      assertTrue(elapsedTime < 50 + acceptableDelay);

      startTime = System.currentTimeMillis();
      handler.imposeBackoffExponentialDelay(2, "TEST FAILURE: 2");
      elapsedTime = System.currentTimeMillis() - startTime;
      assertTrue(elapsedTime >= 200);
      assertTrue(elapsedTime < 200 + acceptableDelay);

      startTime = System.currentTimeMillis();
      handler.imposeBackoffExponentialDelay(3, "TEST FAILURE: 3");
      elapsedTime = System.currentTimeMillis() - startTime;
      assertTrue(elapsedTime >= 450);
      assertTrue(elapsedTime < 450 + acceptableDelay);

      startTime = System.currentTimeMillis();
      handler.imposeBackoffExponentialDelay(4, "TEST FAILURE: 4");
      elapsedTime = System.currentTimeMillis() - startTime;
      assertTrue(elapsedTime >= 800);
      assertTrue(elapsedTime < 800 + acceptableDelay);

      startTime = System.currentTimeMillis();
      handler.imposeBackoffExponentialDelay(5, "TEST FAILURE: 5");
      elapsedTime = System.currentTimeMillis() - startTime;
      assertTrue(elapsedTime >= 1250);
      assertTrue(elapsedTime < 1250 + acceptableDelay);
   }

   TransformingHttpCommandExecutorServiceImpl executorService;

   @BeforeTest
   void setupExecutorService() throws Exception {
      ExecutorService execService = Executors.newCachedThreadPool();
      JavaUrlHttpCommandExecutorService httpService = new JavaUrlHttpCommandExecutorService(
               execService, new DelegatingRetryHandler(), new DelegatingErrorHandler());
      executorService = new TransformingHttpCommandExecutorServiceImpl(httpService, execService);
   }

   @Test
   void testClosesInputStream() throws InterruptedException, IOException {
      HttpCommand command = createCommand();

      HttpResponse response = new HttpResponse(new URL("file:///unused"));
      InputStream inputStream = new InputStream() {
         boolean isOpen = true;

         @Override
         public void close() {
            this.isOpen = false;
         }

         @Override
         public int read() throws IOException {
            if (this.isOpen)
               return 1;
            else
               return -1;
         }

         @Override
         public int available() throws IOException {
            if (this.isOpen)
               return 1;
            else
               return 0;
         }
      };
      response.setContent(inputStream);

      assertEquals(response.getContent().available(), 1);
      assertEquals(response.getContent().read(), 1);

      handler.shouldRetryRequest(command, response);

      assertEquals(response.getContent().available(), 0);
      assertEquals(response.getContent().read(), -1);
   }

   private final HttpRequest request = new HttpRequest("HEAD", END_POINT);

   private HttpCommand createCommand() {
      HttpCommand command = new TransformingHttpCommandImpl<String>(executorService, request,
               new ReturnStringIf200(), new Function<Exception, String>() {
                  public String apply(Exception from) {
                     return null;
                  }
               });
      return command;
   }

   @Test
   void testIncrementsFailureCount() throws InterruptedException, IOException {
      HttpCommand command = createCommand();
      HttpResponse response = new HttpResponse(new URL("file:///unused"));

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 1);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 2);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 3);
   }

   @Test
   void testDisallowsExcessiveRetries() throws InterruptedException, IOException {
      HttpCommand command = createCommand();
      HttpResponse response = new HttpResponse(new URL("file:///unused"));

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 1

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 2

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 3

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 4

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 5

      assertEquals(handler.shouldRetryRequest(command, response), false); // Failure 6
   }

}