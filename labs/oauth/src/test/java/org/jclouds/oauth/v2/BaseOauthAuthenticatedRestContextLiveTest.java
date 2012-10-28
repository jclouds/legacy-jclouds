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

package org.jclouds.oauth.v2;

import com.google.common.base.Ticker;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredentialFromPemFile;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.testng.Assert.assertNotNull;

/**
 * A base test of oauth authenticated rest providers. Providers must set the following properties:
 * <p/>
 * - oauth.endpoint
 * - oauth.audience
 * - oauth.signature-or-mac-algorithm
 * <p/>
 * - oauth.scopes is provided by the subclass
 * <p/>
 * This test asserts that a provider can authenticate with oauth for a given scope, or more simply
 * that authentication/authorization is working.
 *
 * @author David Alves
 */

@Test(groups = "live")
public abstract class BaseOauthAuthenticatedRestContextLiveTest<S, A> extends BaseContextLiveTest<RestContext<S, A>> {


   private OAuthApi oauthApi;

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setCredentialFromPemFile(props, provider + ".credential");
      return props;
   }

   public void testAuthenticate() {

      try {
         oauthApi = context.utils().injector().getInstance(OAuthApi.class);
      } catch (Exception e) {
         throw new IllegalStateException("Provider has no OAuthApi bound. Was the OAuthAuthenticationModule added?");
      }

      // obtain the necessary properties from the context
      String signatureAlgorithm = getContextPropertyOrFail(SIGNATURE_OR_MAC_ALGORITHM);
      checkState(OAuthConstants.OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES.containsKey(signatureAlgorithm)
              , String.format("Algorithm not supported: " + signatureAlgorithm));

      String audience = getContextPropertyOrFail(AUDIENCE);

      // obtain the scopes from the subclass
      String scopes = getScopes();

      Header header = Header.builder().signerAlgorithm(signatureAlgorithm).type("JWT").build();

      long now = TimeUnit.SECONDS.convert(Ticker.systemTicker().read(), TimeUnit.NANOSECONDS);

      ClaimSet claimSet = ClaimSet.builder().addClaim("aud", audience).addClaim("scope", scopes).addClaim("iss",
              identity).emissionTime(now).expirationTime(now + 3600).build();

      TokenRequest tokenRequest = TokenRequest.builder().header(header).claimSet(claimSet).build();

      Token token = oauthApi.authenticate(tokenRequest);

      assertNotNull(token);
   }

   public abstract String getScopes();

   private String getContextPropertyOrFail(String property) {
      try {
         return context.utils().injector().getInstance(Key.get(String.class, Names.named(property)));
      } catch (Exception e) {
         throw new IllegalStateException("Provider " + provider + " must have a named property: " + property + " for " +
                 "oauth to work");
      }
   }

}
