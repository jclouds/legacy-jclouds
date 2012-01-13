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
package org.jclouds.cloudservers.handlers;

import java.net.URI;

import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.internal.BaseCloudServersRestClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code RetryOnRenew} handler
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "unit", testName = "RetryOnRenewExpectTest")
public class RetryOnRenewExpectTest extends BaseCloudServersRestClientExpectTest {

   @Test
   public void testShouldReauthenticateOn401() {
      String authToken = "d6245d35-22a0-47c0-9770-2c5097da25fc";
      String authToken2 = "12345678-9012-47c0-9770-2c5097da25fc";
      
      HttpRequest initialAuth = HttpRequest.builder().method("GET").endpoint(URI.create("https://auth/v1.0"))
               .headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-User", "identity")
               .put("X-Auth-Key", "credential")
               .put("Accept", "*/*").build()).build();
      
      
      HttpResponse responseWithUrls = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content")
               .headers(ImmutableMultimap.<String,String>builder()
               .put("Server", "Apache/2.2.3 (Red Hat)")
               .put("vary", "X-Auth-Token,X-Auth-Key,X-Storage-User,X-Storage-Pass")
               .put("X-Storage-Url", "https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Cache-Control", "s-maxage=86399")
               .put("Content-Type", "text/xml")
               .put("Date", "Tue, 10 Jan 2012 22:08:47 GMT")
               .put("X-Auth-Token", authToken)
               .put("X-Server-Management-Url","https://servers.api.rackspacecloud.com/v1.0/413274")
               .put("X-Storage-Token", authToken)
               .put("Connection", "Keep-Alive")
               .put("X-CDN-Management-Url", "https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Content-Length", "0")
               .build()).build();
      
      HttpResponse responseWithUrls2 = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content")
               .headers(ImmutableMultimap.<String,String>builder()
               .put("Server", "Apache/2.2.3 (Red Hat)")
               .put("vary", "X-Auth-Token,X-Auth-Key,X-Storage-User,X-Storage-Pass")
               .put("X-Storage-Url", "https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Cache-Control", "s-maxage=86399")
               .put("Content-Type", "text/xml")
               .put("Date", "Tue, 10 Jan 2012 22:08:47 GMT")
               .put("X-Auth-Token", authToken2)
               .put("X-Server-Management-Url","https://servers.api.rackspacecloud.com/v1.0/413274")
               .put("X-Storage-Token", authToken2)
               .put("Connection", "Keep-Alive")
               .put("X-CDN-Management-Url", "https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Content-Length", "0")
               .build()).build();
                     
      HttpRequest deleteImage = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://servers.api.rackspacecloud.com/v1.0/413274/images/11?now=1257695648897")).headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-Token", authToken).build()).build();

      HttpResponse pleaseRenew = HttpResponse.builder().statusCode(401)
               .message("HTTP/1.1 401 Unauthorized")
               .payload(Payloads.newStringPayload("[{\"unauthorized\":{\"message\":\"Invalid authentication token.  Please renew.\",\"code\":401}}]"))
               .build();

      HttpRequest deleteImage2 = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://servers.api.rackspacecloud.com/v1.0/413274/images/11?now=1257695648897")).headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-Token", authToken2).build()).build();

      HttpResponse imageDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudServersClient clientWhenImageExists = orderedRequestsSendResponses(initialAuth, responseWithUrls,
                deleteImage, pleaseRenew, initialAuth, responseWithUrls2, deleteImage2, imageDeleted);
      
      assert clientWhenImageExists.deleteImage(11);
   }

   @Test(expectedExceptions=AuthorizationException.class)
   public void testDoesNotReauthenticateOnFatal401() {
      String authToken = "d6245d35-22a0-47c0-9770-2c5097da25fc";
      
      HttpRequest initialAuth = HttpRequest.builder().method("GET").endpoint(URI.create("https://auth/v1.0"))
               .headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-User", "identity")
               .put("X-Auth-Key", "credential")
               .put("Accept", "*/*").build()).build();
      
      
      HttpResponse responseWithUrls = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content")
               .headers(ImmutableMultimap.<String,String>builder()
               .put("Server", "Apache/2.2.3 (Red Hat)")
               .put("vary", "X-Auth-Token,X-Auth-Key,X-Storage-User,X-Storage-Pass")
               .put("X-Storage-Url", "https://storage101.dfw1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Cache-Control", "s-maxage=86399")
               .put("Content-Type", "text/xml")
               .put("Date", "Tue, 10 Jan 2012 22:08:47 GMT")
               .put("X-Auth-Token", authToken)
               .put("X-Server-Management-Url","https://servers.api.rackspacecloud.com/v1.0/413274")
               .put("X-Storage-Token", authToken)
               .put("Connection", "Keep-Alive")
               .put("X-CDN-Management-Url", "https://cdn1.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22")
               .put("Content-Length", "0")
               .build()).build();
      
      HttpRequest deleteImage = HttpRequest.builder().method("DELETE").endpoint(
               URI.create("https://servers.api.rackspacecloud.com/v1.0/413274/images/11?now=1257695648897")).headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-Token", authToken).build()).build();

      HttpResponse unauthResponse = HttpResponse.builder().statusCode(401)
               .message("HTTP/1.1 401 Unauthorized")
               .payload(Payloads.newStringPayload("[{\"unauthorized\":{\"message\":\"Fatal unauthorized.\",\"code\":401}}]"))
               .build();

      CloudServersClient client = orderedRequestsSendResponses(initialAuth, responseWithUrls,
                deleteImage, unauthResponse);
      
      client.deleteImage(11);
   }

   // FIXME stack trace shows the AuthorizationException, but it's buried inside a guice TestException
   @Test(expectedExceptions=AuthorizationException.class)
   public void testDoesNotReauthenticateOnAuthentication401() {
      HttpRequest initialAuth = HttpRequest.builder().method("GET").endpoint(URI.create("https://auth/v1.0"))
               .headers(
               ImmutableMultimap.<String, String> builder()
               .put("X-Auth-User", "identity")
               .put("X-Auth-Key", "credential")
               .put("Accept", "*/*").build()).build();
      
      
      HttpResponse unauthResponse = HttpResponse.builder().statusCode(401)
               .message("HTTP/1.1 401 Unauthorized")
               .payload(Payloads.newStringPayload("[{\"unauthorized\":{\"message\":\"A different message implying fatal.\",\"code\":401}}]"))
               .build();

      CloudServersClient client = requestSendsResponse(initialAuth, unauthResponse);
                    
      client.deleteImage(11);
   }
}
