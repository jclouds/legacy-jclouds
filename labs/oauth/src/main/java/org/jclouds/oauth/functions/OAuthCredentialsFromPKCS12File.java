package org.jclouds.oauth.functions;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import org.jclouds.oauth.domain.OAuthCredentials;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.PrivateKey;

import static org.jclouds.oauth.OAuthConstants.PKCS_CERITIFICATE_KEY_PASSWORD;
import static org.jclouds.oauth.OAuthConstants.PKCS_CERTIFICATE_KEY_NAME;

@Singleton
public class OAuthCredentialsFromPKCS12File implements Supplier<OAuthCredentials> {

   private String keystorePath;
   private String keyName;
   private String keyPassword;
   private String identity;

   @Inject
   public OAuthCredentialsFromPKCS12File(@Identity String identity, @Credential String keystorePath,
                                         @Named(PKCS_CERTIFICATE_KEY_NAME)
                                         String keyName, @Named(PKCS_CERITIFICATE_KEY_PASSWORD) String keyPassword) {
      this.identity = identity;
      this.keystorePath = keystorePath;
      this.keyName = keyName;
      this.keyPassword = keyPassword;
   }


   @Override
   public OAuthCredentials get() {
      File file = new File(keystorePath);
      if (!file.exists()) {
         try {
            throw new FileNotFoundException("Private key file not found in: " + keystorePath);
         } catch (FileNotFoundException e) {
            Throwables.propagate(e);
         }
      }

      KeyStore keyStore = null;
      try {
         keyStore = KeyStore.getInstance("PKCS12");
         keyStore.load(new FileInputStream(file), null);
         PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, keyPassword.toCharArray());
         return new OAuthCredentials.Builder().identity(identity).credential
                 (keystorePath).privateKey(privateKey).build();
      } catch (Exception e) {
         throw new AuthorizationException("Cannot access private key.", e);
      }
   }
}
