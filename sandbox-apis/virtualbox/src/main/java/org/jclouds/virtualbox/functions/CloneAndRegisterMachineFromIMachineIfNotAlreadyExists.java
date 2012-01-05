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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.PropertyUtils;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * CloneAndRegisterMachineFromIMachineIfNotAlreadyExists will take care of the
 * followings: - cloning the master - register the clone machine -
 *
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements Function<IMachine, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private VirtualBoxManager manager;
   private VmSpec vmSpec;
   boolean isLinkedClone;
   
   @Inject
   public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
           VirtualBoxManager manager, VmSpec vmSpec, boolean isLinkedClone) {
      this.manager = manager;
      this.vmSpec = vmSpec;
      this.isLinkedClone = isLinkedClone;
   }

   @Override
   public IMachine apply(@Nullable IMachine master) {
      try {
         manager.getVBox().findMachine(vmSpec.getVmName());
         throw new IllegalStateException("Machine " + vmSpec.getVmName() + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return cloneMachine(vmSpec, master);
         else
            throw e;
      }
   }

   private boolean machineNotFoundException(VBoxException e) {
      return e.getMessage().contains("VirtualBox error: Could not find a registered machine named ");
   }

   private IMachine cloneMachine(VmSpec vmSpec, IMachine master) {
      String workingDir = PropertyUtils.getWorkingDirFromProperty();
      String settingsFile = manager.getVBox().composeMachineFilename(vmSpec.getVmName(), workingDir);
      IMachine clonedMachine = manager.getVBox().createMachine(settingsFile, vmSpec.getVmName(), vmSpec.getOsTypeId(), vmSpec.getVmId(), vmSpec.isForceOverwrite());
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      if(isLinkedClone)
         options.add(CloneOptions.Link);

      // TODO snapshot name
      ISnapshot currentSnapshot = new TakeSnapshotIfNotAlreadyAttached(manager, "snapshotName", "snapshotDesc").apply(master);

      // clone
      IProgress progress = currentSnapshot.getMachine().cloneTo(clonedMachine, CloneMode.MachineState, options);

      if (progress.getCompleted())
         logger.debug("clone done");

      // registering
      manager.getVBox().registerMachine(clonedMachine);
      return clonedMachine;
   }
   
}
