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
package org.jclouds.aws.sqs;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.sqs.options.CreateQueueOptions;
import org.jclouds.aws.sqs.options.ListQueuesOptions;
import org.jclouds.aws.sqs.xml.ListQueuesResponseHandler;
import org.jclouds.aws.sqs.xml.QueueHandler;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SQSAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sqs.SQSAsyncClientTest")
public class SQSAsyncClientTest extends RestClientTest<SQSAsyncClient> {
   public void testListQueuesInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SQSAsyncClient.class.getMethod("listQueuesInRegion", Region.class, Array
               .newInstance(ListQueuesOptions.class, 0).getClass());
      GeneratedHttpRequest<SQSAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT);

      assertRequestLineEquals(httpMethod, "POST https://default/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 36\nContent-Type: application/x-www-form-urlencoded\nHost: default\n");
      assertPayloadEquals(httpMethod, "Version=2009-02-01&Action=ListQueues");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListQueuesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testListQueuesInRegionOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SQSAsyncClient.class.getMethod("listQueuesInRegion", Region.class, Array
               .newInstance(ListQueuesOptions.class, 0).getClass());
      GeneratedHttpRequest<SQSAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, ListQueuesOptions.Builder.queuePrefix("prefix"));

      assertRequestLineEquals(httpMethod, "POST https://default/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 59\nContent-Type: application/x-www-form-urlencoded\nHost: default\n");
      assertPayloadEquals(httpMethod, "Version=2009-02-01&Action=ListQueues&QueueNamePrefix=prefix");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListQueuesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateQueueInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SQSAsyncClient.class.getMethod("createQueueInRegion", Region.class,
               String.class, Array.newInstance(CreateQueueOptions.class, 0).getClass());
      GeneratedHttpRequest<SQSAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "queueName");

      assertRequestLineEquals(httpMethod, "POST https://default/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 57\nContent-Type: application/x-www-form-urlencoded\nHost: default\n");
      assertPayloadEquals(httpMethod, "Version=2009-02-01&Action=CreateQueue&QueueName=queueName");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateQueueInRegionOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SQSAsyncClient.class.getMethod("createQueueInRegion", Region.class,
               String.class, Array.newInstance(CreateQueueOptions.class, 0).getClass());
      GeneratedHttpRequest<SQSAsyncClient> httpMethod = processor
               .createRequest(method, Region.DEFAULT, "queueName", CreateQueueOptions.Builder
                        .defaultVisibilityTimeout(45));

      assertRequestLineEquals(httpMethod, "POST https://default/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 85\nContent-Type: application/x-www-form-urlencoded\nHost: default\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-02-01&Action=CreateQueue&QueueName=queueName&DefaultVisibilityTimeout=45");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, QueueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<SQSAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SQSAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SQSAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(SQS.class).toInstance(URI.create("https://default"));
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String provide() {
            return "2009-11-08T15:54:08.897Z";
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         Map<Region, URI> provideMap() {
            return ImmutableMap.<Region, URI> of(Region.DEFAULT, URI.create("https://booya"),
                     Region.EU_WEST_1, URI.create("https://sqs.eu-west-1.amazonaws.com"),
                     Region.US_EAST_1, URI.create("https://sqs.us-east-1.amazonaws.com"),
                     Region.US_WEST_1, URI.create("https://sqs.us-west-1.amazonaws.com"));
         }
      };
   }
}
