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
package org.jclouds.ssh;

import static org.jclouds.ssh.SshKeys.fingerprint;
import static org.jclouds.ssh.SshKeys.generate;
import static org.jclouds.ssh.SshKeys.privateKeyHasFingerprint;
import static org.jclouds.ssh.SshKeys.privateKeyHasSha1;
import static org.jclouds.ssh.SshKeys.privateKeyMatchesPublicKey;
import static org.jclouds.ssh.SshKeys.publicKeySpecFromOpenSSH;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import org.jclouds.crypto.Pems;
import org.jclouds.io.Payloads;
import org.jclouds.ssh.SshKeys;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "SshKeysTest")
public class SshKeysTest {

   String expectedFingerprint = "2b:a9:62:95:5b:8b:1d:61:e0:92:f7:03:10:e9:db:d9";
   String expectedSha1 = "c8:01:34:c0:3c:8c:91:ac:e1:da:cf:72:15:d7:f2:e5:99:5b:28:d4";

   @Test
   public void testCanReadRsaAndCompareFingerprintOnPublicRSAKey() throws IOException {
      String pubKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test.pub"));
      RSAPublicKeySpec key = SshKeys.publicKeySpecFromOpenSSH(pubKey);
      String fingerPrint = fingerprint(key.getPublicExponent(), key.getModulus());
      assertEquals(fingerPrint, expectedFingerprint);
   }

   @Test
   public void testCanReadRsaAndCompareFingerprintOnPrivateRSAKey() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      RSAPrivateCrtKeySpec key = (RSAPrivateCrtKeySpec) Pems.privateKeySpec(privKey);
      String fingerPrint = fingerprint(key.getPublicExponent(), key.getModulus());
      assertEquals(fingerPrint, expectedFingerprint);
   }

   @Test
   public void testPrivateKeyMatchesFingerprintTyped() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      RSAPrivateCrtKeySpec privateKey = (RSAPrivateCrtKeySpec) Pems.privateKeySpec(privKey);
      assert privateKeyHasFingerprint(privateKey, expectedFingerprint);
   }

   @Test
   public void testPrivateKeyMatchesFingerprintString() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      assert privateKeyHasFingerprint(privKey, expectedFingerprint);
   }

   @Test
   public void testPrivateKeyMatchesSha1Typed() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      RSAPrivateCrtKeySpec privateKey = (RSAPrivateCrtKeySpec) Pems.privateKeySpec(privKey);
      assert privateKeyHasSha1(privateKey, expectedSha1);
   }

   @Test
   public void testPrivateKeyMatchesSha1String() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      assert privateKeyHasSha1(privKey, expectedSha1);
   }

   @Test
   public void testPrivateKeyMatchesPublicKeyTyped() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      RSAPrivateCrtKeySpec privateKey = (RSAPrivateCrtKeySpec) Pems.privateKeySpec(privKey);
      String pubKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test.pub"));
      RSAPublicKeySpec publicKey = publicKeySpecFromOpenSSH(pubKey);
      assert privateKeyMatchesPublicKey(privateKey, publicKey);
   }

   @Test
   public void testPrivateKeyMatchesPublicKeyString() throws IOException {
      String privKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test"));
      String pubKey = Strings2.toStringAndClose(getClass().getResourceAsStream("/test.pub"));
      assert privateKeyMatchesPublicKey(privKey, pubKey);
   }

   @Test
   public void testCanGenerate() {
      Map<String, String> map = generate();
      assert map.get("public").startsWith("ssh-rsa ") : map;
      assert map.get("private").startsWith("-----BEGIN RSA PRIVATE KEY-----") : map;
      assert privateKeyMatchesPublicKey(map.get("private"), map.get("public")) : map;

   }

   @Test
   public void testEncodeAsOpenSSH() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String encoded = SshKeys.encodeAsOpenSSH((RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
               SshKeys.publicKeySpecFromOpenSSH(Payloads.newPayload(getClass().getResourceAsStream("/test.pub")))));
      assertEquals(encoded, Strings2.toStringAndClose(getClass().getResourceAsStream("/test.pub")).trim());
   }

}
