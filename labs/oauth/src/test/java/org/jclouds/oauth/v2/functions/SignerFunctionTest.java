package org.jclouds.oauth.v2.functions;

import org.apache.commons.codec.binary.Base64;
import org.jclouds.ContextBuilder;
import org.jclouds.crypto.Crypto;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.oauth.v2.config.OAuthBaseModule;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;

import static com.google.common.base.Suppliers.ofInstance;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


/**
 * Tests the SignerFunction along with the loading of the private key and signature.
 */
@Test(groups = "unit", testName = "SignerFunctionTest")
public class SignerFunctionTest {

   private static final String PAYLOAD = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.\n" +
           "eyJpc3MiOiI3NjEzMjY3OTgwNjktcjVtbGpsbG4xcmQ0bHJiaGc3NWVmZ2lncDM2bTc4ajVAZ" +
           "GV2ZWxvcGVyLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzY29wZSI6Imh0dHBzOi8vd3d3Lmdvb2ds" +
           "ZWFwaXMuY29tL2F1dGgvcHJlZGljdGlvbiIsImF1ZCI6Imh0dHBzOi8vYWNjb3VudHMuZ29vZ2x" +
           "lLmNvbS9vL29hdXRoMi90b2tlbiIsImV4cCI6MTMyODU1NDM4NSwiaWF0IjoxMzI4NTUwNzg1fQ";

   private static final String PAYLOAD_SIGNATURE_RESULT =
           "bmQrCv4gjkLWDK1JNJni74_kPiSDUMF_FImgqKJMUIgkDX1m2Sg3bH1yjF-cjBN7CvfAscnageo" +
                   "GtL2TGbwoTjJgUO5Yy0esavUUF-mBQHQtSw-2nL-9TNyM4SNi6fHPbgr83GGKOgA86r" +
                   "I9-nj3oUGd1fQty2k4Lsd-Zdkz6es";

   private Signature signature;
   private PrivateKey pk;


   public void testLoadRS256Signature() throws NoSuchAlgorithmException {
      Crypto crypto = ContextBuilder.newBuilder("oauth").overrides(OAuthTestUtils.defaultProperties(null)).
              buildInjector().getInstance(Crypto.class);
      signature = new OAuthBaseModule().provideSignature("RS256", crypto).get();
      assertNotNull(signature);
   }

   @Test(dependsOnMethods = {"testLoadRS256Signature"})
   public void testSignPayload() throws InvalidKeyException, UnsupportedEncodingException {
      SignerFunction signer = new SignerFunction(ofInstance(signature), ofInstance(OAuthCredentialsfromPKCS12FileTest
              .loadOAuthCredentials()));
      byte[] payloadSignature = signer.apply(PAYLOAD.getBytes("UTF-8"));
      assertNotNull(payloadSignature);
      assertEquals(Base64.encodeBase64URLSafeString(payloadSignature), PAYLOAD_SIGNATURE_RESULT);
   }
}
