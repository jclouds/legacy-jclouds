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

package org.jclouds.nirvanix.sdn;

import static org.jclouds.rest.RestContextFactory.contextSpec;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.http.HttpRequest;
import org.jclouds.nirvanix.sdn.SDNAuthenticationLiveTest.SDNAuthClient;
import org.jclouds.nirvanix.sdn.functions.ParseSessionTokenFromJsonResponse;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.SDNAuthentication")
public class SDNAuthAsyncClientTest extends RestClientTest<SDNAuthAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SDNAuthAsyncClient.class.getMethod("authenticate", String.class, String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "apple", "foo", "bar");
      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/ws/Authentication/Login.ashx?output=json&appKey=apple&password=bar&username=foo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSessionTokenFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SDNAuthAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SDNAuthAsyncClient>>() {
      };
   }

   @Override
   public RestContextSpec<SDNAuthClient, SDNAuthAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "", "identity", "credential", SDNAuthClient.class,
               SDNAuthAsyncClient.class);
   }

}
