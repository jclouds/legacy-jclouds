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

/**
 * The constants for OAuth \
 *
 * @author David Alves
 */
public class OAuthConstants {

   /**
    * The selected signature algorithm to use to sign the requests.
    */
   public static final String SIGNATURE_ALGORITHM = "jclouds.oauth.signature-algo";

   /**
    * The format of the certificate key file that will be used to sign the token request.
    * Supported formats are PKCS12 and PKCS8, default is PKCS12
    */
   public static final String SIGNATURE_KEY_FORMAT = "jclouds.oauth.signature-key-format";

   /**
    * The name of the key (if the ceritificate is in a PKCS12 Keystore)
    */
   public static final String PKCS_CERTIFICATE_KEY_NAME = "jclouds.oauth.pkcs12.key-name";

   /**
    * The the password for the key (if the ceritificate is in a PKCS12 Keystore)
    */
   public static final String PKCS_CERITIFICATE_KEY_PASSWORD = "jclouds.oauth.pkcs12.key-password";

   /**
    * The permissions being requested.
    */
   public static final String TOKEN_SCOPE = "jclouds.oauth.scope";

   /**
    * The assertion target
    */
   public static final String TOKEN_ASSERTION_DESCRIPTION = "jclouds.oauth.assertion-description";

   /**
    * The (optional) set of additional claims to use, provided in Map<String,String> form
    */
   public static final String ADDITIONAL_CLAIMS = "jclouds.oauth.additional-claims";


}
