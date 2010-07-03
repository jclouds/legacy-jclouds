/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark;

import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.jclouds.vcloud.terremark.domain.TerremarkOrganization;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class TerremarkVCloudClientLiveTest extends TerremarkClientLiveTest {

   KeyPair key;

   @Test
   public void testKeysList() throws Exception {
      TerremarkVCloudClient tmVCloudClient = TerremarkVCloudClient.class.cast(tmClient);
      TerremarkOrganization org = tmVCloudClient.getDefaultOrganization();
      Set<KeyPair> response = tmVCloudClient.listKeyPairs();
      assertNotNull(response);
      System.err.println(response);
      assertEquals(response, tmVCloudClient.listKeyPairsInOrg(org.getId()));
   }

   @Override
   protected void prepare() {
      TerremarkVCloudClient tmVCloudClient = TerremarkVCloudClient.class.cast(tmClient);

      TerremarkOrganization org = tmVCloudClient.getDefaultOrganization();
      key = tmVCloudClient.generateKeyPairInOrg(org.getId(), "livetest", false);
      assertNotNull(key);
      System.err.println(key);
      assertEquals(key.getName(), "livetest");
      assertNotNull(key.getPrivateKey());
      assertNotNull(key.getFingerPrint());
      assertEquals(key.isDefault(), false);
      assertEquals(key.getFingerPrint(), tmVCloudClient.getKeyPair(key.getId()).getFingerPrint());
   }

   @AfterTest
   void cleanup1() throws InterruptedException, ExecutionException, TimeoutException {
      if (key != null) {
         TerremarkVCloudClient tmVCloudClient = TerremarkVCloudClient.class.cast(tmClient);
         tmVCloudClient.deleteKeyPair(key.getId());
      }
   }

   @Override
   protected SshClient getConnectionFor(IPSocket socket) {
      return sshFactory.create(socket, "vcloud", key.getPrivateKey().getBytes());
   }

   @Override
   protected TerremarkInstantiateVAppTemplateOptions createInstantiateOptions() {
      return processorCount(1).memory(512).sshKeyFingerprint(key.getFingerPrint());
   }
}
