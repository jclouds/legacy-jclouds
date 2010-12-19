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

package org.jclouds.cloudsigma;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.DriveType;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.elasticstack.CommonElasticStackClientLiveTest;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code CloudSigmaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudsigma.CloudSigmaClientLiveTest")
public class CloudSigmaClientLiveTest extends CommonElasticStackClientLiveTest<CloudSigmaClient, CloudSigmaAsyncClient> {

   public CloudSigmaClientLiveTest() {
      provider = "cloudsigma";
      driveSize = 8 * 1024 * 1024 * 1024l;
      maxDriveImageTime = 300;
   }

   @Test
   public void testListStandardDrives() throws Exception {
      Set<String> drives = client.listStandardDrives();
      assertNotNull(drives);
   }

   @Test
   public void testListStandardCds() throws Exception {
      Set<String> drives = client.listStandardCds();
      assertNotNull(drives);
   }

   @Test
   public void testListStandardImages() throws Exception {
      Set<String> drives = client.listStandardImages();
      assertNotNull(drives);
   }

   @Override
   protected void checkDriveMatchesGet(org.jclouds.elasticstack.domain.DriveInfo newInfo) {
      super.checkDriveMatchesGet(newInfo);
      assertEquals(DriveInfo.class.cast(newInfo).getType(), DriveType.DISK);
   }

   @Override
   protected void checkCreatedDrive() {
      super.checkCreatedDrive();
      assertEquals(DriveInfo.class.cast(drive).getType(), null);
   }

   @Override
   protected Credentials getSshCredentials(Server server) {
      return new Credentials("cloudsigma", "cloudsigma");
   }

   @Override
   protected void prepareDrive() {
      client.destroyDrive(drive.getUuid());
      drive = client.cloneDrive("0b060e09-d98b-44cc-95a4-7e3a22ba1b53", drive.getName(),
            new CloneDriveOptions().size(driveSize));
      assert driveNotClaimed.apply(drive) : client.getDriveInfo(drive.getUuid());
      System.err.println("after prepare" + client.getDriveInfo(drive.getUuid()));
   }

   @Override
   protected void checkTagsAndMetadata(ServerInfo server2) {
      // bug where tags aren't updated
      assertEquals(server2.getTags(), ImmutableSet.<String> of());
      assertEquals(server2.getUserMetadata(), ImmutableMap.<String, String> of());
   }
}
