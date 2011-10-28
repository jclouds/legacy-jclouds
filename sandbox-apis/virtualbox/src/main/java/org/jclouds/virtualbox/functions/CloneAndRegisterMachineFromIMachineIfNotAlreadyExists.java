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
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.virtualbox.functions.IsoToIMachine.lockMachineAndApply;
import static org.virtualbox_4_1.LockType.Write;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.BridgedInterface;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * @author Andrea Turli
 */
public class CloneAndRegisterMachineFromIMachineIfNotAlreadyExists implements Function<IMachine, IMachine> {

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

	@Inject
	public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
			VirtualBoxManager manager, ComputeServiceContext context,
			String settingsFile, String osTypeId, String vmId,
			boolean forceOverwrite, String cloneName, String hostId) {
		super();
		this.manager = manager;
		this.context = context;
		this.settingsFile = settingsFile;
		this.osTypeId = osTypeId;
		this.vmId = vmId;
		this.forceOverwrite = forceOverwrite;
		this.cloneName = cloneName;
		this.hostId = hostId;
	}

	@Override
	public IMachine apply(@Nullable IMachine master) {

		final IVirtualBox vBox = manager.getVBox();
		try {
			vBox.findMachine(cloneName);
			throw new IllegalStateException("Machine " + cloneName + " is already registered.");
		} catch (VBoxException e) {
			if (machineNotFoundException(e))
				return cloneMachine(vBox, cloneName, master);
			else
				throw e;
		}
	}

	private boolean machineNotFoundException(VBoxException e) {
		return e.getMessage().indexOf("VirtualBox error: Could not find a registered machine named ") != -1;
	}

	private IMachine cloneMachine(IVirtualBox vBox, String cloneName, IMachine master) {
		IMachine clonedMachine = manager.getVBox().createMachine(settingsFile, cloneName, osTypeId, vmId, forceOverwrite);
		List<CloneOptions> options = new ArrayList<CloneOptions>();
		options.add(CloneOptions.Link);

		ISession session = null;
		try {
			session = manager.openMachineSession(master);
			session.getConsole().takeSnapshot("test", "desc test");
		} catch (Exception e) {
			propogate(e);
		}
		session.unlockMachine();

		// clone
		IProgress progress = master.getCurrentSnapshot().getMachine().cloneTo(clonedMachine, CloneMode.MachineState,
				options);

		if (progress.getCompleted())
			logger.debug("clone done");

		// registering
		manager.getVBox().registerMachine(clonedMachine);
		
		// Bridged Network
		String command = "vboxmanage list bridgedifs";
		String bridgedIfs = runScriptOnNode(hostId, command, runAsRoot(false).wrapInInitScript(false)).getOutput();
		String hostInterface = retrieveAvailableBridgedInterfaceInfo(bridgedIfs);
		checkNotNull(hostInterface);
		String macAddress = manager.getVBox().getHost().generateMACAddress();
		ensureBridgedNetworkingIsAppliedToMachine(cloneName, macAddress, hostInterface);

		// TODO maybe need to notify outside about macaddress and network, useful on IMachineToIpAddress
		return clonedMachine;
	}

	/**
	 * @return hostInterface
	 */
	protected static String retrieveAvailableBridgedInterfaceInfo(String bridgedIfs) {
		List<BridgedInterface> bridgedInterfaces = new ArrayList<BridgedInterface>();
		String hostInterface = null;
		List<String> networkInfoBlocks = Lists.newArrayList();
		// separate the different bridge block
		for (String bridgedIf : Splitter.on(Pattern.compile("(?m)^[ \t]*\r?\n")).split(bridgedIfs)) {
			
			
			Iterable<String> block = Iterables.filter(Splitter.on("\n").split(bridgedIf), new Predicate<String>() {
				@Override
				public boolean apply(String arg0) {
					return arg0.startsWith("Name:") || arg0.startsWith("IPAddress:") || arg0.startsWith("Status:");
				}
			});
						
			
			networkInfoBlocks.add(Joiner.on(",").join(block));
		}
			/*
			Iterable<String> block = Splitter.on("\n").split(bridgedIf);
			if(!bridgedIf.isEmpty())
				bridgedInterfaces.add(new BridgedInterface(bridgedIf));
				
		}
		for (BridgedInterface bridgedInterface : bridgedInterfaces) {
			if(bridgedInterface.getStatus().equals("Up"))
				return bridgedInterface;
		}
		*/
		for (String networkInfoBlock : networkInfoBlocks) {
			if(!networkInfoBlock.isEmpty() && networkInfoBlock.contains("Up")) {
				Iterable<String> map = Splitter.on(",").split(networkInfoBlock);
				for (String key : map) {
					if(key.startsWith("Name:"))
						hostInterface = key.substring("Name:".length()).trim();
				}
			}
		}
		
		return hostInterface;
		
	}

	private void ensureBridgedNetworkingIsAppliedToMachine(String vmName, String macAddress, String hostInterface) {
		lockMachineAndApply(manager, Write, vmName, new AttachBridgedAdapterToMachine(0l, macAddress, hostInterface));
	}

	protected ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
		return context.getComputeService().runScriptOnNode(nodeId, command, options);
	}

   protected <T> T propogate(Exception e) {
      Throwables.propagate(e);
      assert false;
      return null;
   }
   
}
