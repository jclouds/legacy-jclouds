package org.jclouds.cloudstack.functions;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;

import org.jclouds.cloudstack.domain.EncryptedPasswordAndPrivateKey;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Given an encrypted Windows Administrator password and the decryption key, return a LoginCredentials instance.
 *
 * @author Richard Downer, Andrei Savu
 */
@Singleton
public class WindowsLoginCredentialsFromEncryptedData implements Function<EncryptedPasswordAndPrivateKey, LoginCredentials> {

   private final Crypto crypto;

   @Inject
   public WindowsLoginCredentialsFromEncryptedData(Crypto crypto) {
      this.crypto = crypto;
   }

   @Override
   public LoginCredentials apply(@Nullable EncryptedPasswordAndPrivateKey dataAndKey) {
      if (dataAndKey == null)
         return null;

      try {
         KeySpec keySpec = Pems.privateKeySpec(dataAndKey.getPrivateKey());
         KeyFactory kf = crypto.rsaKeyFactory();
         PrivateKey privKey = kf.generatePrivate(keySpec);

         Cipher cipher = crypto.cipher("RSA/NONE/PKCS1Padding");
         cipher.init(Cipher.DECRYPT_MODE, privKey);
         byte[] cipherText = Base64.decode(dataAndKey.getEncryptedPassword());
         byte[] plainText = cipher.doFinal(cipherText);
         String password = new String(plainText, Charsets.US_ASCII);

         return LoginCredentials.builder()
            .user("Administrator")
            .password(password)
            .noPrivateKey()
            .build();

      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }
}
