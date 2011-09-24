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

package org.jclouds.virtualbox.experiment;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.jclouds.byon.Node;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.compute.LoadMachineFromVirtualBox;
import org.jclouds.virtualbox.functions.IMachineToIpAddress;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.virtualbox_4_1.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhost;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForVirtualBox;
import static org.testng.Assert.assertEquals;

public class VirtualBoxLiveTest2 {


   protected String provider = "virtualbox";
   protected String identity;
   protected String credential;
   protected URI endpoint;
   protected String vmName;
   private String hostId = "host";
   private String guestId = "guest";

   VirtualBoxManager manager = VirtualBoxManager.createInstance("");

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
   protected int numberOfVirtualMachine;
   protected String originalDisk;
   private String clonedDisk;
   private ComputeServiceContext virtualBoxComputeService;

   private String adminNodeName;
   private Injector injector;
   private Cache<String, Node> cache;
   private ComputeServiceContext localhostComputeService;

   protected Logger logger() {
      return virtualBoxComputeService.utils().loggerFactory().getLogger("jclouds.compute");
   }

   protected void setupCredentials() {
      identity = System.getProperty("test." + provider + ".identity",
              "administrator");
      credential = System.getProperty("test." + provider + ".credential",
              "12345");
      endpoint = URI.create(System.getProperty("test." + provider
              + ".endpoint", "http://localhost:18083/"));
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

      clonedDisk = System.getProperty("test." + provider + ".clonedDisk", "clone.vdi");
      clonedDiskPath = workingDir + File.separator + clonedDisk;
      numberOfVirtualMachine = Integer.parseInt(checkNotNull(System.getProperty("test." + provider
              + ".numberOfVirtualMachine", "3")));
      injector = new RestContextFactory().createContextBuilder(provider,
              ImmutableSet.<Module>of(new Log4JLoggingModule(), new SshjSshClientModule())).buildInjector();

      sshFactory = injector.getInstance(SshClient.Factory.class);
   }

   public static void main(String[] args) {
      VirtualBoxLiveTest2 virtualBoxLiveTest2 = new VirtualBoxLiveTest2();
      try {
         virtualBoxLiveTest2.runAll();
      } catch (IOException e) {
         virtualBoxLiveTest2.logger().error(e, "error", null);
      } catch (InterruptedException e) {
         virtualBoxLiveTest2.logger().error(e, "error", null);
      }
   }

   private void runAll() throws IOException, InterruptedException {
      localhostComputeService = computeServiceForLocalhost();
      IMachineToIpAddress iMachineToIpAddress = new IMachineToIpAddress(manager, localhostComputeService.getComputeService());
      LoadMachineFromVirtualBox cacheLoader = new LoadMachineFromVirtualBox(manager, iMachineToIpAddress);
      cache = CacheBuilder.newBuilder().build(cacheLoader);
      virtualBoxComputeService = computeServiceForVirtualBox(cache);
      socketTester = new RetryablePredicate<IPSocket>(
              new InetSocketAddressConnect(), 130, 10, TimeUnit.SECONDS);
      setupCredentials();
      setupConfigurationProperties();

      manager.connect(endpoint.toASCIIString(), identity, credential);

      createAndLaunchVirtualMachine(1);

      String instanceName = vmName + "_1";
      IMachine machine = manager.getVBox().findMachine(instanceName);
      logMachineStatus(machine);
      try {
         ISession machineSession = manager.openMachineSession(machine);
         IProgress progress = machineSession.getConsole().powerDown();
         progress.waitForCompletion(-1);
         machineSession.unlockMachine();
         while (!machine.getSessionState().equals(SessionState.Unlocked)) {
            try {
               logger().debug("Waiting for unlocking session - session state: " + machine.getSessionState());
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
         logMachineStatus(machine);
      } catch (Exception e) {
         e.printStackTrace();
      }


   }

   private void createAndLaunchVirtualMachine(int i) throws InterruptedException {
      String instanceName = vmName + "_" + i;
      IMachine adminNode = manager.getVBox().findMachine(adminNodeName);

      IMachine clonedVM = manager.getVBox().createMachine(settingsFile, instanceName, osTypeId, vmId, forceOverwrite);
      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);
      IProgress progress = adminNode.getCurrentSnapshot().getMachine().cloneTo(clonedVM, CloneMode.MachineState, options);

      if (progress.getCompleted())
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
               hostInterface = line.substring(line.indexOf(":") + 1);
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
      mutable.getNetworkAdapter(new Long(0)).setMACAddress("0800279DA478");
      mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
      mutable.saveSettings();
      session.unlockMachine();

      System.out.println("\nLaunching VM named " + clonedVM.getName() + " ...");
      launchVMProcess(clonedVM, manager.getSessionObject());

      clonedVM = manager.getVBox().findMachine(instanceName);



      try {
         boolean gotIp = false;
         while (!gotIp) {
            Node node = cache.get(instanceName);
            if (node.getHostname() != null) {
               gotIp = true;
            }
            else {
               cache.invalidate(node);
               Thread.sleep(1000);
               System.out.println("waiting for IP: got IP");
            }
         }

      } catch (ExecutionException e) {
      }

      logger().debug("Trying echo...");
      runScriptOnNode(instanceName, "echo ciao");

   }

   protected ExecResponse runScriptOnNode(String nodeId, String command) {
      return runScriptOnNode(nodeId, command, wrapInInitScript(false));
   }

   protected ExecResponse runScriptOnNode(String nodeId, String command,
                                          RunScriptOptions options) {
      ExecResponse toReturn = virtualBoxComputeService.getComputeService().runScriptOnNode(nodeId, command, options);
      assert toReturn.getExitCode() == 0 : toReturn;
      return toReturn;
   }

   private void checkSSH(IPSocket socket) {
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(osUsername, osPassword));
      logger().debug(client.toString());
      try {
         client.connect();
         ExecResponse exec = client.exec("echo hello");
         System.out.println(exec);
         assertEquals(exec.getOutput().trim(), "hello");
      } finally {
         if (client != null)
            client.disconnect();
      }

   }

   private void launchVMProcess(IMachine machine, ISession session) {
      IProgress prog = machine.launchVMProcess(session, "gui", "");
      prog.waitForCompletion(-1);
      session.unlockMachine();
   }

   private void logMachineStatus(IMachine machine) {
      NodeMetadata node = new IMachineToNodeMetadata().apply(machine);
      logger().debug("Machine status: " + node.toString());
   }

}
