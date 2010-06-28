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
package org.jclouds.nirvanix.sdn;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.nirvanix.sdn.SDNAuthenticationLiveTest.SDNAuthClient;
import org.jclouds.nirvanix.sdn.functions.ParseSessionTokenFromJsonResponse;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.GeneratedHttpRequest;
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

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = SDNAuthAsyncClient.class.getMethod("authenticate", String.class,
               String.class, String.class);
      HttpRequest httpMethod = processor.createRequest(method,
               new Object[] { "apple", "foo", "bar" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/ws/Authentication/Login.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "output=json&appKey=apple&password=bar&username=foo");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(RestAnnotationProcessor.getParserOrThrowException(method),
               ParseSessionTokenFromJsonResponse.class);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<SDNAuthAsyncClient> httpMethod) {

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SDNAuthAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SDNAuthAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<SDNAuthClient, SDNAuthAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "identity", "credential",
               SDNAuthClient.class, SDNAuthAsyncClient.class);
   }

}
