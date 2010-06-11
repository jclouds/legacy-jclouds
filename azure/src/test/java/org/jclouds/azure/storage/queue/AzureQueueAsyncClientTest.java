/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.queue;

import static org.jclouds.azure.storage.options.CreateOptions.Builder.withMetadata;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.jclouds.azure.storage.queue.options.GetOptions.Builder.maxMessages;
import static org.jclouds.azure.storage.queue.options.PutMessageOptions.Builder.withTTL;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.config.AzureQueueRestClientModule;
import org.jclouds.azure.storage.queue.options.GetOptions;
import org.jclouds.azure.storage.queue.options.PutMessageOptions;
import org.jclouds.azure.storage.queue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.queue.xml.QueueMessagesListHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code AzureQueueAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azurequeue.AzureQueueAsyncClientTest")
public class AzureQueueAsyncClientTest extends RestClientTest<AzureQueueAsyncClient> {

   public void testGetMessages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("getMessages", String.class,
               GetOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "myqueue");

      assertRequestLineEquals(httpRequest,
               "GET https://myaccount.queue.core.windows.net/myqueue/messages HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueMessagesListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testGetMessagesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("getMessages", String.class,
               GetOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "myqueue", maxMessages(1).visibilityTimeout(30));

      assertRequestLineEquals(
               httpRequest,
               "GET https://myaccount.queue.core.windows.net/myqueue/messages?numofmessages=1&visibilitytimeout=30 HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueMessagesListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListQueues() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("listQueues", ListOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET https://myaccount.queue.core.windows.net/?comp=list HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testListQueuesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("listQueues", ListOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               maxResults(1).marker("marker").prefix("prefix"));

      assertRequestLineEquals(
               httpRequest,
               "GET https://myaccount.queue.core.windows.net/?comp=list&maxresults=1&marker=marker&prefix=prefix HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testCreateQueue() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureQueueAsyncClient.class.getMethod("createQueue", String.class,
               CreateOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue");

      assertRequestLineEquals(httpRequest,
               "PUT https://myaccount.queue.core.windows.net/queue HTTP/1.1");
      assertHeadersEqual(httpRequest, "Content-Length: 0\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testCreateQueueOptions() throws SecurityException, NoSuchMethodException,
            IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("createQueue", String.class,
               CreateOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue", withMetadata(ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(httpRequest,
               "PUT https://myaccount.queue.core.windows.net/queue HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Content-Length: 0\nx-ms-meta-foo: bar\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testDeleteQueue() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("deleteQueue", String.class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue");

      assertRequestLineEquals(httpRequest,
               "DELETE https://myaccount.queue.core.windows.net/queue HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testPutMessage() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("putMessage", String.class,
               String.class, PutMessageOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue", "message");

      assertRequestLineEquals(httpRequest,
               "POST https://myaccount.queue.core.windows.net/queue/messages HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Content-Length: 63\nContent-Type: application/unknown\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest,
               "<QueueMessage><MessageText>message</MessageText></QueueMessage>");

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testPutMessageOptions() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("putMessage", String.class,
               String.class, PutMessageOptions[].class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue", "message", withTTL(3));

      assertRequestLineEquals(httpRequest,
               "POST https://myaccount.queue.core.windows.net/queue/messages?messagettl=3 HTTP/1.1");
      assertHeadersEqual(httpRequest,
               "Content-Length: 63\nContent-Type: application/unknown\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest,
               "<QueueMessage><MessageText>message</MessageText></QueueMessage>");

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testClearMessages() throws SecurityException, NoSuchMethodException, IOException {

      Method method = AzureQueueAsyncClient.class.getMethod("clearMessages", String.class);
      GeneratedHttpRequest<AzureQueueAsyncClient> httpRequest = processor.createRequest(method,
               "queue");

      assertRequestLineEquals(httpRequest,
               "DELETE https://myaccount.queue.core.windows.net/queue/messages HTTP/1.1");
      assertHeadersEqual(httpRequest, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<AzureQueueAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureQueueAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureQueueAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AzureQueueRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new AzureQueuePropertiesBuilder(new Properties())
                     .withCredentials("myaccount", "key").build());
            install(new NullLoggingModule());
            super.configure();
         }

      };
   }
}
