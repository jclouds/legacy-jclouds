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
package org.jclouds.rackspace;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Collections;

import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.RackspaceAuthenticationLiveTest.RackspaceAuthClient;
import org.jclouds.rackspace.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.rackspace.reference.RackspaceHeaders;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rackspace.RackspaceAuthentication")
public class RackspaceAuthAsyncClientTest extends RestClientTest<RackspaceAuthAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = RackspaceAuthAsyncClient.class.getMethod("authenticate", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, "foo", "bar");
      assertEquals(request.getEndpoint().getHost(), "localhost");
      assertEquals(request.getEndpoint().getPath(), "/auth");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(RackspaceHeaders.AUTH_USER), Collections
               .singletonList("foo"));
      assertEquals(request.getHeaders().get(RackspaceHeaders.AUTH_KEY), Collections
               .singletonList("bar"));
      assertEquals(RestAnnotationProcessor.getParserOrThrowException(method),
               ParseAuthenticationResponseFromHeaders.class);

   }

   @Override
   public ContextSpec<RackspaceAuthClient, RackspaceAuthAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1", "identity", "credential",
               RackspaceAuthClient.class, RackspaceAuthAsyncClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<RackspaceAuthAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<RackspaceAuthAsyncClient>>() {
      };
   }

}
