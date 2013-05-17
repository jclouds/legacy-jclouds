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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.nova.v2_0.domain.BackupType;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.nova.v2_0.options.CreateBackupOfServerOptions;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
   private Optional<? extends ServerAdminApi> apiOption;
   private String zone;

   private String testServerId;
   private String backupImageId;

   @BeforeClass(groups = {"integration", "live"})
   @Override
   public void setup() {
      super.setup();
      zone = Iterables.getLast(api.getConfiguredZones(), "nova");
      serverApi = api.getServerApiForZone(zone);
      extensionApi = api.getExtensionApiForZone(zone);
      imageApi = api.getImageApiForZone(zone);
      apiOption = api.getServerAdminExtensionForZone(zone);
      if (apiOption.isPresent()) {
         testServerId = createServerInZone(zone).getId();
      }
   }

   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDown() {
      if (apiOption.isPresent()) {
         if (testServerId != null) {
            assertTrue(api.getServerApiForZone(zone).delete(testServerId));
         }
         if (backupImageId != null) {
            imageApi.delete(backupImageId);
         }
      }
      super.tearDown();
   }

   protected void skipOnAdminExtensionAbsent() {
      if (!apiOption.isPresent()) {
         throw new SkipException("Test depends on ServerAdminApi extension");
      }
   }

   @AfterMethod(alwaysRun = true)
   public void ensureServerIsActiveAgain() {
      if (apiOption.isPresent())
         blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
   }

   public void testSuspendAndResume() {
      skipOnAdminExtensionAbsent();
      ServerAdminApi api = apiOption.get();

      // Suspend-resume
      try {
         api.resume(testServerId);
         fail("Resumed an active server!");
      } catch (HttpResponseException e) {
      }
      assertTrue(api.suspend(testServerId));
      blockUntilServerInState(testServerId, serverApi, Status.SUSPENDED);
      try {
         api.suspend(testServerId);
         fail("Suspended an already suspended server!");
      } catch (HttpResponseException e) {
      }
      assertTrue(api.resume(testServerId));
      blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
      try {
         api.resume(testServerId);
         fail("Resumed an already resumed server!");
      } catch (HttpResponseException e) {
      }

   }

   public void testLockAndUnlock() {
      skipOnAdminExtensionAbsent();
      ServerAdminApi api = apiOption.get();

      // TODO should we be able to double-lock (as it were)
      assertTrue(api.unlock(testServerId));
      assertTrue(api.unlock(testServerId));
      assertTrue(api.lock(testServerId));
      assertTrue(api.lock(testServerId));
      assertTrue(api.unlock(testServerId));
      assertTrue(api.unlock(testServerId));

   }

   public void testResetNetworkAndInjectNetworkInfo() {
      skipOnAdminExtensionAbsent();
      ServerAdminApi api = apiOption.get();
      assertTrue(api.resetNetwork(testServerId));
      assertTrue(api.injectNetworkInfo(testServerId));
   }

   @Test
   public void testPauseAndUnpause() {
      skipOnAdminExtensionAbsent();
      ServerAdminApi api = apiOption.get();

      // Unlock and lock (double-checking error contitions too)
      try {
         api.unpause(testServerId);
         fail("Unpaused active server!");
      } catch (HttpResponseException e) {
      }
      assertTrue(api.pause(testServerId));
      blockUntilServerInState(testServerId, serverApi, Status.PAUSED);
      try {
         api.pause(testServerId);
         fail("paused a paused server!");
      } catch (HttpResponseException e) {
      }
      assertTrue(api.unpause(testServerId));
      blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);
      try {
         api.unpause(testServerId);
         fail("Unpaused a server we just unpaused!");
      } catch (HttpResponseException e) {
      }

   }

   @Test
   public void testCreateBackupOfServer() throws InterruptedException {
      skipOnAdminExtensionAbsent();
      backupImageId = apiOption.get().createBackup(testServerId, "jclouds-test-backup", BackupType.DAILY, 0,
               CreateBackupOfServerOptions.Builder.metadata(ImmutableMap.of("test", "metadata")));

      assertNotNull(backupImageId);

      // If we don't have extended task status, we'll have to wait here!
      if (extensionApi.get("OS-EXT-STS") == null) {
         Thread.sleep(30000);
      }

      blockUntilServerInState(testServerId, serverApi, Status.ACTIVE);

      Image backupImage = imageApi.get(backupImageId);
      assertEquals(backupImage.getId(), backupImageId);
   }
}
