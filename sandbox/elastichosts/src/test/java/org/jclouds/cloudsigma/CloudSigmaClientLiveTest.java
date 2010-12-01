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
import org.jclouds.elastichosts.CommonElasticHostsClientLiveTest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code CloudSigmaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudsigma.CloudSigmaClientLiveTest")
public class CloudSigmaClientLiveTest extends CommonElasticHostsClientLiveTest<CloudSigmaClient, CloudSigmaAsyncClient> {

   public CloudSigmaClientLiveTest() {
      provider = "cloudsigma";
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
   protected void checkDriveMatchesGet(org.jclouds.elastichosts.domain.DriveInfo newInfo) {
      super.checkDriveMatchesGet(newInfo);
      assertEquals(DriveInfo.class.cast(newInfo).getType(), DriveType.DISK);
   }

   @Override
   protected void checkCreatedDrive() {
      super.checkCreatedDrive();
      assertEquals(DriveInfo.class.cast(info).getType(), null);
   }
}
