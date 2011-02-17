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

package org.jclouds.openstack;

import static org.jclouds.rest.RestContextFactory.contextSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.OpenStackAuthAsyncClient;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code OpenStackAuthAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "OpenStackAuthAsyncClientTest")
public class OpenStackAuthAsyncClientTest extends RestClientTest<OpenStackAuthAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OpenStackAuthAsyncClient.class.getMethod("authenticate", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "foo", "bar");

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/v1.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "X-Auth-Key: bar\nX-Auth-User: foo\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAuthenticationResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

   }

   @Override
   public RestContextSpec<OpenStackAuthClient, OpenStackAuthAsyncClient> createContextSpec() {
      return contextSpec("test", "http://localhost:8080", "1.0", "", "identity", "credential",
               OpenStackAuthClient.class, OpenStackAuthAsyncClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<OpenStackAuthAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<OpenStackAuthAsyncClient>>() {
      };
   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface OpenStackAuthClient {

      AuthenticationResponse authenticate(String user, String key);
   }
}
