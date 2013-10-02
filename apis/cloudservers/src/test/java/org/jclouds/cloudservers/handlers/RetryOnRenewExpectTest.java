/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudservers.handlers;

import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.internal.BaseCloudServersRestClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "RetryOnRenewExpectTest")
public class RetryOnRenewExpectTest extends BaseCloudServersRestClientExpectTest {

   @Test
   public void testShouldReauthenticateOn401() {

      HttpRequest deleteImage = HttpRequest.builder().method("DELETE")
            .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/11?now=1257695648897")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse pleaseRenew = HttpResponse
            .builder()
            .statusCode(401)
            .message("HTTP/1.1 401 Unauthorized")
            .payload("[{\"unauthorized\":{\"message\":\"Invalid authentication token.  Please renew.\",\"code\":401}}]")
            .build();

      // second auth uses same creds as initial one
      HttpRequest redoAuth = initialAuth;
      
      String authToken2 = "12345678-9012-47c0-9770-2c5097da25fc";

      HttpResponse responseWithUrls2 = responseWithAuth.toBuilder()
                                                       .payload(responseWithAuth.getPayload().getRawContent().toString().replace(authToken, authToken2))
                                                       .build();

      HttpRequest deleteImage2 = HttpRequest.builder().method("DELETE")
            .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/11?now=1257695648897")
            .addHeader("X-Auth-Token", authToken2).build();

      HttpResponse imageDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudServersClient clientWhenImageExists = orderedRequestsSendResponses(initialAuth, responseWithAuth,
            deleteImage, pleaseRenew, redoAuth, responseWithUrls2, deleteImage2, imageDeleted);

      assert clientWhenImageExists.deleteImage(11);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testDoesNotReauthenticateOnFatal401() {
      HttpRequest deleteImage = HttpRequest.builder().method("DELETE")
            .endpoint("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/11?now=1257695648897")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse unauthResponse = HttpResponse
            .builder()
            .statusCode(401)
            .message("HTTP/1.1 401 Unauthorized")
            .payload("[{\"unauthorized\":{\"message\":\"Fatal unauthorized.\",\"code\":401}}]")
            .build();

      CloudServersClient client = orderedRequestsSendResponses(initialAuth, responseWithAuth, deleteImage,
            unauthResponse);

      client.deleteImage(11);
   }

   // FIXME stack trace shows the AuthorizationException, but it's buried inside
   // a guice TestException
   @Test(expectedExceptions = AuthorizationException.class)
   public void testDoesNotReauthenticateOnAuthentication401() {

      HttpResponse unauthResponse = HttpResponse
            .builder()
            .statusCode(401)
            .message("HTTP/1.1 401 Unauthorized")
            .payload(
                  Payloads
                        .newStringPayload("[{\"unauthorized\":{\"message\":\"A different message implying fatal.\",\"code\":401}}]"))
            .build();

      CloudServersClient client = requestSendsResponse(initialAuth, unauthResponse);

      client.deleteImage(11);
   }
}
