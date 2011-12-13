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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKey;
import org.jclouds.tmrk.enterprisecloud.domain.keys.SSHKeys;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Tests behavior of {@code SSHKeyClient}
 * 
 * @author Jason King
 */
@Test(groups = "live", testName = "SSHKeyClientLiveTest")
public class SSHKeyClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getSSHKeyClient();
   }

   private SSHKeyClient client;

   public void testGetSSHKeys() throws Exception {
      //TODO: Remove the hardcoded uri once have access to organization
      SSHKeys sshKeys = client.getSSHKeys(URI.create("/cloudapi/ecloud/admin/sshkeys/organizations/17"));
      for(SSHKey key: sshKeys.getSSHKeys()) {
         testGetSSHKey(key.getHref());
      }
   }

   private void testGetSSHKey(URI uri) {
      SSHKey sshKey = client.getSSHKey(uri);
      assertNotNull(sshKey);
      assertNotNull(sshKey.getFingerPrint());
   }
   
   public void testCreateSSHKey() {
      SSHKey sshKey = client.createSSHKey(URI.create("/cloudapi/ecloud/admin/sshkeys/organizations/17/action/createsshkey"),"mylivetestkey",false);
      assertNotNull(sshKey);
      assertEquals(sshKey.getName(),"mylivetestkey");
      assertFalse(sshKey.isDefaultKey());
      assertFalse(sshKey.getFingerPrint().isEmpty());
      assertFalse(sshKey.getPrivateKey().isEmpty());
   }
}