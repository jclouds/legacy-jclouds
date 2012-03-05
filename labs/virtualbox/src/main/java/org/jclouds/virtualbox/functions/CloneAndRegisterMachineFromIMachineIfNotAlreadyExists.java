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
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

/**
 * CloneAndRegisterMachineFromIMachineIfNotAlreadyExists will take care of the
 * followings: - cloning the master - register the clone machine -
 * 
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements
		Function<CloneSpec, IMachine> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final Supplier<VirtualBoxManager> manager;
	private final String workingDir;
	private final MachineUtils machineUtils;
	
	@Inject
	public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
			Supplier<VirtualBoxManager> manager,
			@Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR) String workingDir,
			MachineUtils machineUtils) {
		this.manager = manager;
		this.workingDir = workingDir;
		this.machineUtils = machineUtils;
	}

	@Override
	public IMachine apply(CloneSpec cloneSpec) {
		VmSpec vmSpec = cloneSpec.getVmSpec();
		try {
			manager.get().getVBox().findMachine(vmSpec.getVmName());
			throw new IllegalStateException("Machine " + vmSpec.getVmName()
					+ " is already registered.");
		} catch (VBoxException e) {
			if (machineNotFoundException(e))
				return cloneMachine(cloneSpec);
			else
				throw e;
		}
	}

	private boolean machineNotFoundException(VBoxException e) {
		return e.getMessage().contains(
				"VirtualBox error: Could not find a registered machine named ")
				|| e.getMessage().contains(
						"Could not find a registered machine with UUID {");
	}

	private IMachine cloneMachine(CloneSpec cloneSpec) {
	  VmSpec vmSpec = cloneSpec.getVmSpec();
	  boolean isLinkedClone = cloneSpec.isLinked();
	  IMachine master = cloneSpec.getMaster();
		String settingsFile = manager.get().getVBox()
				.composeMachineFilename(vmSpec.getVmName(), workingDir);
		IMachine clonedMachine = manager
				.get()
				.getVBox()
				.createMachine(settingsFile, vmSpec.getVmName(),
						vmSpec.getOsTypeId(), vmSpec.getVmId(),
						vmSpec.isForceOverwrite());
		List<CloneOptions> options = new ArrayList<CloneOptions>();
		if (isLinkedClone)
			options.add(CloneOptions.Link);

		// TODO snapshot name
		ISnapshot currentSnapshot = new TakeSnapshotIfNotAlreadyAttached(
				manager, "snapshotName", "snapshotDesc").apply(master);

		// clone
		IProgress progress = currentSnapshot.getMachine().cloneTo(
				clonedMachine, CloneMode.MachineState, options);

		progress.waitForCompletion(-1);
		logger.debug("clone done");

		// registering
		manager.get().getVBox().registerMachine(clonedMachine);

		// Bridged#
//		for (NetworkInterfaceCard nic : cloneSpec.getNetworkSpec().getNetworkInterfaceCards()){
//			ensureBridgedNetworkingIsAppliedToMachine(clonedMachine.getName(), nic);
//		}
		
		return clonedMachine;
	}

	private void ensureBridgedNetworkingIsAppliedToMachine(String vmName,
			NetworkInterfaceCard nic) {
		
		machineUtils.writeLockMachineAndApply(vmName,
				new AttachBridgedAdapterToMachine(nic));
	}
}
