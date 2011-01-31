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

package org.jclouds.vcloud;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.xml.SupportedVersionsHandler;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudVersionsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VCloudVersionsAsyncClientTest")
public class VCloudVersionsAsyncClientTest extends RestClientTest<VCloudVersionsAsyncClient> {

   public void testVersions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudVersionsAsyncClient.class.getMethod("getSupportedVersions");
      HttpRequest request = processor.createRequest(method);

      assertEquals(request.getRequestLine(), "GET http://localhost:8080/versions HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SupportedVersionsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 0);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudVersionsAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudVersionsAsyncClient>>() {
      };
   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface VCloudVersionsClient {

      SortedMap<String, URI> getSupportedVersions();
   }

   @Override
   public RestContextSpec<VCloudVersionsClient, VCloudVersionsAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "", "identity", "credential",
               VCloudVersionsClient.class, VCloudVersionsAsyncClient.class);
   }

}
