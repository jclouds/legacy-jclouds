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

package org.jclouds.mezeo.pcs2;

import static org.jclouds.rest.RestContextFactory.contextSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.mezeo.pcs2.PCSCloudAsyncClient.Response;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSCloudTest")
public class PCSCloudTest extends RestClientTest<PCSCloudAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = PCSCloudAsyncClient.class.getMethod("authenticate");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://localhost:8080/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CloudXlinkHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<PCSCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<PCSCloudAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<PCSCloudClient, PCSCloudAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "identity", "credential",
               PCSCloudClient.class, PCSCloudAsyncClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface PCSCloudClient {

      Response authenticate();
   }

}
