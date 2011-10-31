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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.cloudstack.domain.SSHKeyPair;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SSHKeyPairClient}
 * 
 * @author Vijay Kiran
 */
@Test(groups = "live", singleThreaded = true, testName = "SSHKeyPairClientLiveTest")
public class SSHKeyPairClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListSSHKeyPairs() {
      final Set<SSHKeyPair> sshKeyPairs = client.getSSHKeyPairClient().listSSHKeyPairs();
      for (SSHKeyPair sshKeyPair : sshKeyPairs) {
         checkSSHKeyPair(sshKeyPair);
      }
   }

   public void testCreateSSHKeyPair() {
      final SSHKeyPair sshKeyPair = client.getSSHKeyPairClient().createSSHKeyPair("jclouds-keypair");
      System.out.println(sshKeyPair);
   }

   protected void checkSSHKeyPair(SSHKeyPair pair) {
      assert pair.getName() != null : pair;
      assertEquals(pair.toString(), client.getSSHKeyPairClient().getSSHKeyPair(pair.getName()).toString());
   }

}
