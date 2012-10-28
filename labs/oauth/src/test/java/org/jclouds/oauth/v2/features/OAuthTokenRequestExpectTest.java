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
package org.jclouds.oauth.v2.features;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.oauth.v2.OAuthClient;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.internal.BaseOAuthApiExpectTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * Tests that a token requess is well formed.
 *
 * @author David Alves
 */
@Test(groups = "unit", testName = "OAuthTokenRequestExpectTest")
public class OAuthTokenRequestExpectTest extends BaseOAuthApiExpectTest {

   private static final Token TOKEN = new Token("1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M", "Bearer", 3600);

   private static final ClaimSet CLAIM_SET = new ClaimSet.Builder().addClaim("iss",
           "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer" +
                   ".gserviceaccount.com")
           .addClaim("scope", "https://www.googleapis.com/auth/prediction")
           .addClaim("aud", "https://accounts.google.com/o/oauth2/token")
           .expirationTime(1328573381)
           .emissionTime(1328569781).build();

   private static final Header HEADER = new Header.Builder().signer("RS256").type("JWT").build();

   private static final String URL_ENCODED_TOKEN_REQUEST =
           "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
                   // Base64 Encoded Header
                   "assertion=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
                   // Base64 Encoded Claims
                   "eyJpc3MiOiI3NjEzMjY3OTgwNjktcjVtbGpsbG4xcmQ0bHJiaGc3NWVmZ2lncDM2bTc4a" +
                   "jVAZGV2ZWxvcGVyLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzY29wZSI6Imh0dHBzOi8vd3" +
                   "d3Lmdvb2dsZWFwaXMuY29tL2F1dGgvcHJlZGljdGlvbiIsImF1ZCI6Imh0dHBzOi8vYWN" +
                   "jb3VudHMuZ29vZ2xlLmNvbS9vL29hdXRoMi90b2tlbiIsImV4cCI6MTMyODU3MzM4MSwia" +
                   "WF0IjoxMzI4NTY5NzgxfQ." +
                   // Base64 encoded {header}.{claims} signature (using SHA256)
                   "W2Lesr_98AzVYiMbzxFqmwcOjpIWlwqkC6pNn1fXND9oSDNNnFhy-AAR6DKH-x9ZmxbY80" +
                   "R5fH-OCeWumXlVgceKN8Z2SmgQsu8ElTpypQA54j_5j8vUImJ5hsOUYPeyF1U2BUzZ3L5g" +
                   "03PXBA0YWwRU9E1ChH28dQBYuGiUmYw";

   private static final HttpRequest TOKEN_REQUEST = HttpRequest.builder()
           .method("POST")
           .endpoint(URI.create("http://localhost:5000/o/oauth2/token"))
           .addHeader("Accept", MediaType.APPLICATION_JSON)
           .payload(payloadFromStringWithContentType(URL_ENCODED_TOKEN_REQUEST, "application/x-www-form-urlencoded"))
           .build();

   private static final HttpResponse TOKEN_RESPONSE = HttpResponse.builder().statusCode(200).payload(
           payloadFromString("{\n" +
                   "  \"access_token\" : \"1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M\",\n" +
                   "  \"token_type\" : \"Bearer\",\n" +
                   "  \"expires_in\" : 3600\n" +
                   "}")).build();

   @Override
   protected Properties setupProperties() {
      return OAuthTestUtils.defaultProperties(super.setupProperties());
   }

   public void testGenerateJWTRequest() {
      OAuthClient client = requestSendsResponse(TOKEN_REQUEST, TOKEN_RESPONSE);
      assertEquals(client.authenticate(new TokenRequest(HEADER, CLAIM_SET)), TOKEN);
   }

}
