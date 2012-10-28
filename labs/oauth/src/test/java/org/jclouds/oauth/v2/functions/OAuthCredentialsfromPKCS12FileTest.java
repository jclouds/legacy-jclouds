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
package org.jclouds.oauth.v2.functions;

import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Test loading the credentials by extracting a pk from a PKCS12 keystore.
 *

 */
@Test(groups = "unit", testName = "OAuthCredentialsfromPKCS12FileTest")
public class OAuthCredentialsfromPKCS12FileTest {

   public static OAuthCredentials loadOAuthCredentials() {
      OAuthCredentialsFromPKCS12File loader = new OAuthCredentialsFromPKCS12File("foo",
              "target/test-classes/test.p12", "privatekey",
              "notasecret");
      return loader.get();
   }


   public void testLoadPKCS12Certificate() throws IOException, NoSuchAlgorithmException, KeyStoreException,
           CertificateException, UnrecoverableKeyException {
      OAuthCredentials creds = loadOAuthCredentials();
      assertNotNull(creds);
      assertEquals(creds.identity, "foo");
      assertNotNull(creds.privateKey);
   }


}
