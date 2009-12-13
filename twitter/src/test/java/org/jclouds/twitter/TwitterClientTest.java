/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.twitter;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.twitter.functions.ParseStatusesFromJsonResponse;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TwitterClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "twitter.TwitterClientTest")
public class TwitterClientTest extends RestClientTest<TwitterAsyncClient> {

   public void testGetMyMentions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TwitterAsyncClient.class.getMethod("getMyMentions");
      GeneratedHttpRequest<TwitterAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://twitter.com/statuses/mentions.json HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseStatusesFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<TwitterAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TwitterAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TwitterAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(Twitter.class).toInstance(
                     URI.create("http://twitter.com"));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication(EncryptionService encryptionService)
                  throws UnsupportedEncodingException {
            return new BasicAuthentication("foo", "bar", encryptionService);
         }

      };
   }
}
