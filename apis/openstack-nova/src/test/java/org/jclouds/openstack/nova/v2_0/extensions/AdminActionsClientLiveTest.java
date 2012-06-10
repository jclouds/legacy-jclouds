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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.extensions.AdminActionsClient;
import org.jclouds.openstack.nova.v2_0.features.ExtensionClient;
import org.jclouds.openstack.nova.v2_0.features.ImageClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaClientLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of HostAdministrationClient
 * 
 * TODO test migration methods
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "AdminActionsClientLiveTest", singleThreaded = true)
public class AdminActionsClientLiveTest extends BaseNovaClientLiveTest {
   private ImageClient imageClient;
   private ServerClient serverClient;
   private ExtensionClient extensionClient;
   private Optional<AdminActionsClient> clientOption;
   private String zone;

   private String testServerId;
   private String backupImageId;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      serverClient = novaContext.getApi().getServerClientForZone(zone);
      extensionClient = novaContext.getApi().getExtensionClientForZone(zone);
      imageClient = novaContext.getApi().getImageClientForZone(zone);
      clientOption = novaContext.getApi().getAdminActionsExtensionForZone(zone);
      if (clientOption.isPresent()) {
         testServerId = createServerInZone(zone).getId();
      }
   }

   @AfterGroups(groups = "live", alwaysRun = true)
   @Override
   protected void tearDown() {
      if (clientOption.isPresent()) {
         if (testServerId != null) {
            assertTrue(novaContext.getApi().getServerClientForZone(zone).deleteServer(testServerId));
         }
         if (backupImageId != null) {
            imageClient.deleteImage(backupImageId);
         }
      }
      super.tearDown();
   }

   @AfterMethod(alwaysRun = true)
   public void ensureServerIsActiveAgain() {
      blockUntilServerInState(testServerId, serverClient, Status.ACTIVE);
   }
   
   public void testSuspendAndResume() {
      if (clientOption.isPresent()) {
         AdminActionsClient client = clientOption.get();

         // Suspend-resume
         try {
            client.resumeServer(testServerId);
            fail("Resumed an active server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(client.suspendServer(testServerId));
         blockUntilServerInState(testServerId, serverClient, Status.SUSPENDED);
         try {
            client.suspendServer(testServerId);
            fail("Suspended an already suspended server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(client.resumeServer(testServerId));
         blockUntilServerInState(testServerId, serverClient, Status.ACTIVE);
         try {
            client.resumeServer(testServerId);
            fail("Resumed an already resumed server!");
         } catch (HttpResponseException e) {
         }
      }
   }

   public void testLockAndUnlock() {
      if (clientOption.isPresent()) {
         AdminActionsClient client = clientOption.get();

         // TODO should we be able to double-lock (as it were)
         assertTrue(client.unlockServer(testServerId));
         assertTrue(client.unlockServer(testServerId));
         assertTrue(client.lockServer(testServerId));
         assertTrue(client.lockServer(testServerId));
         assertTrue(client.unlockServer(testServerId));
         assertTrue(client.unlockServer(testServerId));
      }
   }

   public void testResetNetworkAndInjectNetworkInfo() {
      if (clientOption.isPresent()) {
         AdminActionsClient client = clientOption.get();
         assertTrue(client.resetNetworkOfServer(testServerId));
         assertTrue(client.injectNetworkInfoIntoServer(testServerId));
      }
   }

   @Test
   public void testPauseAndUnpause() {
      if (clientOption.isPresent()) {
         AdminActionsClient client = clientOption.get();

         // Unlock and lock (double-checking error contitions too)
         try {
            client.unpauseServer(testServerId);
            fail("Unpaused active server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(client.pauseServer(testServerId));
         blockUntilServerInState(testServerId, serverClient, Status.PAUSED);
         try {
            client.pauseServer(testServerId);
            fail("paused a paused server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(client.unpauseServer(testServerId));
         blockUntilServerInState(testServerId, serverClient, Status.ACTIVE);
         try {
            client.unpauseServer(testServerId);
            fail("Unpaused a server we just unpaused!");
         } catch (HttpResponseException e) {
         }
      }
   }

   @Test
   public void testCreateBackupOfServer() throws InterruptedException {
      if (clientOption.isPresent()) {
         backupImageId = clientOption.get().createBackupOfServer(testServerId, "jclouds-test-backup", BackupType.DAILY, 0,
               CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("test", "metadata")));

         assertNotNull(backupImageId);
         
         // If we don't have extended task status, we'll have to wait here!
         if (extensionClient.getExtensionByAlias("OS-EXT-STS") == null) {
            Thread.sleep(30000);
         }
         
         blockUntilServerInState(testServerId, serverClient, Status.ACTIVE);
         
         Image backupImage = imageClient.getImage(backupImageId);
         assertEquals(backupImage.getId(), backupImageId);
      }
   }   
}
