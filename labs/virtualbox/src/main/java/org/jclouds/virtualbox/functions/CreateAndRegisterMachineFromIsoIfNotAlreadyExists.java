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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.virtualbox.util.MachineUtils.machineNotFoundException;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.DeviceDetails;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoImage;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_2.AccessMode;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;
import org.virtualbox_4_2.IVirtualBox;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * @author Mattias Holmqvist
 */
@Singleton
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExists implements Function<MasterSpec, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final MachineUtils machineUtils;

   private final String workingDir;

   @Inject
   public CreateAndRegisterMachineFromIsoIfNotAlreadyExists(Supplier<VirtualBoxManager> manager,
            MachineUtils machineUtils, @Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR) String workingDir) {
      this.manager = manager;
      this.machineUtils = machineUtils;
      this.workingDir = workingDir;
   }

   @Override
   public IMachine apply(MasterSpec masterSpec) {
      final IVirtualBox vBox = manager.get().getVBox();
      String vmName = masterSpec.getVmSpec().getVmName();
      String vmId = masterSpec.getVmSpec().getVmId();

      try {
         vBox.findMachine(vmId);
         throw new IllegalStateException("Machine " + vmName + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return createMachine(vBox, masterSpec);
         else
            throw e;
      }
   }

   private IMachine createMachine(IVirtualBox vBox, MasterSpec masterSpec) {
      VmSpec vmSpec = masterSpec.getVmSpec();
      String flags = "";
      List<String> groups = Lists.newArrayList();
      String group = "";
      String settingsFile = manager.get().getVBox().composeMachineFilename(vmSpec.getVmName(), group , flags , workingDir);

      IMachine newMachine = vBox.createMachine(settingsFile, vmSpec.getVmName(), groups, vmSpec.getOsTypeId(), flags);
      
      manager.get().getVBox().registerMachine(newMachine);
      ensureConfiguration(masterSpec);
      return newMachine;
   }

   private void ensureConfiguration(MasterSpec machineSpec) {
      VmSpec vmSpec = machineSpec.getVmSpec();
      NetworkSpec networkSpec = machineSpec.getNetworkSpec();
      String vmName = vmSpec.getVmName();

      // Change BootOrder
      Map<Long, DeviceType> positionAndDeviceType = ImmutableMap.of(1l, DeviceType.HardDisk);
      ensureMachineHasDesiredBootOrder(vmName, positionAndDeviceType);

      // Change RAM
      ensureMachineHasMemory(vmName, vmSpec.getMemory());

      Set<StorageController> controllers = vmSpec.getControllers();
      if (controllers.isEmpty()) {
         throw new IllegalStateException(missingIDEControllersMessage(vmSpec));
      }
      StorageController controller = controllers.iterator().next();
      ensureMachineHasStorageControllerNamed(vmName, controller);
      setupHardDisksForController(vmName, controller);
      setupDvdsForController(vmSpec, vmName, controller);

      // Networking
      for (NetworkInterfaceCard networkInterfaceCard : networkSpec.getNetworkInterfaceCards()) {
         new AttachNicToMachine(vmName, machineUtils).apply(networkInterfaceCard);
      }
   }

   private void setupDvdsForController(VmSpec vmSpecification, String vmName, StorageController controller) {
      Set<IsoImage> dvds = controller.getIsoImages();
      for (IsoImage dvd : dvds) {
         String dvdSource = dvd.getSourcePath();
         final IMedium dvdMedium = manager.get().getVBox()
                  .openMedium(dvdSource, DeviceType.DVD, AccessMode.ReadOnly, vmSpecification.isForceOverwrite());
         ensureMachineDevicesAttached(vmName, dvdMedium, dvd.getDeviceDetails(), controller.getName());
      }
   }

   private void ensureMachineDevicesAttached(String vmName, IMedium medium, DeviceDetails deviceDetails,
            String controllerName) {
      machineUtils.writeLockMachineAndApply(vmName, new AttachMediumToMachineIfNotAlreadyAttached(deviceDetails,
               medium, controllerName));
   }

   private String missingIDEControllersMessage(VmSpec vmSpecification) {
      return String
               .format("First controller is not an IDE controller. Please verify that the VM spec is a correct master node: %s",
                        vmSpecification);
   }

   private void setupHardDisksForController(String vmName, StorageController controller) {
      Set<HardDisk> hardDisks = controller.getHardDisks();
      for (HardDisk hardDisk : hardDisks) {
         String sourcePath = hardDisk.getDiskPath();
         if (new File(sourcePath).exists()) {
            boolean deleted = new File(sourcePath).delete();
            if (!deleted) {
               logger.error(String.format("File %s could not be deleted.", sourcePath));
            }
         }
         IMedium medium = new CreateMediumIfNotAlreadyExists(manager, machineUtils, true).apply(hardDisk);
         ensureMachineDevicesAttached(vmName, medium, hardDisk.getDeviceDetails(), controller.getName());
      }
   }

   private void ensureMachineHasDesiredBootOrder(String vmName, Map<Long, DeviceType> positionAndDeviceType) {
      machineUtils.writeLockMachineAndApply(vmName, new ApplyBootOrderToMachine(positionAndDeviceType));
   }

   private void ensureMachineHasMemory(String vmName, final long memorySize) {
      machineUtils.writeLockMachineAndApply(vmName, new ApplyMemoryToMachine(memorySize));
   }

   public void ensureMachineHasStorageControllerNamed(String vmName, StorageController storageController) {
      machineUtils.writeLockMachineAndApply(vmName,
               new AddIDEControllerIfNotExists(checkNotNull(storageController, "storageController can't be null")));
   }
}
