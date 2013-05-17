/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import org.jclouds.cloudstack.domain.EncryptedPasswordAndPrivateKey;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

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
   
   private static final Pattern whitespace = Pattern.compile("\\s");
   
   @Override
   public LoginCredentials apply(@Nullable EncryptedPasswordAndPrivateKey dataAndKey) {
      if (dataAndKey == null)
         return null;
      try {
         KeySpec keySpec = Pems.privateKeySpec(dataAndKey.getPrivateKey());
         KeyFactory kf = crypto.rsaKeyFactory();
         PrivateKey privKey = kf.generatePrivate(keySpec);

         Cipher cipher = crypto.cipher("RSA");
         cipher.init(Cipher.DECRYPT_MODE, privKey);
         byte[] cipherText = base64().decode(whitespace.matcher(dataAndKey.getEncryptedPassword()).replaceAll(""));
         byte[] plainText = cipher.doFinal(cipherText);
         String password = new String(plainText, UTF_8);

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
