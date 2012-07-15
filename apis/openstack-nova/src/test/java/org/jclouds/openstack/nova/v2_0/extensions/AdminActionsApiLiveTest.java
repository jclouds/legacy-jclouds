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
import org.jclouds.openstack.nova.v2_0.features.ExtensionApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of HostAdministrationApi
 * 
 * TODO test migration methods
 *
 * @author Adam Lowe
 */
@Test(groups = "live", testName = "AdminActionsApiLiveTest", singleThreaded = true)
public class AdminActionsApiLiveTest extends BaseNovaApiLiveTest {
   private ImageApi imageApi;
   private ServerApi serverApi;
   private ExtensionApi extensionApi;
   private Optional<AdminActionsApi> apiOption;
   private String zone;

   private String testServerId;
   private String backupImageId;

   @BeforeGroups(groups = {"integration", "live"})
   @Override
   public void setupContext() {
      super.setupContext();
      zone = Iterables.getLast(novaContext.getApi().getConfiguredZones(), "nova");
      serverApi = novaContext.getApi().getServerApiForZone(zone);
      extensionApi = novaContext.getApi().getExtensionApiForZone(zone);
      imageApi = novaContext.getApi().getImageApiForZone(zone);
      apiOption = novaContext.getApi().getAdminActionsExtensionForZone(zone);
      if (apiOption.isPresent()) {
         testServerId = createServerInZone(zone).getId();
      }
   }

   @AfterGroups(groups = "live", alwaysRun = true)
   @Override
   protected void tearDown() {
      if (apiOption.isPresent()) {
         if (testServerId != null) {
            assertTrue(novaContext.getApi().getServerApiForZone(zone).deleteServer(testServerId));
         }
         if (backupImageId != null) {
            imageApi.deleteImage(backupImageId);
         }
      }
      super.tearDown();
   }

   @AfterMethod(alwaysRun = true)
   public void ensureServerIsActiveAgain() {
      blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
   }
   
   public void testSuspendAndResume() {
      if (apiOption.isPresent()) {
         AdminActionsApi api = apiOption.get();

         // Suspend-resume
         try {
            api.resumeServer(testServerId);
            fail("Resumed an active server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(api.suspendServer(testServerId));
         blockUntilServerInState(testServerId, serverApi, Status.SUSPENDED);
         try {
            api.suspendServer(testServerId);
            fail("Suspended an already suspended server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(api.resumeServer(testServerId));
         blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
         try {
            api.resumeServer(testServerId);
            fail("Resumed an already resumed server!");
         } catch (HttpResponseException e) {
         }
      }
   }

   public void testLockAndUnlock() {
      if (apiOption.isPresent()) {
         AdminActionsApi api = apiOption.get();

         // TODO should we be able to double-lock (as it were)
         assertTrue(api.unlockServer(testServerId));
         assertTrue(api.unlockServer(testServerId));
         assertTrue(api.lockServer(testServerId));
         assertTrue(api.lockServer(testServerId));
         assertTrue(api.unlockServer(testServerId));
         assertTrue(api.unlockServer(testServerId));
      }
   }

   public void testResetNetworkAndInjectNetworkInfo() {
      if (apiOption.isPresent()) {
         AdminActionsApi api = apiOption.get();
         assertTrue(api.resetNetworkOfServer(testServerId));
         assertTrue(api.injectNetworkInfoIntoServer(testServerId));
      }
   }

   @Test
   public void testPauseAndUnpause() {
      if (apiOption.isPresent()) {
         AdminActionsApi api = apiOption.get();

         // Unlock and lock (double-checking error contitions too)
         try {
            api.unpauseServer(testServerId);
            fail("Unpaused active server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(api.pauseServer(testServerId));
         blockUntilServerInState(testServerId, serverApi, Status.PAUSED);
         try {
            api.pauseServer(testServerId);
            fail("paused a paused server!");
         } catch (HttpResponseException e) {
         }
         assertTrue(api.unpauseServer(testServerId));
         blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
         try {
            api.unpauseServer(testServerId);
            fail("Unpaused a server we just unpaused!");
         } catch (HttpResponseException e) {
         }
      }
   }

   @Test
   public void testCreateBackupOfServer() throws InterruptedException {
      if (apiOption.isPresent()) {
         backupImageId = apiOption.get().createBackupOfServer(testServerId, "jclouds-test-backup", BackupType.DAILY, 0,
               CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("test", "metadata")));

         assertNotNull(backupImageId);
         
         // If we don't have extended task status, we'll have to wait here!
         if (extensionApi.getExtensionByAlias("OS-EXT-STS") == null) {
            Thread.sleep(30000);
         }
         
         blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
         
         Image backupImage = imageApi.getImage(backupImageId);
         assertEquals(backupImage.getId(), backupImageId);
      }
   }   
}
