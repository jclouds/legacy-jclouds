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

import com.google.common.base.Function;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

/**
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements Function<IMachine, IMachine> {
	
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private String settingsFile;
   private String osTypeId;
   private String vmId;
   private boolean forceOverwrite;
   private VirtualBoxManager manager;

   public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(String settingsFile, String osTypeId, String vmId,
                                                            boolean forceOverwrite, VirtualBoxManager manager) {
      this.settingsFile = settingsFile;
      this.osTypeId = osTypeId;
      this.vmId = vmId;
      this.forceOverwrite = forceOverwrite;
      this.manager = manager;
   }

   @Override
   public IMachine apply(@Nullable IMachine master) {
      String instanceName = master.getName() + "_1";

      final IVirtualBox vBox = manager.getVBox();
      try {
         vBox.findMachine(instanceName);
         throw new IllegalStateException("Machine " + instanceName + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return cloneMachine(vBox, instanceName, master);
         else
            throw e;
      }
   }

   private boolean machineNotFoundException(VBoxException e) {
      return e.getMessage().indexOf("VirtualBox error: Could not find a registered machine named ") != -1;
   }

   private IMachine cloneMachine(IVirtualBox vBox, String instanceName, IMachine master) {
      IMachine clonedMachine = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);
      IProgress progress = master.getCurrentSnapshot().getMachine().cloneTo(clonedMachine, CloneMode.MachineState,
               options);

      if (progress.getCompleted())
         logger.debug("clone done");

      manager.getVBox().registerMachine(clonedMachine);
      return clonedMachine;
   }
}
