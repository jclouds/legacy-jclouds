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
package org.jclouds.oauth.v2;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * The constants for OAuth \
 *
 * @author David Alves
 */
public class OAuthConstants {

   /**
    * Selected algorithm when a signature or mac isn't required.
    */
   public static final String NO_ALGORITHM = "none";

   /**
    * Static mapping between the oauth algorithm name and the Crypto provider signature algorithm name.
    *
    * @see <a href="http://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-06#section-3.1">doc</a>
    * @see org.jclouds.oauth.v2.json.JWTTokenRequestFormat
    */
   public static final Map<String, String> OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES = ImmutableMap
           .<String, String>builder()
           .put("RS256", "SHA256withRSA")
           .put("RS384", "SHA384withRSA")
           .put("RS512", "SHA512withRSA")
           .put("HS256", "HmacSHA256")
           .put("HS384", "HmacSHA384")
           .put("HS512", "HmacSHA512")
           .put("ES256", "SHA256withECDSA")
           .put("ES384", "SHA384withECDSA")
           .put("ES512", "SHA512withECDSA")
           .put(NO_ALGORITHM, NO_ALGORITHM).build();

   /**
    * Static mapping between the oauth algorithm name and the Crypto provider KeyFactory algorithm name.
    *
    * @see <a href="http://tools.ietf.org/html/draft-ietf-jose-json-web-algorithms-06#section-3.1">doc</a>
    */
   public static final Map<String, String> OAUTH_ALGORITHM_NAMES_TO_KEYFACTORY_ALGORITHM_NAMES = ImmutableMap
           .<String, String>builder()
           .put("RS256", "RSA")
           .put("RS384", "RSA")
           .put("RS512", "RSA")
           .put("HS256", "DiffieHellman")
           .put("HS384", "DiffieHellman")
           .put("HS512", "DiffieHellman")
           .put("ES256", "EC")
           .put("ES384", "EC")
           .put("ES512", "EC")
           .put(NO_ALGORITHM, NO_ALGORITHM).build();

   /**
    * The (optional) set of additional claims to use, provided in Map<String,String> form
    */
   public static final String ADDITIONAL_CLAIMS = "jclouds.oauth.additional-claims";
}
