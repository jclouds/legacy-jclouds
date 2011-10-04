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

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.experiment.settings.KeyboardScancodes;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NATProtocol;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MediumVariant;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class IsoToIMachine implements Function<String, IMachine> {

	private VirtualBoxManager manager;
	private String adminDisk;
	private String diskFormat;
	private String settingsFile;
	private String vmName;
	private String osTypeId;
	private String vmId;
	private String controllerIDE;
	private boolean forceOverwrite;
	private ComputeServiceContext context;
	private String hostId;
	private String guestId;

	@Inject
	public IsoToIMachine(VirtualBoxManager manager, String adminDisk,
			String diskFormat, String settingsFile, String vmName,
			String osTypeId, String vmId, boolean forceOverwrite,
			String controllerIDE, ComputeServiceContext context, String hostId,
			String guestId) {
		super();
		this.manager = manager;
		this.adminDisk = adminDisk;
		this.diskFormat = diskFormat;
		this.settingsFile = settingsFile;
		this.vmName = vmName;
		this.osTypeId = osTypeId;
		this.vmId = vmId;
		this.controllerIDE = controllerIDE;
		this.forceOverwrite = forceOverwrite;
		this.context = context;
		this.hostId = hostId;
		this.guestId = guestId;
	}

	@Override
	public IMachine apply(@Nullable String isoName) {
		IMachine vm = manager.getVBox().createMachine(settingsFile, vmName,
				osTypeId, vmId, forceOverwrite);
		assertNotNull(vm.getName());

		IMedium distroMedium = manager.getVBox().openMedium(
				VirtualBoxConstants.VIRTUALBOX_WORKINGDIR + "/" + isoName,
				DeviceType.DVD, AccessMode.ReadOnly, forceOverwrite);

		ISession session = manager.getSessionObject();
		IMachine machine = manager.getVBox().findMachine(vmName);
		machine.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
		mutable.addStorageController(controllerIDE, StorageBus.IDE);
		mutable.saveSettings();

		// CONTROLLER
		mutable.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);
		mutable.saveSettings();

		// DISK
		IMedium hd = null;
		if (new File(adminDisk).exists()) {
			new File(adminDisk).delete();
		}
		hd = manager.getVBox().createHardDisk(diskFormat, adminDisk);
		long size = 4L * 1024L * 1024L * 1024L - 4L;
		hd.createBaseStorage(new Long(size),
				new Long(MediumVariant.STANDARD.ordinal()));
		mutable.attachDevice(controllerIDE, 0, 1, DeviceType.HardDisk, hd);
		mutable.saveSettings();
		
		// NIC
		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(
				NetworkAttachmentType.NAT);
		
		
		machine.getNetworkAdapter(new Long(0)).getNatDriver().addRedirect("guestssh", NATProtocol.TCP, "127.0.0.1", 2222, "", 22);
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
		mutable.saveSettings();

		launchVMProcess(machine, session);
		assertEquals(machine.getState(), MachineState.Running);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			propagate(e);
		}

		try {
			sendKeyboardSequence(VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE);
		} catch (InterruptedException e) {
			propagate(e);
		}

		session.unlockMachine();

		return vm;
	}

	private void launchVMProcess(IMachine machine, ISession session) {
		IProgress prog = machine.launchVMProcess(session, "gui", "");
		prog.waitForCompletion(-1);
		session.unlockMachine();
	}

	private void sendKeyboardSequence(String keyboardSequence)
			throws InterruptedException {
		String[] sequenceSplited = keyboardSequence.split(" ");
		StringBuilder sb = new StringBuilder();
		for (String line : sequenceSplited) {
			String converted = stringToKeycode(line);
			for (String word : converted.split("  ")) {
				sb.append("vboxmanage controlvm " + vmName
						+ " keyboardputscancode " + word + "; ");
				if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
						.get("<Enter>"))) {
					runScriptOnNode(hostId, sb.toString(), runAsRoot(false)
							.wrapInInitScript(false));
					sb.delete(0, sb.length() - 1);
				}
				if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
						.get("<Return>"))) {
					runScriptOnNode(hostId, sb.toString(), runAsRoot(false)
							.wrapInInitScript(false));
					sb.delete(0, sb.length() - 1);
				}

			}
		}
	}

	private String stringToKeycode(String s) {
		StringBuilder keycodes = new StringBuilder();
		if (s.startsWith("<")) {
			String[] specials = s.split("<");
			for (int i = 1; i < specials.length; i++) {
				keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
						.get("<" + specials[i]) + "  ");
			}
			return keycodes.toString();
		}

		int i = 0;
		while (i < s.length()) {
			String digit = s.substring(i, i + 1);
			String hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP
					.get(digit);
			keycodes.append(hex + " ");
			if (i != 0 && i % 14 == 0)
				keycodes.append(" ");
			i++;
		}
		keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP
				.get("<Spacebar>") + " ");

		return keycodes.toString();
	}

	protected ExecResponse runScriptOnNode(String nodeId, String command,
			RunScriptOptions options) {
		ExecResponse toReturn = context.getComputeService().runScriptOnNode(
				nodeId, command, options);
		assert toReturn.getExitCode() == 0 : toReturn;
		return toReturn;
	}

	protected ExecResponse runScriptOnNode(String nodeId, String command) {
		return runScriptOnNode(nodeId, command, wrapInInitScript(false));
	}

}
