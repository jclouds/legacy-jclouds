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

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.predicates.IMachinePredicates.isLinkedClone;
import static org.testng.Assert.assertTrue;

import java.util.Set;

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
import org.jclouds.virtualbox.functions.CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * 
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "IMachinePredicatesLiveTest")
public class IMachinePredicatesLiveTest extends BaseVirtualBoxClientLiveTest {

  private String            osTypeId          = "";
  private String            ideControllerName = "IDE Controller";
  private String            cloneName;
  private String            vmName;
  private StorageController masterStorageController;
  private MasterSpec        masterMachineSpec;
  private NetworkSpec       networkSpec;
  private CloneSpec cloneSpec;

  @Override
  @BeforeClass(groups = "live")
  public void setupClient() {
    super.setupClient();
    vmName = VIRTUALBOX_IMAGE_PREFIX + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

    cloneName = VIRTUALBOX_IMAGE_PREFIX + "Clone#"
        + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());

      HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk(vmName)).autoDelete(true).controllerPort(0)
               .deviceSlot(1).build();
    masterStorageController = StorageController.builder().name(ideControllerName).bus(StorageBus.IDE)
        .attachISO(0, 0, operatingSystemIso).attachHardDisk(hardDisk).attachISO(1, 1, guestAdditionsIso).build();
    VmSpec masterSpec = VmSpec.builder().id(vmName).name(vmName).memoryMB(512).osTypeId(osTypeId)
        .controller(masterStorageController).forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();
    masterMachineSpec = MasterSpec.builder()
        .iso(IsoSpec.builder().sourcePath(operatingSystemIso).installationScript("").build()).vm(masterSpec)
        .network(NetworkSpec.builder().build()).build();

    NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.Bridged)
        .build();
    NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(networkAdapter)
        .build();

    this.networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();

  }

  @Test
  public void testLinkedClone() {

    Injector injector = context.utils().injector();
    IMachine master = injector.getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class).apply(
        masterMachineSpec);

    VmSpec clonedVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(512).cleanUpMode(CleanupMode.Full)
        .forceOverwrite(true).build();

    this.cloneSpec = CloneSpec.builder().vm(clonedVmSpec).network(networkSpec).master(master).linked(true).build();

    IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, workingDir, machineUtils)
        .apply(cloneSpec);

    assertTrue(isLinkedClone().apply(clone));
  }

  @BeforeMethod
  @AfterMethod
   void cleanUpVms() {
      Set<VmSpec> specs = cloneSpec != null ? ImmutableSet.of(cloneSpec.getVmSpec(), masterMachineSpec.getVmSpec())
               : ImmutableSet.of(masterMachineSpec.getVmSpec());
      for (VmSpec spec : specs)
         this.undoVm(spec);
   }
}
