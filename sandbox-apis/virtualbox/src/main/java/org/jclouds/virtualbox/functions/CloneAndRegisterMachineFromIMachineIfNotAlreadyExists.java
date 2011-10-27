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


import static org.virtualbox_4_1.LockType.Write;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.virtualbox.functions.IsoToIMachine.lockMachineAndApply;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.NetworkAdapterType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      session.unlockMachine();

      IProgress progress = master.getCurrentSnapshot().getMachine().cloneTo(clonedMachine, CloneMode.MachineState,
               options);

      if (progress.getCompleted())
         logger.debug("clone done");

      manager.getVBox().registerMachine(clonedMachine);
      
		// network
		String hostInterface = null;
		String command = "vboxmanage list bridgedifs";

			String content = runScriptOnNode(hostId, command, runAsRoot(false).wrapInInitScript(false)).getOutput();
			/*
			Process child = Runtime.getRuntime().exec(command);
			InputStream stream = child.getInputStream();
			String content = CharStreams.toString(new InputStreamReader(stream));
			*/
			System.out.println(content);
			List<String> networkInfoBlocks = Lists.newArrayList();
			for (String s : Splitter.on("\n\n").split(content)) {
				Iterable<String> block = Iterables.filter(Splitter.on("\n").split(s), new Predicate<String>() {
					@Override
					public boolean apply(String arg0) {
						return arg0.startsWith("Name:") || arg0.startsWith("IPAddress:") || arg0.startsWith("Status:");
					}
				});
				networkInfoBlocks.add(Joiner.on(";").join(block));				
			}
			for (String networkInfoBlock : networkInfoBlocks) {
				if(!networkInfoBlock.isEmpty() && networkInfoBlock.contains("Up")) {
					Map<String, String> map = Splitter.on(",").withKeyValueSeparator(":").split(networkInfoBlock);
					for (String key : map.keySet()) {
						if(key.equals("Name"))
							hostInterface = map.get(key);
					}
				}
			}
				
		// Bridged Network
      ensureBridgedNetworkingIsAppliedToMachine(cloneName, manager.getVBox().getHost().generateMACAddress(), hostInterface);

      IProgress prog = clonedMachine.launchVMProcess(manager.getSessionObject(), "gui", "");
      prog.waitForCompletion(-1);
      return clonedMachine;
   }
   
   private void ensureBridgedNetworkingIsAppliedToMachine(String vmName, String macAddress, String hostInterface) {
      lockMachineAndApply(manager, Write, vmName, new AttachBridgedAdapterToMachine(0l, macAddress, hostInterface));
   }
   
   protected ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command, options);
   }
   
}
