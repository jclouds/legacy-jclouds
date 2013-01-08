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
package org.jclouds.azurequeue;

import static org.jclouds.azure.storage.options.CreateOptions.Builder.withMetadata;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.jclouds.azurequeue.options.GetOptions.Builder.maxMessages;
import static org.jclouds.azurequeue.options.PutMessageOptions.Builder.withTTL;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azurequeue.options.GetOptions;
import org.jclouds.azurequeue.options.PutMessageOptions;
import org.jclouds.azurequeue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azurequeue.xml.QueueMessagesListHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureQueueAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AzureQueueAsyncClientTest")
public class AzureQueueAsyncClientTest extends RestClientTest<AzureQueueAsyncClient> {

   public void testGetMessages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("getMessages", String.class, GetOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "myqueue");

      assertRequestLineEquals(request, "GET https://identity.queue.core.windows.net/myqueue/messages HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueMessagesListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetMessagesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("getMessages", String.class, GetOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "myqueue", maxMessages(1).visibilityTimeout(30));

      assertRequestLineEquals(request,
            "GET https://identity.queue.core.windows.net/myqueue/messages?numofmessages=1&visibilitytimeout=30 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueMessagesListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListQueues() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("listQueues", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://identity.queue.core.windows.net/?comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListQueuesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("listQueues", ListOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, maxResults(1).marker("marker").prefix("prefix"));

      assertRequestLineEquals(request,
            "GET https://identity.queue.core.windows.net/?comp=list&maxresults=1&marker=marker&prefix=prefix HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateQueue() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("createQueue", String.class, CreateOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue");

      assertRequestLineEquals(request, "PUT https://identity.queue.core.windows.net/queue HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateQueueOptions() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("createQueue", String.class, CreateOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue", withMetadata(ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(request, "PUT https://identity.queue.core.windows.net/queue HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-meta-foo: bar\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteQueue() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("deleteQueue", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue");

      assertRequestLineEquals(request, "DELETE https://identity.queue.core.windows.net/queue HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);

   }

   public void testPutMessage() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("putMessage", String.class, String.class,
            PutMessageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue", "message");

      assertRequestLineEquals(request, "POST https://identity.queue.core.windows.net/queue/messages HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, "<QueueMessage><MessageText>message</MessageText></QueueMessage>",
            "application/unknown", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testPutMessageOptions() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("putMessage", String.class, String.class,
            PutMessageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue", "message", withTTL(3));

      assertRequestLineEquals(request,
            "POST https://identity.queue.core.windows.net/queue/messages?messagettl=3 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, "<QueueMessage><MessageText>message</MessageText></QueueMessage>",
            "application/unknown", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testClearMessages() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("clearMessages", String.class);
      GeneratedHttpRequest request = processor.createRequest(method, "queue");

      assertRequestLineEquals(request, "DELETE https://identity.queue.core.windows.net/queue/messages HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureQueueAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureQueueAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("azurequeue", "identity", "credential", new Properties());
   }

}
