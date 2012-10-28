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
