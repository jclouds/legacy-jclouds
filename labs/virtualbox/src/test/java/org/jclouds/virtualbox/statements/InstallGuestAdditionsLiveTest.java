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

package org.jclouds.virtualbox.statements;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.StorageBus;

/**
 * @author Andrea Turli, David Alves
 */
@Test(testName = "InstallGuestAdditionsLiveTest", groups = "live", singleThreaded = true)
public class InstallGuestAdditionsLiveTest extends BaseVirtualBoxClientLiveTest {

   public void testIsoPresent() {
      StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE)
               .attachISO(1, 0, "VBoxGuestAdditions_").build();

      VmSpec vmSpecification = VmSpec.builder().id("").name("").memoryMB(512).osTypeId("").controller(ideController)
               .forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();

      InstallGuestAdditions installer = new InstallGuestAdditions(vmSpecification, "4.1.8");
      String scripts = installer.render(OsFamily.UNIX);
      assertEquals("installModuleAssistantIfNeeded || return 1\n" + "mount -t iso9660 /dev/sr1 /mnt\n"
            + "/mnt/VBoxLinuxAdditions.run --nox11\n", scripts);
   }
   
   public void testIsoNotPresent() {
      StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE).build();

      VmSpec vmSpecification = VmSpec.builder().id("").name("").memoryMB(512).osTypeId("").controller(ideController)
               .forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();

      InstallGuestAdditions installer = new InstallGuestAdditions(vmSpecification, "4.1.8");
      String scripts = installer.render(OsFamily.UNIX);
      assertEquals(
               "installModuleAssistantIfNeeded || return 1\n"
                        + "setupPublicCurl || return 1\n"
                        + "(mkdir -p /tmp/ && cd /tmp/ && [ ! -f VBoxGuestAdditions_4.1.8.iso ] && curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -C - -X GET  http://download.virtualbox.org/virtualbox/4.1.8/VBoxGuestAdditions_4.1.8.iso >VBoxGuestAdditions_4.1.8.iso)\n"
                        + "mount -o loop /tmp/VBoxGuestAdditions_4.1.8.iso /mnt\n"
                        + "/mnt/VBoxLinuxAdditions.run --nox11\n", scripts);
   }

}
