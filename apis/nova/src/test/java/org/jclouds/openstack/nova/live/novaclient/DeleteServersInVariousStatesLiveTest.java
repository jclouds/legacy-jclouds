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

package org.jclouds.openstack.nova.live.novaclient;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Victor Galkin
 */

@Test(groups = "live", sequential = true)
public class DeleteServersInVariousStatesLiveTest {

   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   @SuppressWarnings("unused")
   private Predicate<IPSocket> socketTester;
   protected String provider = "nova";

   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   Server server = null;

   @AfterMethod
   public void after() {
      if (server != null) client.deleteServer(server.getId());
   }

   @Test(enabled = true)
   public void testDeleteAfterCreate() throws Exception {

   }

   @SuppressWarnings("unused")
   private void blockUntilServerActive(int serverId) throws InterruptedException {
      Server currentDetails;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }
}
