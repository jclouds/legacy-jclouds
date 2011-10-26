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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

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
	private String cloneName;


   public CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(String settingsFile, String osTypeId, String vmId,
                                                            boolean forceOverwrite, VirtualBoxManager manager,String cloneName) {
      this.settingsFile = settingsFile;
      this.osTypeId = osTypeId;
      this.vmId = vmId;
      this.forceOverwrite = forceOverwrite;
      this.manager = manager;
		this.cloneName = cloneName;

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
		try {
			Process child = Runtime.getRuntime().exec(command);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(child.getInputStream()));
			String line = "";
			boolean found = false;

			while ((line = bufferedReader.readLine()) != null && !found) {
				System.out.println("line: " + line);
				if (line.split(":")[0].contains("Name")) {
					hostInterface = line.substring(line.indexOf(":") +1);
				}
				if (line.split(":")[0].contains("Status") && line.split(":")[1].contains("Up")) {
					System.out.println("bridge: " + hostInterface.trim());
					found = true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		session = manager.getSessionObject();
		clonedMachine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		
		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.Bridged);
		mutable.getNetworkAdapter(new Long(0)).setAdapterType(NetworkAdapterType.Am79C973);
		mutable.getNetworkAdapter(new Long(0)).setMACAddress(manager.getVBox().getHost().generateMACAddress());
		mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
		mutable.saveSettings();
		session.unlockMachine();
      
      
      IProgress prog = clonedMachine.launchVMProcess(manager.getSessionObject(), "gui", "");
      prog.waitForCompletion(-1);
      return clonedMachine;
   }
}
