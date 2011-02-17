/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.jclouds.vcloud.terremark.domain.TerremarkOrg;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.suppliers.TerremarkVCloudExpressInternetServiceAndPublicIpAddressSupplier;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class TerremarkVCloudExpressClientLiveTest extends TerremarkClientLiveTest {

   KeyPair key;

   @Test
   public void testKeysList() throws Exception {
      TerremarkVCloudExpressClient vCloudExpressClient = TerremarkVCloudExpressClient.class.cast(tmClient);
      TerremarkOrg org = vCloudExpressClient.findOrgNamed(null);
      Set<KeyPair> response = vCloudExpressClient.listKeyPairsInOrg(null);
      assertNotNull(response);
      System.err.println(response);
      assertEquals(response, vCloudExpressClient.listKeyPairsInOrg(org.getHref()));
   }

   @Override
   protected void prepare() {
      TerremarkVCloudExpressClient vCloudExpressClient = TerremarkVCloudExpressClient.class.cast(tmClient);

      TerremarkOrg org = vCloudExpressClient.findOrgNamed(null);
      try {
         key = vCloudExpressClient.generateKeyPairInOrg(org.getHref(), "livetest", false);
      } catch (IllegalStateException e) {
         key = vCloudExpressClient.findKeyPairInOrg(org.getHref(), "livetest");
         vCloudExpressClient.deleteKeyPair(key.getId());
         key = vCloudExpressClient.generateKeyPairInOrg(org.getHref(), "livetest", false);
      }
      assertNotNull(key);
      System.err.println(key);
      assertEquals(key.getName(), "livetest");
      assertNotNull(key.getPrivateKey());
      assertNotNull(key.getFingerPrint());
      assertEquals(key.isDefault(), false);
      assertEquals(key.getFingerPrint(), vCloudExpressClient.findKeyPairInOrg(org.getHref(), key.getName())
               .getFingerPrint());
   }

   @AfterTest
   void cleanup1() throws InterruptedException, ExecutionException, TimeoutException {
      if (key != null) {
         TerremarkVCloudExpressClient vCloudExpressClient = TerremarkVCloudExpressClient.class.cast(tmClient);
         vCloudExpressClient.deleteKeyPair(key.getId());
      }
   }

   @Override
   protected SshClient getConnectionFor(IPSocket socket) {
      return sshFactory.create(socket, new Credentials("vcloud", key.getPrivateKey()));
   }

   @Override
   protected TerremarkInstantiateVAppTemplateOptions createInstantiateOptions() {
      return processorCount(1).memory(512).sshKeyFingerprint(key.getFingerPrint());
   }

   @Override
   protected Entry<InternetService, PublicIpAddress> getNewInternetServiceAndIpForSSH(VCloudExpressVApp vApp) {
      return new TerremarkVCloudExpressInternetServiceAndPublicIpAddressSupplier(TerremarkVCloudExpressClient.class
               .cast(tmClient)).getNewInternetServiceAndIp(vApp, 22, Protocol.TCP);
   }
}
