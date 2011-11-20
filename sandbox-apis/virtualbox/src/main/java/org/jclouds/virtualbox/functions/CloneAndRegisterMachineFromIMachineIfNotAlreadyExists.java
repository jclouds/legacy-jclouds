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
import static org.jclouds.virtualbox.functions.IsoToIMachine.lockMachineAndApply;
import static org.virtualbox_4_1.LockType.Write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * 
 * CloneAndRegisterMachineFromIMachineIfNotAlreadyExists will take care of the
 * followings: - cloning the master - register the clone machine -
 * ensureBridgedNetworkingIsAppliedToMachine(cloneName, macAddress,
 * hostInterface)
 * 
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements
      Function<IMachine, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private VirtualBoxManager manager;
   private ComputeServiceContext context;
   private String settingsFile;
   private String osTypeId;
   private String vmId;
   private boolean forceOverwrite;
   private String cloneName;
   private String hostId;
   private String snapshotName;
   private String snapshotDesc;

   @Inject
   public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
         VirtualBoxManager manager, ComputeServiceContext context,
         String settingsFile, String osTypeId, String vmId,
         boolean forceOverwrite, String cloneName, String hostId,
         String snashotName, String snapshotDesc) {
      super();
      this.manager = manager;
      this.context = context;
      this.settingsFile = settingsFile;
      this.osTypeId = osTypeId;
      this.vmId = vmId;
      this.forceOverwrite = forceOverwrite;
      this.cloneName = cloneName;
      this.hostId = hostId;
      this.snapshotName = snashotName;
      this.snapshotDesc = snapshotDesc;
   }

   @Override
   public IMachine apply(@Nullable IMachine master) {

      final IVirtualBox vBox = manager.getVBox();
      try {
         vBox.findMachine(cloneName);
         throw new IllegalStateException("Machine " + cloneName
               + " is already registered.");
      } catch (VBoxException e) {
         if (machineNotFoundException(e))
            return cloneMachine(vBox, cloneName, master);
         else
            throw e;
      }
   }

   private boolean machineNotFoundException(VBoxException e) {
      return e.getMessage().indexOf(
            "VirtualBox error: Could not find a registered machine named ") != -1;
   }

   private IMachine cloneMachine(IVirtualBox vBox, String cloneName,
         IMachine master) {
      IMachine clonedMachine = manager.getVBox().createMachine(settingsFile,
            cloneName, osTypeId, vmId, forceOverwrite);
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);

      // takeSnapshotIfNotAlreadyExists
      ISnapshot currentSnapshot = new TakeSnapshotIfNotAlreadyAttached(manager,
            snapshotName, snapshotDesc).apply(master);

      // clone
      IProgress progress = currentSnapshot.getMachine().cloneTo(clonedMachine,
            CloneMode.MachineState, options);

      if (progress.getCompleted())
         logger.debug("clone done");

      // registering
      manager.getVBox().registerMachine(clonedMachine);

      // Bridged Network
      List<String> activeBridgedInterfaces = new RetrieveActiveBridgedInterfaces(
            context).apply(hostId);
      checkNotNull(activeBridgedInterfaces);
      String macAddress = manager.getVBox().getHost().generateMACAddress();

      // TODO discover among activeBridgedInterfaces the most likely to be used
      String bridgedInterface = activeBridgedInterfaces.get(0);
      ensureBridgedNetworkingIsAppliedToMachine(cloneName, macAddress,
            bridgedInterface);
      return clonedMachine;
   }

   private void ensureBridgedNetworkingIsAppliedToMachine(String vmName,
         String macAddress, String hostInterface) {
      lockMachineAndApply(manager, Write, vmName,
            new AttachBridgedAdapterToMachine(0l, macAddress, hostInterface));
   }

   protected ExecResponse runScriptOnNode(String nodeId, String command,
         RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command,
            options);
   }

   protected <T> T propagate(Exception e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }

}
