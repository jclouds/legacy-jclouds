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
package org.jclouds.ec2.compute.functions;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.inject.Inject;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.PasswordDataAndPrivateKey;
import org.jclouds.encryption.internal.Base64;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;

/**
 * Given an encrypted Windows Administrator password and the decryption key, return a LoginCredentials instance.
 *
 * @author Richard Downer
 */
@Singleton
public class WindowsLoginCredentialsFromEncryptedData implements Function<PasswordDataAndPrivateKey, LoginCredentials> {

   private final Crypto crypto;

   @Inject
   public WindowsLoginCredentialsFromEncryptedData(Crypto crypto) {
      this.crypto = crypto;
   }

   @Override
   public LoginCredentials apply(@Nullable PasswordDataAndPrivateKey dataAndKey) {
      if(dataAndKey == null)
         return null;

      try {
         KeySpec keySpec = Pems.privateKeySpec(dataAndKey.getPrivateKey());
         KeyFactory kf = crypto.rsaKeyFactory();
         PrivateKey privKey = kf.generatePrivate(keySpec);

         Cipher cipher = crypto.cipher("RSA/NONE/PKCS1Padding");
         cipher.init(Cipher.DECRYPT_MODE, privKey);
         byte[] cipherText = Base64.decode(dataAndKey.getPasswordData().getPasswordData());
         byte[] plainText = cipher.doFinal(cipherText);
         String password = new String(plainText, Charset.forName("ASCII"));

         return LoginCredentials.builder()
            .user("Administrator")
            .password(password)
            .noPrivateKey()
            .build();
      } catch(Exception e) {
         throw Throwables.propagate(e);
      }
   }
}
