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
package org.jclouds.twitter;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.twitter.functions.ParseStatusesFromJsonResponse;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TwitterClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "twitter.TwitterClientTest")
public class TwitterAsyncClientTest extends RestClientTest<TwitterAsyncClient> {

   public void testGetMyMentions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TwitterAsyncClient.class.getMethod("getMyMentions");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://twitter.com/statuses/mentions.json HTTP/1.1");
      assertHeadersEqual(request, "");
      assertPayloadEquals(request, null);

      assertResponseParserClassEquals(method, request, ParseStatusesFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TwitterAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TwitterAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<TwitterClient, TwitterAsyncClient> createContextSpec() {
      return contextSpec("test", "http://twitter.com", "1", "identity", "credential",
               TwitterClient.class, TwitterAsyncClient.class);
   }

}
