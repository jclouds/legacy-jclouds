/*
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

package org.jclouds.virtualbox.predicates;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.CloneAndRegisterMachineFromIMachineIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "IsLinkedClonesLiveTest")
public class IsLinkedClonesLiveTest extends BaseVirtualBoxClientLiveTest {

   private static final boolean IS_LINKED_CLONE = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String ideControllerName = "IDE Controller";
   private String cloneName;
   private String vmName;
   private StorageController masterStorageController;
   private VmSpec masterSpec;
   private VmSpec cloneSpec;

   @Override
   @BeforeClass(groups = "live")
   public void setupClient() {
      super.setupClient();
      vmName = VIRTUALBOX_IMAGE_PREFIX 
            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      cloneName = VIRTUALBOX_IMAGE_PREFIX 
            + "Clone#" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk).autoDelete(true).controllerPort(0).deviceSlot(1)
               .build();
      masterStorageController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE).attachISO(0, 0,
               operatingSystemIso).attachHardDisk(hardDisk).attachISO(1, 1, guestAdditionsIso).build();
      masterSpec = VmSpec.builder().id(vmId).name(vmName).memoryMB(512).osTypeId(osTypeId).controller(
               masterStorageController).forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();
      
      cloneSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(512).cleanUpMode(CleanupMode.Full)
            .forceOverwrite(true).build();
   }

   @Test
   public void testLinkedClone() {

      IMachine master = context.utils().injector().getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class)
               .apply(masterSpec);
      IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, cloneSpec,
               IS_LINKED_CLONE).apply(master);

      assertTrue(new IsLinkedClone(manager).apply(clone));
   }

   /*
   public void testFullClone() {
      IMachine master = context.utils().injector().getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class)
               .apply(masterSpec);
      IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, cloneSpec,
               !IS_LINKED_CLONE).apply(master);

      assertFalse(new IsLinkedClone(manager).apply(clone));
   }
   */

   @BeforeMethod
   @AfterMethod
   void cleanUpVms() {
      for (VmSpec spec : ImmutableSet.of(cloneSpec, masterSpec))
         this.undoVm(spec);
   }
}
