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
package org.jclouds.joyent.cloudapi.v6_5.compute.loaders;

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

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.crypto.SshKeys;
import org.jclouds.io.Payloads;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.compute.loaders.CreateUniqueKey;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CreateUniqueKeyTest")
public class CreateUniqueKeyTest {
   private Factory namingConvention;
   private KeyPair keyPair;
   private String openSshKey;

   @BeforeClass
   public void setup() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
      namingConvention = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("foo"));
         }
      }).getInstance(GroupNamingConvention.Factory.class);
      KeyFactory keyfactory = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = keyfactory.generatePrivate(Pems.privateKeySpec(Payloads.newStringPayload(PRIVATE_KEY)));

      PublicKey publicKey = keyfactory
            .generatePublic(Pems.publicKeySpec(Payloads.newStringPayload(PUBLIC_KEY)));

      keyPair = new KeyPair(publicKey, privateKey);
      openSshKey = SshKeys.encodeAsOpenSSH(RSAPublicKey.class.cast(publicKey));
   }

   @Test
   public void testApply() {
      JoyentCloudApi cloudApiApi = createMock(JoyentCloudApi.class);
      KeyApi keyApi = createMock(KeyApi.class);
      Crypto crypto = createMock(Crypto.class);
      KeyPairGenerator rsaKeyPairGenerator = createMock(KeyPairGenerator.class);
      SecureRandom secureRandom = createMock(SecureRandom.class);

      Key key = Key.builder().name("group-foo").key(openSshKey).build();

      expect(crypto.rsaKeyPairGenerator()).andReturn(rsaKeyPairGenerator);
      rsaKeyPairGenerator.initialize(2048, secureRandom);
      expect(rsaKeyPairGenerator.genKeyPair()).andReturn(keyPair);

      expect(cloudApiApi.getKeyApi()).andReturn(keyApi);

      expect(keyApi.create(key)).andReturn(key);

      replay(cloudApiApi, keyApi, crypto, rsaKeyPairGenerator, secureRandom);

      CreateUniqueKey parser = new CreateUniqueKey(cloudApiApi, namingConvention, crypto, Providers.of(secureRandom));

      assertEquals(parser.load(DatacenterAndName.fromDatacenterAndName("datacenter", "group")),
            KeyAndPrivateKey.fromKeyAndPrivateKey(key, PRIVATE_KEY));

      verify(cloudApiApi, keyApi, crypto, rsaKeyPairGenerator, secureRandom);
   }

}
