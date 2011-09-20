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
package org.jclouds.virtualbox.experiment;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NetworkAdapterType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

@Test(groups = "live", testName = "virtualbox.VirtualboxLiveTest")
public class VirtualboxLiveTest {

	protected String provider = "virtualbox";
	protected String identity;
	protected String credential;
	protected URI endpoint;
	protected String apiversion;
	protected String vmName;

	VirtualBoxManager manager = VirtualBoxManager.createInstance("");

	protected Injector injector;
	protected Predicate<IPSocket> socketTester;
	protected SshClient.Factory sshFactory;

	protected String osUsername;
	protected String osPassword;
	protected String controller;
	protected String diskFormat;

	protected String settingsFile; 
	protected String osTypeId; 
	protected String vmId; 
	protected boolean forceOverwrite;
	protected String workingDir;
	protected String clonedDiskPath;
	protected String format = "vdi";
	protected int numberOfVirtualMachine;
	protected String originalDisk;
	private String clonedDisk;
	private ComputeServiceContext context; 

	private String hostId = "host";
	private String guestId = "guest";
	private String majorVersion;
	private String minorVersion;
	private String apiVersion;
	private String adminNodeName;
	private String snapshotDescription;
	private String originalDiskPath;

	protected Logger logger() {
		return context.utils().loggerFactory().getLogger("jclouds.compute");
	}

	protected void setupCredentials() {
		identity = System.getProperty("test." + provider + ".identity",
				"administrator");
		credential = System.getProperty("test." + provider + ".credential",
				"12345");
		endpoint = URI.create(System.getProperty("test." + provider
				+ ".endpoint", "http://localhost:18083/"));
		apiVersion = System.getProperty("test." + provider + ".apiversion",
				"4.1.2r73507");
		majorVersion = Iterables.get(Splitter.on('r').split(apiVersion), 0);
		minorVersion = Iterables.get(Splitter.on('r').split(apiVersion), 1);
	}

	protected void setupConfigurationProperties() {
		// VBOX
		settingsFile = null; 
		osTypeId = System.getProperty("test." + provider + ".osTypeId", "");
		vmId = System.getProperty("test." + provider + ".vmId", null); 
		forceOverwrite = true;
		// OS specific information
		adminNodeName = System.getProperty("test." + provider + ".adminnodename", "jclouds-virtualbox-kickstart-admin");
		vmName = checkNotNull(System.getProperty("test." + provider + ".vmname", "jclouds-virtualbox-node"));
		osUsername = System.getProperty("test." + provider + ".osusername", "toor");
		osPassword = System.getProperty("test." + provider + ".ospassword", "password");
		controller = System.getProperty("test." + provider + ".controller", "IDE Controller");
		diskFormat = System.getProperty("test." + provider + ".diskformat", "");

		workingDir = System.getProperty("user.home")
				+ File.separator
				+ System.getProperty("test." + provider + ".workingDir",
						"jclouds-virtualbox-test");

		originalDisk = System.getProperty("test." + provider + ".originalDisk", "admin.vdi");
		originalDiskPath = workingDir + File.separator + originalDisk;

		clonedDisk = System.getProperty("test." + provider + ".clonedDisk", "clone.vdi");
		clonedDiskPath = workingDir + File.separator + clonedDisk;
		numberOfVirtualMachine = Integer.parseInt(checkNotNull(System.getProperty("test." + provider
				+ ".numberOfVirtualMachine", "1")));
	}

	@BeforeGroups(groups = "live")
	protected void setupClient() throws Exception {
		context = TestUtils.computeServiceForLocalhost();
		socketTester = new RetryablePredicate<IPSocket>(
				new InetSocketAddressConnect(), 130, 10, TimeUnit.SECONDS);
		setupCredentials();
		setupConfigurationProperties();
		if (!new InetSocketAddressConnect().apply(new IPSocket(endpoint.getHost(), endpoint.getPort())))
			startupVboxWebServer();
	}


	@BeforeMethod
	protected void setupManager() throws RemoteException, MalformedURLException {
		manager.connect(endpoint.toASCIIString(), identity, credential);
	}

	@AfterMethod
	protected void disconnectAndClenaupManager() throws RemoteException, MalformedURLException {
		manager.disconnect();
		manager.cleanup();
	}

	@Test
	public void testStartAndValidateVirtualMachines() throws InterruptedException {
		for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
			createAndLaunchVirtualMachine(i);
		}
	}		

	private void createAndLaunchVirtualMachine(int i) throws InterruptedException {
		String instanceName = vmName + "_" + i;
		IMachine adminNode = manager.getVBox().findMachine(adminNodeName);
		
		IMachine clonedVM = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);
		List<CloneOptions> options = new ArrayList<CloneOptions>();
		options.add(CloneOptions.Link);
		IProgress progress = adminNode.getCurrentSnapshot().getMachine().cloneTo(clonedVM,CloneMode.MachineState , options);
		//IProgress progress = adminNode.cloneTo(clonedVM,CloneMode.MachineState , options);
		
		if(progress.getCompleted())
			logger().debug("clone done");

		manager.getVBox().registerMachine(clonedVM);
		
		ISession session = manager.getSessionObject();
		clonedVM.lockMachine(session, LockType.Write);
		IMachine mutable = session.getMachine();
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

		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.Bridged);
		mutable.getNetworkAdapter(new Long(0)).setAdapterType(NetworkAdapterType.Am79C973);
		mutable.getNetworkAdapter(new Long(0)).setMACAddress(manager.getVBox().getHost().generateMACAddress());
		mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
		mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
		mutable.saveSettings();
		session.unlockMachine();

		
		System.out.println("\nLaunching VM named " + clonedVM.getName() + " ...");
		launchVMProcess(clonedVM, manager.getSessionObject());
		String ipAddress = null;
		while (ipAddress == null || ipAddress.equals("")) {
			try {
				ipAddress = clonedVM.getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(ipAddress + " is the IP address of " + clonedVM.getName());
		
		//TODO
		// IPSocket socket = new IPSocket(ipAddress, 22);
		// checkSSH(IPSocket socket)

	}

	private void launchVMProcess(IMachine machine, ISession session) {
		IProgress prog = machine.launchVMProcess(session, "gui", "");
		prog.waitForCompletion(-1);
		session.unlockMachine();
	}

	protected void checkSSH(IPSocket socket) {
		socketTester.apply(socket);
		SshClient client = sshFactory.create(socket, new Credentials(osUsername, osPassword));
		try {
			client.connect();
			ExecResponse exec = client.exec("touch /tmp/hello_" + System.currentTimeMillis());
			exec = client.exec("echo hello");
			System.out.println(exec);
			assertEquals(exec.getOutput().trim(), "hello");
		} finally {
			if (client != null)
				client.disconnect();
		}
	}

	@Test(dependsOnMethods = "testStartAndValidateVirtualMachines")
	public void testStopVirtualMachines() {
		for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
			String instanceName = vmName + "_" + i;
			IMachine machine = manager.getVBox().findMachine(instanceName);

			try {
				ISession machineSession = manager.openMachineSession(machine);
				IProgress progress = machineSession.getConsole().powerDown();
				progress.waitForCompletion(-1);
				machineSession.unlockMachine();

				while (!machine.getSessionState().equals(SessionState.Unlocked)) {
					try {
						System.out.println("waiting for unlocking session - session state: " + machine.getSessionState());
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				assertEquals(machine.getState(), MachineState.PoweredOff);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

	protected boolean isOSX(String id) {
		return context.getComputeService().getNodeMetadata(hostId)
				.getOperatingSystem().getDescription().equals("Mac OS X");
	}

	void startupVboxWebServer() {
		logger().debug("disabling password access");
		runScriptOnNode(hostId, "VBoxManage setproperty websrvauthlibrary null", runAsRoot(false).wrapInInitScript(false));
		logger().debug("starting vboxwebsrv");
		String vboxwebsrv = "vboxwebsrv -t 10000 -v -b";
		if (isOSX(hostId))
			vboxwebsrv = "cd /Applications/VirtualBox.app/Contents/MacOS/ && "
					+ vboxwebsrv;

		runScriptOnNode(
				hostId,
				vboxwebsrv,
				runAsRoot(false).wrapInInitScript(false)
				.blockOnPort(endpoint.getPort(), 10)
				.blockOnComplete(false)
				.nameTask("vboxwebsrv"));
	}
}