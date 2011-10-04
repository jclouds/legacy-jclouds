/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.JustProvider;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Throwables;
import com.google.inject.Singleton;

/**
 * Defines the connection between the {@link org.virtualbox_4_1.VirtualBoxManager} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}
 *
 * @author Mattias Holmqvist, Andrea Turli
 */
@Singleton
public class VirtualBoxComputeServiceAdapter implements ComputeServiceAdapter<IMachine, IMachine, IMachine, Location> {

	private final VirtualBoxManager manager;
   private final JustProvider justProvider;

	@Inject
	public VirtualBoxComputeServiceAdapter(VirtualBoxManager manager, JustProvider justProvider) {
		this.manager = checkNotNull(manager, "manager");
      this.justProvider = checkNotNull(justProvider, "justProvider");
	}

	@Override
	public IMachine createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template, Map<String, Credentials> credentialStore) {
		return null;
	}

	@Override
	public Iterable<IMachine> listNodes() {
		return Collections.emptyList();
	}
	
	@Override
	public Iterable<IMachine> listHardwareProfiles() {
		return manager.getVBox().getMachines();
	}

	@Override
	public Iterable<IMachine> listImages() {
		for (IMachine iMachine : manager.getVBox().getMachines()) {
		System.out.println(iMachine.getName());	
		}
		return manager.getVBox().getMachines();
	}

	@SuppressWarnings("unchecked")
   @Override
	public Iterable<Location> listLocations() {
      return (Iterable<Location>) justProvider.get();
	}

	@Override
	public IMachine getNode(String vmName) {
		return manager.getVBox().findMachine(vmName);
	}

	@Override
	public void destroyNode(String vmName) {
		IMachine machine = manager.getVBox().findMachine(vmName);
		powerDownMachine(machine);
		machine.unregister(CleanupMode.Full);
	}

	@Override
	public void rebootNode(String vmName) {
		IMachine machine = manager.getVBox().findMachine(vmName);
		powerDownMachine(machine);
		launchVMProcess(machine, manager.getSessionObject());
	}

	@Override
	public void resumeNode(String vmName) {
		IMachine machine = manager.getVBox().findMachine(vmName);
		ISession machineSession;
		try {
			machineSession = manager.openMachineSession(machine);
			machineSession.getConsole().resume();
			machineSession.unlockMachine();
		} catch (Exception e) {
			propogate(e);
		}
	}

	@Override
	public void suspendNode(String vmName) {
		IMachine machine = manager.getVBox().findMachine(vmName);
		ISession machineSession;
		try {
			machineSession = manager.openMachineSession(machine);
			machineSession.getConsole().pause();
			machineSession.unlockMachine();
		} catch (Exception e) {
			propogate(e);
		}
	}

	protected <T> T propogate(Exception e) {
		Throwables.propagate(e);
		assert false;
		return null;
	}
	
	private void launchVMProcess(IMachine machine, ISession session) {
		IProgress prog = machine.launchVMProcess(session, "gui", "");
		prog.waitForCompletion(-1);
		session.unlockMachine();
	}
	
	private void powerDownMachine(IMachine machine) {
		try {
			ISession machineSession = manager.openMachineSession(machine);
			IProgress progress = machineSession.getConsole().powerDown();
			progress.waitForCompletion(-1);
			machineSession.unlockMachine();

			while (!machine.getSessionState().equals(SessionState.Unlocked)) {
				try {
					System.out
					.println("waiting for unlocking session - session state: "
							+ machine.getSessionState());
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
		}
	}
}
