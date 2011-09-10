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
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.MediumType;
import org.virtualbox_4_1.NetworkAdapterType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MediumState;
import org.virtualbox_4_1.jaxws.MediumVariant;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
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

   protected String settingsFile; // Fully qualified path where the settings
                                  // file should be created, or NULL for a
                                  // default
   // folder and file based on the name argument (see composeMachineFilename()).

   protected String osTypeId; // Guest OS Type ID.
   protected String vmId; // Machine UUID (optional).
   protected boolean forceOverwrite; // If true, an existing machine settings
                                     // file will be overwritten.

   protected String workingDir;
   protected String clonedDiskPath;

   // Create disk If the @a format attribute is empty or null then the default
   // storage format specified by ISystemProperties#defaultHardDiskFormat
   String format = "vdi";

   protected int numberOfVirtualMachine;
   protected String originalDisk;
private String originalDiskPath;
private String clonedDisk;
private IMedium clonedHd;
private ComputeServiceContext context; 

private String hostId = "host";
private String guestId = "guest";
private String majorVersion;
private String minorVersion;
private String apiVersion;

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
      settingsFile = null; // Fully qualified path where the settings file
                           // should be created, or NULL for a default
      // folder and file based on the name argument (see
      // composeMachineFilename()).
      osTypeId = System.getProperty("test." + provider + ".osTypeId", ""); // Guest
                                                                           // OS
                                                                           // Type
                                                                           // ID.
      vmId = System.getProperty("test." + provider + ".vmId", null); // Machine
                                                                     // UUID
                                                                     // (optional).
      forceOverwrite = true; // If true, an existing machine settings file will
                             // be overwritten.

      // OS specific information
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
		originalDiskPath = workingDir
				+ File.separator + originalDisk;
		
	  clonedDisk = System.getProperty("test." + provider + ".clonedDisk", "clone.vdi");
	  clonedDiskPath = workingDir + File.separator + clonedDisk;
	  
      numberOfVirtualMachine = Integer.parseInt(checkNotNull(System.getProperty("test." + provider
            + ".numberOfVirtualMachine", "2")));
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
   public void testCloneHardDisk() {
	      IMedium hd = manager.getVBox().openMedium(originalDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, forceOverwrite);
	      if (!new File(clonedDiskPath).exists()) {
	    	  clonedHd = manager.getVBox().createHardDisk(diskFormat, clonedDiskPath);
	         IProgress cloning = hd.cloneTo(clonedHd, new Long(
						MediumVariant.STANDARD.ordinal()), null);
	         cloning.waitForCompletion(-1);
	      } else
	         clonedHd = manager.getVBox().openMedium(clonedDiskPath, DeviceType.HardDisk, AccessMode.ReadWrite, forceOverwrite);

	      assertEquals(clonedHd.getId().equals(""), false);
	   }
   
	@Test(dependsOnMethods = "testCloneHardDisk")
   public void testStartVirtualMachines() {
      //IMedium clonedHd = cloneDisk(MediumType.MultiAttach);
      for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
         createVirtualMachine(i);
      }
   }
   
   private void createVirtualMachine(int i) {

      String instanceName = vmName + "_" + i;

      IMachine newVM = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);
      manager.getVBox().registerMachine(newVM);

      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(instanceName);
      machine.lockMachine(session, LockType.Write);
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

            if (line.split(":")[0].contains("Name")) {
               hostInterface = line.split(":")[1];
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
      // TODO: lookup translations for 4.1 for the below
      // mutable.getNetworkAdapter(new Long(0)).attachToBridgedInterface();
      // mutable.getNetworkAdapter(new
      // Long(0)).setHostInterface(hostInterface.trim());
		mutable.getNetworkAdapter(new Long(0)).setAttachmentType(
				NetworkAttachmentType.Bridged);
		mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
		mutable.saveSettings();      

// disk
		mutable.addStorageController(controller, StorageBus.IDE);
		mutable.saveSettings();      
		IMedium distroMedium = manager.getVBox().openMedium(
				clonedDiskPath, DeviceType.HardDisk,
				AccessMode.ReadWrite, forceOverwrite);
		mutable.attachDevice(controller, 0, 0, DeviceType.HardDisk, distroMedium);
		mutable.saveSettings();
      session.unlockMachine();
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

   @Test(dependsOnMethods = "testStartVirtualMachines")
   public void testSshLogin() {
      String ipAddress = null;
      for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
         String instanceName = vmName + "_" + i;
         IMachine machine = manager.getVBox().findMachine(instanceName);

         System.out.println("\nLaunching VM named " + machine.getName() + " ...");
         launchVMProcess(machine, manager.getSessionObject());

         while (ipAddress == null || ipAddress.equals("")) {
            try {
               ipAddress = machine.getGuestPropertyValue("/VirtualBox/GuestInfo/Net/0/V4/IP");
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         System.out.println("VM " + instanceName + " started with IP " + ipAddress);
         IPSocket socket = new IPSocket(ipAddress, 22);

         System.out.println("Check SSH for " + instanceName + " ...");
         checkSSH(socket);
      }
   }

   @Test(dependsOnMethods = "testSshLogin")
   public void testStopVirtualMachine() {
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