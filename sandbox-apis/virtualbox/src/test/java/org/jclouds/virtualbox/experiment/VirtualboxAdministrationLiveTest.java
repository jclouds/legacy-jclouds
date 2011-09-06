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

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IStorageController;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NATProtocol;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MediumVariant;

import com.google.common.base.Predicate;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Test(groups = "live", testName = "virtualbox.VirtualboxAdministrationTest")
public class VirtualboxAdministrationLiveTest {

   protected String provider = "virtualbox";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;
   protected String vmName;

   VirtualBoxManager manager = VirtualBoxManager.createInstance("");

   protected Injector injector;
   protected Predicate<IPSocket> socketTester;
   protected SshClient.Factory sshFactory;

   protected String settingsFile; // Fully qualified path where the settings
                                  // file should be created, or NULL for a
                                  // default
   // folder and file based on the name argument (see composeMachineFilename()).

   protected String osTypeId; // Guest OS Type ID.
   protected String vmId; // Machine UUID (optional).
   protected boolean forceOverwrite; // If true, an existing machine settings
                                     // file will be overwritten.

   protected String osUsername;
   protected String osPassword;
   protected String controller;
   protected String diskFormat;

   protected String workingDir;
   protected String originalDisk;
   protected String clonedDisk;

   protected String guestAdditionsDvdName;
   private String vdiUrl;
   private String gaIsoUrl;
   private String vboxwebsrvStartCommand;
   // private Process pr;
   private String vdiName;
   private String gaIsoName;
   private String admin_pwd;
   private String hostUsername;
   private String hostPassword;
   private String install7zip;
   private String run7zip;
   private String installVboxOse;

   /**
    * 
    * 
    * 
    * @param workingDir
    * @param vdiUrl
    * @param proxy
    *           Proxy proxy = new Proxy(Proxy.Type.HTTP, new
    *           InetSocketAddress("localhost", 5865));
    * @return
    * @throws Exception
    */
   private File downloadFile(String sourceURL, String destinationDir, String vboxGuestAdditionsName, Proxy proxy)
         throws Exception {

      String absolutePathName = destinationDir + File.separator + vboxGuestAdditionsName;
      File iso = new File(absolutePathName);

      final URL isoURL = new URL(sourceURL);
      final HttpURLConnection uc = (HttpURLConnection) isoURL.openConnection(); // isoURL.openConnection(proxy);
      uc.connect();
      if (!iso.exists()) {
         System.out.println("Start download " + sourceURL + " to " + absolutePathName);
         Files.copy(new InputSupplier<InputStream>() {

            @Override
            public InputStream getInput() throws IOException {
               return uc.getInputStream();
            }

         }, iso);
      }
      return iso;
   }

   protected void setupCredentials() {
      identity = System.getProperty("test." + provider + ".identity", "administrator");
      credential = System.getProperty("test." + provider + ".credential", "12345");
      endpoint = System.getProperty("test." + provider + ".endpoint", "http://localhost:18083/");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected void setupConfigurationProperties() {

      admin_pwd = System.getProperty("test." + provider + ".admin_pwd", "password");
      // OS
      osUsername = System.getProperty("test." + provider + ".osusername", "root");
      osPassword = System.getProperty("test." + provider + ".ospassword", "toortoor");
      controller = System.getProperty("test." + provider + ".controller", "IDE Controller");
      // Create disk If the @a format attribute is empty or null then the
      // default storage format specified by
      // ISystemProperties#defaultHardDiskFormat
      diskFormat = System.getProperty("test." + provider + ".diskformat", "");

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
      vmName = System.getProperty("test." + provider + ".vmname", "jclouds-virtualbox-admin");

      workingDir = System.getProperty("user.home") + File.separator
            + System.getProperty("test." + provider + ".workingDir", "jclouds-virtualbox-test");
      if (new File(workingDir).mkdir())
         ;
      vdiName = System.getProperty("test." + provider + ".vdiName", "centos-5.2-x86.7z");
      vdiUrl = System.getProperty("test." + provider + ".vdiUrl",
            "http://leaseweb.dl.sourceforge.net/project/virtualboximage/CentOS/5.2/centos-5.2-x86.7z");
      gaIsoName = System.getProperty("test." + provider + ".gaIsoName", "VBoxGuestAdditions_4.0.2-update-69551.iso");
      gaIsoUrl = System.getProperty("test." + provider + ".gaIsoUrl",
            "http://download.virtualbox.org/virtualbox/4.0.2/VBoxGuestAdditions_4.0.2-update-69551.iso");
      vboxwebsrvStartCommand = System
            .getProperty("test." + provider + ".vboxwebsrvStartCommand", "/usr/bin/vboxwebsrv");
      originalDisk = workingDir + File.separator + "VDI" + File.separator
            + System.getProperty("test." + provider + ".originalDisk", "centos-5.2-x86.vdi");
      clonedDisk = workingDir + File.separator + System.getProperty("test." + provider + ".clonedDisk", "template.vdi");
      guestAdditionsDvdName = workingDir
            + File.separator
            + System.getProperty("test." + provider + ".guestAdditionsDvdName",
                  "VBoxGuestAdditions_4.0.2-update-69551.iso");

      install7zip = System.getProperty("test." + provider + ".install7zip", "sudo -S apt-get --yes install p7zip");
      run7zip = System.getProperty("test." + provider + ".run7zip", "p7zip -d ");
      installVboxOse = System.getProperty("test." + provider + ".installvboxose",
            "sudo -S apt-get --yes install virtualbox-ose");
      if (!new File(originalDisk).exists()) {
         IPSocket socket = new IPSocket("127.0.0.1", 22);
         socketTester.apply(socket);
         SshClient client = sshFactory.create(socket, new Credentials(hostUsername, hostPassword));
         try {
            File vdi7z = downloadFile(vdiUrl, workingDir, vdiName, null);
            client.connect();
            ExecResponse exec = client.exec("echo " + admin_pwd + " | " + install7zip + "; cd " + workingDir + "; "
                  + run7zip + vdi7z.getName());
            System.out.println(exec);
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            if (client != null)
               client.disconnect();
         }
      }

      if (!new File(guestAdditionsDvdName).exists()) {
         try {
            File gaIso = downloadFile(gaIsoUrl, workingDir, gaIsoName, null);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @BeforeGroups(groups = "live")
   protected void setupClient() throws IOException, InterruptedException {

      hostUsername = System.getProperty("test." + provider + ".hostusername", "toor");
      hostPassword = System.getProperty("test." + provider + ".hostpassword", "password");

      injector = Guice.createInjector(new SshjSshClientModule(), new SLF4JLoggingModule(),
            new BouncyCastleCryptoModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 3600, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketTester);

      setupCredentials();
      setupConfigurationProperties();

      installVbox();
      // startup vbox web server
      startupVboxWebServer(vboxwebsrvStartCommand);
   }

   private void installVbox() throws IOException, InterruptedException {
      IPSocket socket = new IPSocket("127.0.0.1", 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(hostUsername, hostPassword));
      try {
         client.connect();
         ExecResponse exec = client.exec("echo " + hostPassword + " | " + installVboxOse);
         System.out.println(exec);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (client != null)
            client.disconnect();
      }

   }

   /**
    * 
    * @param command
    *           absolute path to command. For ubuntu 10.04: /usr/bin/vboxwebsrv
    * @throws IOException
    * @throws InterruptedException
    */
   private void startupVboxWebServer(String command) throws IOException, InterruptedException {
      // Disable login credential: $
      // rt.exec("VBoxManage setproperty websrvauthlibrary null");
      IPSocket socket = new IPSocket("127.0.0.1", 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(hostUsername, hostPassword));
      try {
         client.connect();
         ExecResponse exec = client.exec(command + " --timeout 50000 -b");
         System.out.println(exec.getOutput());
         System.out.println(exec);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (client != null)
            client.disconnect();
      }

   }

   @BeforeMethod
   protected void setupManager() {
      manager.connect(endpoint, identity, credential);
   }

   @AfterMethod
   protected void disconnectAndClenaupManager() throws RemoteException, MalformedURLException {
      manager.disconnect();
      manager.cleanup();
   }

   public void testCreateVirtualMachine() {
      IMachine newVM = manager.getVBox().createMachine(settingsFile, vmName, osTypeId, vmId, forceOverwrite);
      manager.getVBox().registerMachine(newVM);
      assertEquals(newVM.getName(), vmName);
   }

   @Test(dependsOnMethods = "testCreateVirtualMachine")
   public void testChangeRAM() {
      Long memorySize = new Long(2048);
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.setMemorySize(memorySize);
      mutable.saveSettings();
      session.unlockMachine();
      assertEquals(manager.getVBox().findMachine(vmName).getMemorySize(), memorySize);
   }

   @Test(dependsOnMethods = "testChangeRAM")
   public void testCreateDiskController() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.addStorageController(controller, StorageBus.IDE);
      mutable.saveSettings();
      session.unlockMachine();
      assertEquals(manager.getVBox().findMachine(vmName).getStorageControllers().size(), 1);
   }

   @Test(dependsOnMethods = "testCreateDiskController")
   public void testCloneAndAttachHardDisk() {
      IMedium hd = manager.getVBox().openMedium(originalDisk, DeviceType.HardDisk, AccessMode.ReadOnly, forceOverwrite);
      IMedium clonedHd = null;
      if (!new File(clonedDisk).exists()) {
         clonedHd = manager.getVBox().createHardDisk(diskFormat, clonedDisk);
         IProgress cloning = hd.cloneTo(clonedHd, new Long(MediumVariant.VMDK_SPLIT_2_G.ordinal()), null);
         cloning.waitForCompletion(-1);
      } else
         clonedHd = manager.getVBox().openMedium(clonedDisk, DeviceType.HardDisk, AccessMode.ReadOnly, forceOverwrite);

      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.attachDevice(controller, 0, 0, DeviceType.HardDisk, clonedHd);
      mutable.saveSettings(); // write settings to xml
      session.unlockMachine();
      assertEquals(hd.getId().equals(""), false);
   }

   @Test(dependsOnMethods = "testCloneAndAttachHardDisk")
   public void testConfigureNIC() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();

      /*
       * NAT
       */
      // mutable.getNetworkAdapter(new Long(0)).attachToNAT(); not in 4.1
      mutable.getNetworkAdapter(new Long(0)).setNATNetwork("");
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
      mutable.saveSettings();
      session.unlockMachine();

      machine.lockMachine(session, LockType.Write);
      mutable = session.getMachine();
      machine.getNetworkAdapter(new Long(0)).getNatDriver()
            .addRedirect("guestssh", NATProtocol.TCP, "127.0.0.1", 2222, "", 22);
      mutable.saveSettings();
      session.unlockMachine();
   }

   @Test(dependsOnMethods = "testConfigureNIC")
   public void testAttachGuestAdditions() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);

      IMedium guestAdditionsDVD = manager.getVBox().openMedium(guestAdditionsDvdName, DeviceType.DVD,
            AccessMode.ReadOnly, forceOverwrite);
      for (IStorageController storageController : machine.getStorageControllers()) {
         // for DVD we choose IDE
         if (storageController.getName().equals(controller)) {

            machine.lockMachine(session, LockType.Write);
            IMachine mutable = session.getMachine();

            // IDE secondary slave [1:1]
            mutable.attachDevice(storageController.getName(), new Integer(1), new Integer(1), DeviceType.DVD,
                  guestAdditionsDVD);
            mutable.saveSettings();
            session.unlockMachine();
         }
      }
   }

   @Test(dependsOnMethods = "testAttachGuestAdditions")
   public void testStartVirtualMachine() {
      IMachine machine = manager.getVBox().findMachine(vmName);
      ISession session = manager.getSessionObject();
      launchVMProcess(machine, session);
      assertEquals(machine.getState(), MachineState.Running);
   }

   /**
    * 
    * @param machine
    * @param session
    */
   private void launchVMProcess(IMachine machine, ISession session) {
      IProgress prog = machine.launchVMProcess(session, "gui", "");
      prog.waitForCompletion(-1);
      session.unlockMachine();
   }

   @Test(dependsOnMethods = "testStartVirtualMachine")
   public void testInstallGuestAdditionsThroughNATPortForwarding() {

      IPSocket socket = new IPSocket("127.0.0.1", 2222);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(osUsername, osPassword));
      try {
         client.connect();
         ExecResponse exec = client.exec("yum install gcc kernel kernel-devel -y");
         System.out.println(exec);
      } finally {
         if (client != null)
            client.disconnect();
      }

      // manually restart
      IMachine machine = manager.getVBox().findMachine(vmName);
      powerDownMachine(machine);
      launchVMProcess(machine, manager.getSessionObject());

      socketTester.apply(socket);
      client = sshFactory.create(socket, new Credentials(osUsername, osPassword));
      try {
         client.connect();
         ExecResponse exec = client
               .exec("mkdir -p /media/cdrom; mount /dev/cdrom /media/cdrom; sh /media/cdrom/VBoxLinuxAdditions.run --nox11 force");
         System.out.println(exec);
         exec = client.exec("echo '/usr/sbin/VBoxService' >> /etc/rc.d/rc.local");
         System.out.println(exec);
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @Test(dependsOnMethods = "testInstallGuestAdditionsThroughNATPortForwarding")
   public void testStopVirtualMachine() {
      IMachine machine = manager.getVBox().findMachine(vmName);
      powerDownMachine(machine);
      assertEquals(machine.getState(), MachineState.PoweredOff);
   }

   /**
    * @param machine
    */
   private void powerDownMachine(IMachine machine) {
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

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   @Test(dependsOnMethods = "testStopVirtualMachine")
   public void cleanUp() throws IOException {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.getNetworkAdapter(new Long(0)).getNatDriver().removeRedirect("guestssh");
      // detach disk from controller
      mutable.detachDevice(controller, 0, 0);
      mutable.saveSettings();
      session.unlockMachine();

      for (IStorageController storageController : machine.getStorageControllers()) {
         if (storageController.getName().equals(controller)) {
            session = manager.getSessionObject();
            machine.lockMachine(session, LockType.Write);

            mutable = session.getMachine();
            mutable.detachDevice(storageController.getName(), 1, 1);
            mutable.saveSettings();
            session.unlockMachine();
         }
      }
   }

   @AfterClass
   void stopVboxWebServer() throws IOException {
      // stop vbox web server
      IPSocket socket = new IPSocket("127.0.0.1", 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials(hostUsername, hostPassword));
      try {
         client.connect();
         ExecResponse exec = client.exec("pidof vboxwebsrv | xargs kill");
         System.out.println(exec.getOutput());

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (client != null)
            client.disconnect();
      }
   }
}