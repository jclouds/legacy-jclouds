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
package org.jclouds.oauth.v2.json;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.io.BaseEncoding.base64Url;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payloads;
import org.jclouds.json.Json;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.domain.TokenRequestFormat;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Formats a token request into JWT format namely:
 * - transforms the token request to json
 * - creates the base64 header.claimset portions of the payload.
 * - uses the provided signer function to create a signature
 * - creates the full url encoded payload as described in:
 * https://developers.google.com/accounts/docs/OAuth2ServiceAccount
 * <p/>
 *
 * @author David Alves
 */
@Singleton
public class JWTTokenRequestFormat implements TokenRequestFormat {

   private static final String ASSERTION_FORM_PARAM = "assertion";
   private static final String GRANT_TYPE_FORM_PARAM = "grant_type";
   private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";

   private final Function<byte[], byte[]> signer;
   private final Json json;

   @Inject
   public JWTTokenRequestFormat(Function<byte[], byte[]> signer, Json json) {
      this.signer = signer;
      this.json = json;
   }

   @Override
   public <R extends HttpRequest> R formatRequest(R httpRequest, TokenRequest tokenRequest) {
      HttpRequest.Builder builder = httpRequest.toBuilder();

      String encodedHeader = json.toJson(tokenRequest.getHeader());
      String encodedClaimSet = json.toJson(tokenRequest.getClaimSet());

      encodedHeader = base64Url().omitPadding().encode(encodedHeader.getBytes(UTF_8));
      encodedClaimSet = base64Url().omitPadding().encode(encodedClaimSet.getBytes(UTF_8));

      byte[] signature = signer.apply(on(".").join(encodedHeader, encodedClaimSet).getBytes(UTF_8));
      String encodedSignature = signature != null ?  base64Url().omitPadding().encode(signature) : "";

      // the final assertion in base 64 encoded {header}.{claimSet}.{signature} format
      String assertion = on(".").join(encodedHeader, encodedClaimSet, encodedSignature);

      builder.payload(Payloads.newUrlEncodedFormPayload(ImmutableMultimap.of(GRANT_TYPE_FORM_PARAM,
              GRANT_TYPE_JWT_BEARER, ASSERTION_FORM_PARAM, assertion)));

      return (R) builder.build();
   }

   @Override
   public String getTypeName() {
      return "JWT";
   }

   @Override
   public Set<String> requiredClaims() {
      // exp and ist (expiration and emission times) are assumed mandatory already
      return ImmutableSet.of("iss", "scope", "aud");
   }
}
