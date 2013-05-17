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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Map;
import java.util.Set;

import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SSHKeyPairClient}
 *
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "SSHKeyPairClientLiveTest")
public class SSHKeyPairClientLiveTest extends BaseCloudStackClientLiveTest {

   protected String prefix = System.getProperty("user.name");
   private String keyPairName = prefix + "-jclouds-keypair";
   private SshKeyPair sshKeyPair;

   @BeforeMethod
   @AfterMethod
   public void removeExistingKey() {
      client.getSSHKeyPairClient().deleteSSHKeyPair(keyPairName);
   }

   @Test
   public void testListSSHKeyPairs() {
      final Set<SshKeyPair> sshKeyPairs = client.getSSHKeyPairClient().listSSHKeyPairs();
      for (SshKeyPair sshKeyPair : sshKeyPairs) {
         checkSSHKeyPair(sshKeyPair);
      }
   }

   @Test
   public void testCreateDeleteSSHKeyPair() {
      sshKeyPair = client.getSSHKeyPairClient().createSSHKeyPair(keyPairName);
      assertNotNull(sshKeyPair.getPrivateKey());
      checkSSHKeyPair(sshKeyPair);
      client.getSSHKeyPairClient().deleteSSHKeyPair(sshKeyPair.getName());

      assertEquals(client.getSSHKeyPairClient().getSSHKeyPair(sshKeyPair.getName()), null);
      assertEquals(SshKeys.fingerprintPrivateKey(sshKeyPair.getPrivateKey()), sshKeyPair.getFingerprint());

      sshKeyPair = null;
   }

   @Test
   public void testRegisterDeleteSSHKeyPair() {
      final Map<String, String> sshKey = SshKeys.generate();
      final String publicKey = sshKey.get("public");

      sshKeyPair = client.getSSHKeyPairClient().registerSSHKeyPair(keyPairName, publicKey);
      assertNull(sshKeyPair.getPrivateKey());
      checkSSHKeyPair(sshKeyPair);
      client.getSSHKeyPairClient().deleteSSHKeyPair(keyPairName);

      assertEquals(client.getSSHKeyPairClient().getSSHKeyPair(sshKeyPair.getName()), null);
      assertEquals(SshKeys.fingerprintPublicKey(publicKey), sshKeyPair.getFingerprint());

      sshKeyPair = null;
   }

   protected void checkSSHKeyPair(SshKeyPair pair) {
      assert pair.getName() != null : pair;
      assertEquals(pair.getFingerprint(),
         client.getSSHKeyPairClient().getSSHKeyPair(pair.getName()).getFingerprint());
   }

}
