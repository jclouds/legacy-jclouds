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

package org.jclouds.virtualbox.functions;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.Assert.assertEquals;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest extends
      BaseVirtualBoxClientLiveTest {

   private static final boolean IS_LINKED_CLONE = true;

   private VmSpec clonedVmSpec;
   private MasterSpec sourceMachineSpec;

   private CleanupMode mode = CleanupMode.Full;

   @Override
   @BeforeClass(groups = "live")
   public void setupClient() {
      super.setupClient();
      String sourceName = VIRTUALBOX_IMAGE_PREFIX
            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
                  .getSimpleName());
      String cloneName = VIRTUALBOX_IMAGE_PREFIX
            + "Clone#"
            + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
                  .getSimpleName());

      StorageController ideController = StorageController
            .builder()
            .name("IDE Controller")
            .bus(StorageBus.IDE)
            .attachISO(0, 0, operatingSystemIso)
            .attachHardDisk(
                  HardDisk.builder().diskpath(adminDisk).controllerPort(0)
                        .deviceSlot(1).autoDelete(true).build())
            .attachISO(1, 1, guestAdditionsIso).build();

      VmSpec sourceVmSpec = VmSpec.builder().id(sourceName).name(sourceName)
            .osTypeId("").memoryMB(512).cleanUpMode(CleanupMode.Full)
            .controller(ideController).forceOverwrite(true).build();

      Injector injector = context.utils().injector();
      Function<String, String> configProperties = injector
            .getInstance(ValueOfConfigurationKeyOrNull.class);
      IsoSpec isoSpec = IsoSpec
            .builder()
            .sourcePath(operatingSystemIso)
            .installationScript(
                  configProperties.apply(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE)
                        .replace("HOSTNAME", sourceVmSpec.getVmName())).build();

      NetworkSpec networkSpec = NetworkSpec.builder().build();
      sourceMachineSpec = MasterSpec.builder().iso(isoSpec).vm(sourceVmSpec).network(networkSpec).build();

      clonedVmSpec = VmSpec.builder().id(cloneName).name(cloneName)
            .memoryMB(512).cleanUpMode(mode).forceOverwrite(true).build();
   }

   @Test
   public void testCloneMachineFromAnotherMachine() throws Exception {
      try {
         IMachine source = getSourceNode();

         if (source.getCurrentSnapshot() != null) {
            ISession session = manager.get().openMachineSession(source);
            session.getConsole().deleteSnapshot(
                  source.getCurrentSnapshot().getId());
            session.unlockMachine();
         }

         IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
               manager, workingDir, clonedVmSpec, IS_LINKED_CLONE)
               .apply(source);
         assertEquals(clone.getName(), clonedVmSpec.getVmName());
      } finally {
         for (VmSpec spec : ImmutableSet.of(clonedVmSpec,
               sourceMachineSpec.getVmSpec()))
            undoVm(spec);
      }

   }

   private IMachine getSourceNode() {
      try {
         Injector injector = context.utils().injector();
         return injector.getInstance(
               CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class).apply(
               sourceMachineSpec);
      } catch (IllegalStateException e) {
         // already created
         return manager.get().getVBox()
               .findMachine(sourceMachineSpec.getVmSpec().getVmId());
      }
   }

}