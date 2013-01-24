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

package org.jclouds.virtualbox.predicates;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.testng.Assert.assertTrue;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.CloneAndRegisterMachineFromIMachineIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.CreateAndInstallVm;
import org.jclouds.virtualbox.functions.IMachineToSshClient;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_2.CleanupMode;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "GuestAdditionsInstallerLiveTest")
public class GuestAdditionsInstallerLiveTest extends BaseVirtualBoxClientLiveTest {

   private Injector injector;
   private Function<IMachine, SshClient> sshClientForIMachine;
   private Predicate<SshClient> sshResponds;
   private VmSpec instanceVmSpec;
   private NetworkSpec instanceNetworkSpec;
   
   @Override
   @BeforeClass(groups = "live")
   public void setupContext() {
      super.setupContext();
      injector = view.utils().injector();

      String instanceName = VIRTUALBOX_IMAGE_PREFIX
               + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      StorageController ideController = StorageController
               .builder()
               .name("IDE Controller")
               .bus(StorageBus.IDE)
               .attachISO(0, 0, operatingSystemIso)
               .attachHardDisk(
                        HardDisk.builder().diskpath(adminDisk(instanceName)).controllerPort(0).deviceSlot(1)
                                 .autoDelete(true).build()).attachISO(1, 1, guestAdditionsIso).build();

      instanceVmSpec = VmSpec.builder().id(instanceName).name(instanceName).osTypeId("").memoryMB(512)
               .cleanUpMode(CleanupMode.Full).controller(ideController).forceOverwrite(true).build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder()
            .networkAttachmentType(NetworkAttachmentType.HostOnly)
             .build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
            .addHostInterfaceName("vboxnet0").slot(0L).build();

      instanceNetworkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();
   }

   @Test
   public void testGuestAdditionsAreInstalled() throws Exception {
      IMachine machine = null;
      try {
         machine = cloneFromMaster();
         machineController.ensureMachineIsLaunched(machine.getName());
         sshClientForIMachine = injector.getInstance(IMachineToSshClient.class);
         SshClient client = sshClientForIMachine.apply(machine);

         sshResponds = injector.getInstance(SshResponds.class);
         checkState(sshResponds.apply(client), "timed out waiting for guest %s to be accessible via ssh",
                  machine.getName());
         
         assertTrue(NetworkUtils.isIpv4(networkUtils.getIpAddressFromNicSlot(machine.getName(), 0l)));

      } finally {
         if(machine!=null) {
            for (String vmNameOrId : ImmutableSet.of(machine.getName())) {
               machineController.ensureMachineHasPowerDown(vmNameOrId);
               undoVm(vmNameOrId);
            }
         }
      }
   }

   protected IMachine cloneFromMaster() {
      IMachine source = getVmWithGuestAdditionsInstalled();
      CloneSpec cloneSpec = CloneSpec.builder()
            .vm(instanceVmSpec)
            .network(instanceNetworkSpec)
            .master(source)
            .linked(true)
            .build();
      return new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, machineUtils)
               .apply(cloneSpec);
   }

   private IMachine getVmWithGuestAdditionsInstalled() {
      MasterSpec masterSpecForTest = getMasterSpecForTest();
      try {
         Injector injector = view.utils().injector();
         return injector.getInstance(CreateAndInstallVm.class).apply(masterSpecForTest);
      } catch (IllegalStateException e) {
         // already created
         return manager.get().getVBox().findMachine(masterSpecForTest.getVmSpec().getVmId());
      }
   }
}
