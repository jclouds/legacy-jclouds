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
package org.jclouds.http;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests the retry behavior of the default {@link RetryHandler} implementation
 * {@link BackoffLimitedRetryHandler} to ensure that retries up to the default limit succeed.
 * 
 * TODO: Should either explicitly set retry limit or get it from Guice, rather than assuming it's 5.
 * 
 * @author James Murty
 */
@Test(sequential = true)
public class BackoffLimitedRetryJavaTest extends BaseJettyTest {
   private int beginToFailOnRequestNumber = 0;
   private int endFailuresOnRequestNumber = 0;
   private int requestCount = 0;

   @Override
   protected void addConnectionProperties(Properties props) {
   }

   @Override
   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   @Override
   protected boolean failEveryTenRequests(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
      requestCount++;
      boolean shouldFail = requestCount >= beginToFailOnRequestNumber
               && requestCount <= endFailuresOnRequestNumber;
      if (shouldFail) {
         response.sendError(500);
         ((Request) request).setHandled(true);
         return true;
      } else {
         return false;
      }
   }

   protected String submitGetRequest() throws InterruptedException, ExecutionException {
      return client.download("");
   }

   @Test
   public void testNoRetriesSuccessful() throws InterruptedException, ExecutionException {
      beginToFailOnRequestNumber = 1;
      endFailuresOnRequestNumber = 1;
      requestCount = 0;

      assertEquals(submitGetRequest().trim(), XML);
   }

   @Test
   public void testSingleRetrySuccessful() throws InterruptedException, ExecutionException {
      beginToFailOnRequestNumber = 0;
      endFailuresOnRequestNumber = 1;
      requestCount = 0;

      assertEquals(submitGetRequest().trim(), XML);
   }

   @Test
   public void testMaximumRetriesSuccessful() throws InterruptedException, ExecutionException {
      beginToFailOnRequestNumber = 0;
      endFailuresOnRequestNumber = 5;
      requestCount = 0;

      assertEquals(submitGetRequest().trim(), XML);
   }

   @Test
   public void testMaximumRetriesExceeded() throws InterruptedException, ExecutionException {
      beginToFailOnRequestNumber = 0;
      endFailuresOnRequestNumber = 6;
      requestCount = 0;

      try {
         submitGetRequest();
         fail("Request should not succeed within " + endFailuresOnRequestNumber + " requests");
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 500);
      }
   }

   @Test
   public void testInterleavedSuccessesAndFailures() throws InterruptedException,
            ExecutionException {
      beginToFailOnRequestNumber = 3;
      endFailuresOnRequestNumber = 3 + 5; // Force third request to fail completely
      requestCount = 0;

      assertEquals(submitGetRequest().trim(), XML);
      assertEquals(submitGetRequest().trim(), XML);

      try {
         submitGetRequest();
         fail("Third request should not succeed by attempt number " + requestCount);
      } catch (HttpResponseException e) {
         assertEquals(e.getResponse().getStatusCode(), 500);
      }

      assertEquals(submitGetRequest().trim(), XML);
   }

}
