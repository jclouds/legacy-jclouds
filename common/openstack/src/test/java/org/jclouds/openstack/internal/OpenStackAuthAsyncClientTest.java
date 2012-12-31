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
package org.jclouds.openstack.internal;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.openstack.functions.ParseAuthenticationResponseFromHeaders;
import org.jclouds.rest.AnonymousRestApiMetadata;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code OpenStackAuthAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "OpenStackAuthAsyncClientTest")
public class OpenStackAuthAsyncClientTest extends BaseAsyncClientTest<OpenStackAuthAsyncClient> {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OpenStackAuthAsyncClient.class.getMethod("authenticate", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "foo", "bar");

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/v1.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: */*\nHost: localhost:8080\nX-Auth-Key: bar\nX-Auth-User: foo\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAuthenticationResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

   }

   public void testAuthenticateStorage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = OpenStackAuthAsyncClient.class.getMethod("authenticateStorage", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "foo", "bar");

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/v1.0 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: */*\nHost: localhost:8080\nX-Storage-Pass: bar\nX-Storage-User: foo\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAuthenticationResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

   }

   @Override
   public ApiMetadata createApiMetadata() {
      return AnonymousRestApiMetadata.forClientMappedToAsyncClient(IntegrationTestClient.class, IntegrationTestAsyncClient.class).toBuilder().defaultEndpoint(
            "http://localhost:8080").version("1.0").build();
   }

   @Override
   protected void checkFilters(HttpRequest request) {
   }
}
