/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.sqs;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.sqs.config.SQSRestClientModule;
import org.jclouds.aws.sqs.options.CreateQueueOptions;
import org.jclouds.aws.sqs.options.ListQueuesOptions;
import org.jclouds.aws.sqs.xml.RegexListQueuesResponseHandler;
import org.jclouds.aws.sqs.xml.RegexQueueHandler;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SQSAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sqs.SQSAsyncClientTest")
public class SQSAsyncClientTest extends RestClientTest<SQSAsyncClient> {

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestSQSRestClientModule extends SQSRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

      @Override
      protected String provideTimeStamp(final DateService dateService,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int expiration) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   public void testListQueuesInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SQSAsyncClient.class.getMethod("listQueuesInRegion", String.class, Array.newInstance(
            ListQueuesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://sqs.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sqs.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-02-01&Action=ListQueues", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, RegexListQueuesResponseHandler.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListQueuesInRegionOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SQSAsyncClient.class.getMethod("listQueuesInRegion", String.class, Array.newInstance(
            ListQueuesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, ListQueuesOptions.Builder.queuePrefix("prefix"));

      assertRequestLineEquals(request, "POST https://sqs.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sqs.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-02-01&Action=ListQueues&QueueNamePrefix=prefix",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, RegexListQueuesResponseHandler.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateQueueInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SQSAsyncClient.class.getMethod("createQueueInRegion", String.class, String.class, Array
            .newInstance(CreateQueueOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "queueName");

      assertRequestLineEquals(request, "POST https://sqs.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sqs.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-02-01&Action=CreateQueue&QueueName=queueName",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, RegexQueueHandler.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateQueueInRegionOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SQSAsyncClient.class.getMethod("createQueueInRegion", String.class, String.class, Array
            .newInstance(CreateQueueOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "queueName", CreateQueueOptions.Builder
            .defaultVisibilityTimeout(45));

      assertRequestLineEquals(request, "POST https://sqs.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sqs.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Version=2009-02-01&Action=CreateQueue&QueueName=queueName&DefaultVisibilityTimeout=45",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, RegexQueueHandler.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAllRegions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SQSAsyncClient.class.getMethod("createQueueInRegion", String.class, String.class, Array
            .newInstance(CreateQueueOptions.class, 0).getClass());
      for (String region : Iterables.filter(Region.ALL_SQS, not(equalTo("us-standard")))) {
         processor.createRequest(method, region, "queueName");
      }
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SQSAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SQSAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TestSQSRestClientModule();
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("sqs", "identity", "credential", new Properties());
   }

}
