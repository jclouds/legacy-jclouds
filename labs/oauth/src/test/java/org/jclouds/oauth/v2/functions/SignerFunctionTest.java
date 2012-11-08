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

import org.jclouds.crypto.CryptoStreams;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static com.google.common.base.Suppliers.ofInstance;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;


/**
 * Tests the SignOrProduceMacForToken
 *
 * @author David Alves
 */
@Test(groups = "unit")
public class SignerFunctionTest {

   private static final String PAYLOAD = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.\n" +
           "eyJpc3MiOiI3NjEzMjY3OTgwNjktcjVtbGpsbG4xcmQ0bHJiaGc3NWVmZ2lncDM2bTc4ajVAZ" +
           "GV2ZWxvcGVyLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzY29wZSI6Imh0dHBzOi8vd3d3Lmdvb2ds" +
           "ZWFwaXMuY29tL2F1dGgvcHJlZGljdGlvbiIsImF1ZCI6Imh0dHBzOi8vYWNjb3VudHMuZ29vZ2x" +
           "lLmNvbS9vL29hdXRoMi90b2tlbiIsImV4cCI6MTMyODU1NDM4NSwiaWF0IjoxMzI4NTUwNzg1fQ";

   private static final String SHA256withRSA_PAYLOAD_SIGNATURE_RESULT =
           "bmQrCv4gjkLWDK1JNJni74_kPiSDUMF_FImgqKJMUIgkDX1m2Sg3bH1yjF-cjBN7CvfAscnageo" +
                   "GtL2TGbwoTjJgUO5Yy0esavUUF-mBQHQtSw-2nL-9TNyM4SNi6fHPbgr83GGKOgA86r" +
                   "I9-nj3oUGd1fQty2k4Lsd-Zdkz6es";


   public void testSignPayload() throws InvalidKeyException, IOException, NoSuchAlgorithmException,
           CertificateException, InvalidKeySpecException {
      SignOrProduceMacForToken signer = new SignOrProduceMacForToken("RS256",
              ofInstance(OAuthCredentialsFromPKTest
                      .loadOAuthCredentials()));
      signer.loadSignatureOrMacOrNone();
      byte[] payloadSignature = signer.apply(PAYLOAD.getBytes("UTF-8"));
      assertNotNull(payloadSignature);
      assertEquals(CryptoStreams.base64Url(payloadSignature), SHA256withRSA_PAYLOAD_SIGNATURE_RESULT);
   }
}
