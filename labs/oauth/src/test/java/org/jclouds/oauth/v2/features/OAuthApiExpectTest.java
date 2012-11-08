/*
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
import org.jclouds.oauth.v2.OAuthApi;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.internal.BaseOAuthApiExpectTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.jclouds.crypto.CryptoStreams.base64Url;
import static org.testng.Assert.assertEquals;

/**
 * Tests that a token requess is well formed.
 *
 * @author David Alves
 */
@Test(groups = "unit")
public class OAuthApiExpectTest extends BaseOAuthApiExpectTest {

   private static final String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";

   private static final String claims = "{\"iss\":\"761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer" +
           ".gserviceaccount.com\"," +
           "\"scope\":\"https://www.googleapis.com/auth/prediction\",\"aud\":\"https://accounts.google" +
           ".com/o/oauth2/token\",\"exp\":1328573381,\"iat\":1328569781}";

   private static final Token TOKEN = new Token.Builder().accessToken
           ("1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M").tokenType("Bearer").expiresIn(3600).build();

   private static final ClaimSet CLAIM_SET = new ClaimSet.Builder().addClaim("iss",
           "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer" +
                   ".gserviceaccount.com")
           .addClaim("scope", "https://www.googleapis.com/auth/prediction")
           .addClaim("aud", "https://accounts.google.com/o/oauth2/token")
           .expirationTime(1328573381)
           .emissionTime(1328569781).build();

   private static final Header HEADER = new Header.Builder().signerAlgorithm("RS256").type("JWT").build();

   private static final String URL_ENCODED_TOKEN_REQUEST =
           "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&" +
                   // Base64 Encoded Header
                   "assertion=" + base64Url(header.getBytes(Charset.forName("UTF-8"))) + "." +
                   // Base64 Encoded Claims
                   base64Url(claims.getBytes(Charset.forName("UTF-8"))) + "." +
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
      OAuthApi api = requestSendsResponse(TOKEN_REQUEST, TOKEN_RESPONSE);
      assertEquals(api.authenticate(new TokenRequest(HEADER, CLAIM_SET)), TOKEN);
   }
}
