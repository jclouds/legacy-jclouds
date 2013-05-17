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
package org.jclouds.encryption.bouncycastle;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Singleton;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jclouds.encryption.internal.JCECrypto;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BouncyCastleCrypto extends JCECrypto {

   public BouncyCastleCrypto() throws NoSuchAlgorithmException, CertificateException {
      super(new BouncyCastleProvider());
   }

   
   /*******
    * Override the standard implementation to convert the "RSA" algorithm to the String that bouncycastle expects.
    * See org.jclouds.encryption.bouncycastle.BouncyCastlePasswordDecryptionTest for how this effects password decryption.
    * 
    */
   @Override
   public Cipher cipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
      return super.cipher("RSA".equals(algorithm) ? "RSA/NONE/PKCS1Padding" : algorithm);
   }
}
