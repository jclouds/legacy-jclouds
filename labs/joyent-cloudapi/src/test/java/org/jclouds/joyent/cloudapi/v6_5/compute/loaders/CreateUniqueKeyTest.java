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
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.functions.GroupNamingConvention.Factory;
import org.jclouds.joyent.cloudapi.v6_5.JoyentCloudApi;
import org.jclouds.joyent.cloudapi.v6_5.compute.internal.KeyAndPrivateKey;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.joyent.cloudapi.v6_5.domain.datacenterscoped.DatacenterAndName;
import org.jclouds.joyent.cloudapi.v6_5.features.KeyApi;
import org.jclouds.ssh.SshKeyPairGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CreateUniqueKeyTest")
public class CreateUniqueKeyTest {
   private ImmutableMap<String, String> keyPair = ImmutableMap.of("public", "ssh-rsa AAAAB3NzaC...", "private",
            "-----BEGIN RSA PRIVATE KEY-----\n");

   private Factory namingConvention;

   @BeforeClass
   public void setup() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
      namingConvention = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("foo"));
         }
      }).getInstance(GroupNamingConvention.Factory.class);

   }

   @Test
   public void testApply() {
      JoyentCloudApi cloudApiApi = createMock(JoyentCloudApi.class);
      SshKeyPairGenerator sshKeyPairGenerator = new SshKeyPairGenerator() {

         @Override
         public Map<String, String> get() {
            return keyPair;
         }

      };
      KeyApi keyApi = createMock(KeyApi.class);
      Key key = Key.builder().name("group-foo").key(keyPair.get("public")).build();

      expect(cloudApiApi.getKeyApi()).andReturn(keyApi);

      expect(keyApi.create(key)).andReturn(key);

      replay(cloudApiApi, keyApi);

      CreateUniqueKey parser = new CreateUniqueKey(cloudApiApi, namingConvention, sshKeyPairGenerator);

      assertEquals(parser.load(DatacenterAndName.fromDatacenterAndName("datacenter", "group")),
               KeyAndPrivateKey.fromKeyAndPrivateKey(key, keyPair.get("private")));

      verify(cloudApiApi, keyApi);
   }

}
