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
package org.jclouds.crypto;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import org.jclouds.encryption.internal.JCECrypto;

import com.google.inject.ImplementedBy;

/**
 * Allows you to access cryptographic objects and factories without adding a provider to the JCE runtime.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(JCECrypto.class)
public interface Crypto {
   KeyPairGenerator rsaKeyPairGenerator();

   KeyFactory rsaKeyFactory();

   CertificateFactory certFactory();

   Mac hmac(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException;

   Mac hmacSHA256(byte[] key) throws InvalidKeyException;

   Mac hmacSHA1(byte[] key) throws InvalidKeyException;

   Cipher cipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException;

}
