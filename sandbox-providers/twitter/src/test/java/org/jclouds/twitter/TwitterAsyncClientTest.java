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
package org.jclouds.twitter;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.twitter.config.TwitterRestClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code TwitterAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TwitterAsyncClientTest extends RestClientTest<TwitterAsyncClient> {
   public void testGetMyMentions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TwitterAsyncClient.class.getMethod("getMyMentions");
      GeneratedHttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET http://api.twitter.com/statuses/mentions.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      httpRequest = (GeneratedHttpRequest) Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET http://api.twitter.com/statuses/mentions.json HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\nAuthorization: Basic Zm9vOmJhcg==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

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
   protected Module createModule() {
      return new TwitterRestClientModuleExtension();
   }

   @RequiresHttp
   @ConfiguresRestClient
   public static class TwitterRestClientModuleExtension extends TwitterRestClientModule {

      // Override here
   }

   @Override
   public RestContextSpec<TwitterClient, TwitterAsyncClient> createContextSpec() {
      // TODO take this out, when the service is registered in jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties.setProperty("twitter.contextbuilder", "org.jclouds.twitter.TwitterContextBuilder");
      restProperties.setProperty("twitter.propertiesbuilder", "org.jclouds.twitter.TwitterPropertiesBuilder");

      Properties props = new Properties();
      props.setProperty("twitter.endpoint", "http://api.twitter.com");
      return new RestContextFactory(restProperties).createContextSpec("twitter", "foo", "bar", props);
   }
}
