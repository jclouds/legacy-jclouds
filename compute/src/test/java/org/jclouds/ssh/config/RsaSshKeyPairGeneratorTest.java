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
package org.jclouds.ssh.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.crypto.PemsTest.PRIVATE_KEY;
import static org.jclouds.crypto.PemsTest.PUBLIC_KEY;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.io.Payloads;
import org.jclouds.ssh.SshKeys;
import org.jclouds.ssh.internal.RsaSshKeyPairGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "RsaSshKeyPairGeneratorTest")
public class RsaSshKeyPairGeneratorTest {
   private static final String lineSeparator = System.getProperty("line.separator");

   private KeyPair keyPair;
   private String openSshKey;

   @BeforeClass
   public void setup() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
      KeyFactory keyfactory = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = keyfactory.generatePrivate(Pems.privateKeySpec(Payloads.newStringPayload(PRIVATE_KEY)));

      PublicKey publicKey = keyfactory.generatePublic(Pems.publicKeySpec(Payloads.newStringPayload(PUBLIC_KEY)));

      keyPair = new KeyPair(publicKey, privateKey);
      openSshKey = SshKeys.encodeAsOpenSSH(RSAPublicKey.class.cast(publicKey));
   }

   @Test
   public void testApply() {
      final Crypto crypto = createMock(Crypto.class);
      KeyPairGenerator rsaKeyPairGenerator = createMock(KeyPairGenerator.class);
      final SecureRandom secureRandom = createMock(SecureRandom.class);

      expect(crypto.rsaKeyPairGenerator()).andReturn(rsaKeyPairGenerator);
      rsaKeyPairGenerator.initialize(2048, secureRandom);
      expect(rsaKeyPairGenerator.genKeyPair()).andReturn(keyPair);

      replay(crypto, rsaKeyPairGenerator, secureRandom);

      RsaSshKeyPairGenerator supplier = Guice.createInjector(new AbstractModule(){
         protected void configure() {
            bind(Crypto.class).toInstance(crypto);
            bind(SecureRandom.class).toInstance(secureRandom);
         }
      }).getInstance(RsaSshKeyPairGenerator.class);

      assertEquals(supplier.get(),
               ImmutableMap.of("public", openSshKey, "private", PRIVATE_KEY.replaceAll("\n", lineSeparator)));

      verify(crypto, rsaKeyPairGenerator, secureRandom);
   }

}
