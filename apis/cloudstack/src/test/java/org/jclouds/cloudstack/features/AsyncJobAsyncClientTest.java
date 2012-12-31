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
package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.functions.ParseAsyncJobFromHttpResponse;
import org.jclouds.cloudstack.functions.ParseAsyncJobsFromHttpResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListAsyncJobsOptions;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AsyncJobAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AsyncJobAsyncClientTest")
public class AsyncJobAsyncClientTest extends BaseCloudStackAsyncClientTest<AsyncJobAsyncClient> {

   public void testGetAsyncJob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AsyncJobAsyncClient.class.getMethod("getAsyncJob", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 11l);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=queryAsyncJobResult&jobid=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAsyncJobFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListAsyncJobs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AsyncJobAsyncClient.class.getMethod("listAsyncJobs", ListAsyncJobsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listAsyncJobs&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAsyncJobsFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListAsyncJobsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AsyncJobAsyncClient.class.getMethod("listAsyncJobs", ListAsyncJobsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method,
            ListAsyncJobsOptions.Builder.accountInDomain("adrian", "5"));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listAsyncJobs&listAll=true&account=adrian&domainid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAsyncJobsFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
