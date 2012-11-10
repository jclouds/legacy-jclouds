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

import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_PASSWORD;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_USER;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_2.CloneMode;
import org.virtualbox_4_2.CloneOptions;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISnapshot;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * CloneAndRegisterMachineFromIMachineIfNotAlreadyExists will take care of the followings: - cloning
 * the master - register the clone machine.
 * 
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements Function<CloneSpec, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final String workingDir;
   private final MachineUtils machineUtils;
   
   @Inject
   public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(Supplier<VirtualBoxManager> manager,
            @Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR) String workingDir, MachineUtils machineUtils) {
      this.manager = manager;
      this.workingDir = workingDir;
      this.machineUtils = machineUtils;
   }

   @Override
   public IMachine apply(CloneSpec cloneSpec) {
      VmSpec vmSpec = cloneSpec.getVmSpec();
      try {
         manager.get().getVBox().findMachine(vmSpec.getVmName());
         throw new IllegalStateException("Machine " + vmSpec.getVmName() + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return cloneMachine(cloneSpec);
         else
            throw e;
      }
   }

   private boolean machineNotFoundException(VBoxException e) {
      return e.getMessage().contains("VirtualBox error: Could not find a registered machine named ")
               || e.getMessage().contains("Could not find a registered machine with UUID {");
   }

   private IMachine cloneMachine(CloneSpec cloneSpec) {
      VmSpec vmSpec = cloneSpec.getVmSpec();
      NetworkSpec networkSpec = cloneSpec.getNetworkSpec();
      boolean isLinkedClone = cloneSpec.isLinked();
      IMachine master = cloneSpec.getMaster();
      String flags = "";
      List<String> groups = ImmutableList.of();
      String group = "";
      String settingsFile = manager.get().getVBox().composeMachineFilename(vmSpec.getVmName(), group , flags , workingDir);
      IMachine clonedMachine = manager
               .get()
               .getVBox()
               .createMachine(settingsFile, vmSpec.getVmName(), groups, vmSpec.getOsTypeId(), flags);
      List<CloneOptions> options = Lists.newArrayList();
      if (isLinkedClone)
         options.add(CloneOptions.Link);

      ISnapshot currentSnapshot = new TakeSnapshotIfNotAlreadyAttached(manager,
            "snapshotName", "snapshotDesc", logger).apply(master);
      IProgress progress = currentSnapshot.getMachine().cloneTo(clonedMachine,
            CloneMode.MachineState, options);
      progress.waitForCompletion(-1);

      // memory may not be the same as the master vm
      clonedMachine.setMemorySize(cloneSpec.getVmSpec().getMemory());

      // registering
      manager.get().getVBox().registerMachine(clonedMachine);

      // Networking
      for (NetworkInterfaceCard networkInterfaceCard : networkSpec.getNetworkInterfaceCards()) {
         new AttachNicToMachine(vmSpec.getVmName(), machineUtils).apply(networkInterfaceCard);
      }
      
      // set only once the creds for this machine, same coming from its master
      logger.debug("<< storing guest credentials on vm(%s) as extra data", clonedMachine.getName());
      String masterUsername = master.getExtraData(GUEST_OS_USER);
      String masterPassword = master.getExtraData(GUEST_OS_PASSWORD);
      clonedMachine.setExtraData(GUEST_OS_USER, masterUsername);
      clonedMachine.setExtraData(GUEST_OS_PASSWORD, masterPassword);

      return clonedMachine;
   }
}
