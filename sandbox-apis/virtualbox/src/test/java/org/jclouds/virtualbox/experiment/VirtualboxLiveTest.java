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
import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
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
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

@Test(groups = "live", testName = "virtualbox.VirtualboxLiveTest")
public class VirtualboxLiveTest {

   private String provider = "virtualbox";
   private String identity;
   private String credential;
   private URI endpoint;
   private String vmName;

   private VirtualBoxManager manager = VirtualBoxManager.createInstance("");

   private String settingsFile;
   private String osTypeId;
   private String vmId;
   private boolean forceOverwrite;
   private int numberOfVirtualMachine;
   private ComputeServiceContext context;

   private String hostId = "host";
   private String adminNodeName;

   private Logger logger() {
      return context.utils().loggerFactory().getLogger("jclouds.compute");
   }

   private void setupCredentials() {
      identity = System.getProperty("test." + provider + ".identity", "administrator");
      credential = System.getProperty("test." + provider + ".credential", "12345");
      endpoint = URI.create(System.getProperty("test." + provider + ".endpoint", "http://localhost:18083/"));
   }

   private void setupConfigurationProperties() {
      // VBOX
      settingsFile = null;
      osTypeId = System.getProperty("test." + provider + ".osTypeId", "");
      vmId = System.getProperty("test." + provider + ".vmId", null);
      forceOverwrite = true;
      // OS specific information
      adminNodeName = System.getProperty("test." + provider + ".adminnodename", "jclouds-virtualbox-kickstart-admin");
      vmName = checkNotNull(System.getProperty("test." + provider + ".vmname", "jclouds-virtualbox-node"));
      numberOfVirtualMachine = Integer.parseInt(checkNotNull(System.getProperty("test." + provider
               + ".numberOfVirtualMachine", "1")));
   }

   @BeforeGroups(groups = "live")
   public void setupClient() throws Exception {
      context = TestUtils.computeServiceForLocalhostAndGuest();
      setupCredentials();
      setupConfigurationProperties();
      if (!new InetSocketAddressConnect().apply(new IPSocket(endpoint.getHost(), endpoint.getPort())))
         startupVboxWebServer();
   }

   @BeforeMethod
   public void setupManager() throws RemoteException, MalformedURLException {
      manager.connect(endpoint.toASCIIString(), identity, credential);
   }

   @AfterMethod
   public void disconnectAndClenaupManager() throws RemoteException, MalformedURLException {
      manager.disconnect();
      manager.cleanup();
   }

   @Test
   public void testStartAndValidateVirtualMachines() throws InterruptedException {
      for (int i = 1; i < numberOfVirtualMachine + 1; i++) {
         createVirtualMachine(i);
      }
   }

   private void createVirtualMachine(int i) throws InterruptedException {
      String instanceName = vmName + "_" + i;
      IMachine adminNode = manager.getVBox().findMachine(adminNodeName);

      IMachine clonedVM = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);
      IProgress progress = adminNode.getCurrentSnapshot().getMachine().cloneTo(clonedVM, CloneMode.MachineState,
               options);

      if (progress.getCompleted())
         logger().debug("clone done");

      manager.getVBox().registerMachine(clonedVM);

      ISession session = manager.getSessionObject();
      clonedVM.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();

      mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.Bridged);
      String mac_address = manager.getVBox().getHost().generateMACAddress();
      System.out.println("mac_address " + mac_address);
      mutable.getNetworkAdapter(new Long(0)).setMACAddress(mac_address);

      if (isOSX(hostId)) {
         mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(findBridgeInUse());
      } else {
         mutable.getNetworkAdapter(new Long(0)).setBridgedInterface("virbr0");
      }
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
      mutable.saveSettings();
      session.unlockMachine();

      System.out.println("\nLaunching VM named " + clonedVM.getName() + " ...");
      launchVMProcess(clonedVM, manager.getSessionObject());

      clonedVM = manager.getVBox().findMachine(instanceName);
      String macAddressOfClonedVM = clonedVM.getNetworkAdapter(new Long(0)).getMACAddress();

      int offset = 0, step = 2;
      for (int j = 1; j <= 5; j++) {
         macAddressOfClonedVM = new StringBuffer(macAddressOfClonedVM).insert(j * step + offset, ":").toString()
                  .toLowerCase();
         offset++;
      }

      String simplifiedMacAddressOfClonedVM = macAddressOfClonedVM;

      if (isOSX(hostId)) {
         if (simplifiedMacAddressOfClonedVM.contains("00"))
            simplifiedMacAddressOfClonedVM = new StringBuffer(simplifiedMacAddressOfClonedVM).delete(
                     simplifiedMacAddressOfClonedVM.indexOf("00"), simplifiedMacAddressOfClonedVM.indexOf("00") + 1)
                     .toString();

         if (simplifiedMacAddressOfClonedVM.contains("0"))
            if (simplifiedMacAddressOfClonedVM.indexOf("0") + 1 != ':'
                     && simplifiedMacAddressOfClonedVM.indexOf("0") - 1 != ':')
               simplifiedMacAddressOfClonedVM = new StringBuffer(simplifiedMacAddressOfClonedVM).delete(
                        simplifiedMacAddressOfClonedVM.indexOf("0"), simplifiedMacAddressOfClonedVM.indexOf("0") + 1)
                        .toString();
      }

      // TODO as we don't know the hostname (nor the IP address) of the cloned machine we can't use
      // "ssh check" to check that the machine is up and running

      // we need to find another way to check the machine is up: only at that stage a new IP would
      // be used by the machine and arp will answer correctly
      Thread.sleep(35000); // TODO to be removed asap
      runScriptOnNode(hostId, "for i in $(seq 1 254) ; do ping -c 1 -t 1 192.168.122.$i & done", runAsRoot(false)
               .wrapInInitScript(false));
      String arpLine = runScriptOnNode(hostId, "arp -an | grep " + simplifiedMacAddressOfClonedVM,
               runAsRoot(false).wrapInInitScript(false)).getOutput();
      String ipAddress = arpLine.substring(arpLine.indexOf("(") + 1, arpLine.indexOf(")"));
      System.out.println("IP address " + ipAddress);

      // TODO we need to redifine guest node at runtinme: in particular hostnmane and ssh port
      // runScriptOnNode(guestId, "echo ciao");
   }

   /**
	 * 
	 */
   private String findBridgeInUse() {
      // network
      String hostInterface = null;
      String command = "vboxmanage list bridgedifs";
      try {
         Process child = Runtime.getRuntime().exec(command);
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(child.getInputStream()));
         String line = "";
         boolean found = false;

         while ((line = bufferedReader.readLine()) != null && !found) {
            System.out.println("- " + line);
            if (line.split(":")[0].contains("Name")) {
               hostInterface = line.substring(line.indexOf(":") + 1);
            }
            if (line.split(":")[0].contains("Status") && line.split(":")[1].contains("Up")) {
               found = true;
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return hostInterface.trim();
   }

   private void launchVMProcess(IMachine machine, ISession session) {
      IProgress prog = machine.launchVMProcess(session, "gui", "");
      prog.waitForCompletion(-1);
      session.unlockMachine();
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

   private ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      ExecResponse toReturn = context.getComputeService().runScriptOnNode(nodeId, command, options);
      assert toReturn.getExitCode() == 0 : toReturn;
      return toReturn;
   }

   private boolean isOSX(String id) {
      return context.getComputeService().getNodeMetadata(hostId).getOperatingSystem().getDescription().equals(
               "Mac OS X");
   }

   private void startupVboxWebServer() {
      logger().debug("disabling password access");
      runScriptOnNode(hostId, "VBoxManage setproperty websrvauthlibrary null", runAsRoot(false).wrapInInitScript(false));
      logger().debug("starting vboxwebsrv");
      String vboxwebsrv = "vboxwebsrv -t 10000 -v -b";
      if (isOSX(hostId))
         vboxwebsrv = "cd /Applications/VirtualBox.app/Contents/MacOS/ && " + vboxwebsrv;

      runScriptOnNode(hostId, vboxwebsrv, runAsRoot(false).wrapInInitScript(false).blockOnPort(endpoint.getPort(), 10)
               .blockOnComplete(false).nameTask("vboxwebsrv"));
   }
}