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

package org.jclouds.oauth.v2.json;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jclouds.ContextBuilder;
import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.v2.OAuthApiMetadata;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.domain.TokenRequestFormat;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.jclouds.oauth.v2.internal.Base64UrlSafeTest.STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class JWTTokenRequestFormatTest {

   public void testPayloadIsUrlSafe() throws IOException {


      TokenRequestFormat tokenRequestFormat = ContextBuilder.newBuilder(new OAuthApiMetadata()).overrides
              (OAuthTestUtils.defaultProperties(null)).build().utils()
              .injector().getInstance(TokenRequestFormat.class);
      Header header = new Header.Builder().signerAlgorithm("a").type("b").build();
      ClaimSet claimSet = new ClaimSet.Builder().addClaim("ist", STRING_THAT_GENERATES_URL_UNSAFE_BASE64_ENCODING)
              .build();
      TokenRequest tokenRequest = new TokenRequest.Builder().claimSet(claimSet).header(header).build();
      HttpRequest request = tokenRequestFormat.formatRequest(HttpRequest.builder().method("GET").endpoint
              ("http://localhost").build(), tokenRequest);

      assertNotNull(request.getPayload());

      String payload = Strings2.toStringAndClose(request.getPayload().getInput());

      // make sure the paylod is in the format {header}.{claims}.{signature}
      Iterable<String> parts = Splitter.on(".").split(payload);

      assertSame(Iterables.size(parts), 3);

      assertTrue(!payload.contains("+"));
      assertTrue(!payload.contains("/"));
   }
}
