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
package org.jclouds.encryption.internal;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.annotation.Nullable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.crypto.Crypto;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class JCECrypto implements Crypto {

   private final KeyPairGenerator rsaKeyPairGenerator;
   private final KeyFactory rsaKeyFactory;
   private final CertificateFactory certFactory;
   private final Provider provider;

   @Inject
   public JCECrypto() throws NoSuchAlgorithmException, CertificateException {
      this(null);
   }

   public JCECrypto(@Nullable Provider provider) throws NoSuchAlgorithmException, CertificateException {
      this.rsaKeyPairGenerator = provider == null ? KeyPairGenerator.getInstance("RSA") : KeyPairGenerator.getInstance(
            "RSA", provider);
      this.rsaKeyFactory = provider == null ? KeyFactory.getInstance("RSA") : KeyFactory.getInstance("RSA", provider);
      this.certFactory = provider == null ? CertificateFactory.getInstance("X.509") : CertificateFactory.getInstance(
            "X.509", provider);
      this.provider = provider;
   }

   @Override
   public Mac hmac(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
      Mac mac = provider == null ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
      SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
      mac.init(signingKey);
      return mac;

   }

   @Override
   public MessageDigest digest(String algorithm) throws NoSuchAlgorithmException {
      return provider == null ? MessageDigest.getInstance(algorithm) : MessageDigest.getInstance(algorithm, provider);
   }

   public final static String MD5 = "MD5";
   public final static String SHA1 = "SHA1";
   public final static String SHA256 = "SHA-256";
   public final static String SHA512 = "SHA-512";

   @Override
   public MessageDigest md5() {
      try {
         return digest(MD5);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("MD5 must be supported", e);
      }
   }

   @Override
   public MessageDigest sha1() {
      try {
         return digest(SHA1);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("MD5 must be supported", e);
      }
   }

   @Override
   public MessageDigest sha256() {
      try {
         return digest(SHA256);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException(SHA256 + " must be supported", e);
      }
   }

   @Override
   public MessageDigest sha512() {
      try {
         return digest(SHA512);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException(SHA512 + " must be supported", e);
      }
   }

   public final static String HmacSHA256 = "HmacSHA256";
   public final static String HmacSHA1 = "HmacSHA1";

   @Override
   public Mac hmacSHA1(byte[] key) throws InvalidKeyException {
      try {
         return hmac(HmacSHA1, key);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("HmacSHA1 must be supported", e);
      }
   }

   @Override
   public Mac hmacSHA256(byte[] key) throws InvalidKeyException {
      try {
         return hmac(HmacSHA256, key);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("HmacSHA256 must be supported", e);
      }
   }

   @Override
   public CertificateFactory certFactory() {
      return certFactory;
   }

   @Override
   public KeyFactory rsaKeyFactory() {
      return rsaKeyFactory;
   }

   @Override
   public KeyPairGenerator rsaKeyPairGenerator() {
      return rsaKeyPairGenerator;
   }
}
