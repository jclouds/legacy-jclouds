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

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.DefaultConfiguration;
import org.jclouds.ssh.SshException;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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
import org.virtualbox_4_1.NATProtocol;
import org.virtualbox_4_1.NetworkAdapterType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MediumVariant;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "virtualbox.VirtualboxAdministrationKickstartTest")
public class VirtualboxAdministrationKickstartLiveTest extends BaseVirtualBoxClientLiveTest {

   private String hostId = "host"; // TODO: shared between classes; move to iTestContext
   private String guestId = "guest"; // TODO: ^^
   private String distroIsoName; // TODO: ^^
   private String workingDir;// TODO: ^^

   private String settingsFile; // Fully qualified path where the settings
   private String keyboardSequence;

   private String vmId; // Machine UUID
   private String vmName;
   private String controllerIDE;
   private String osTypeId; // Guest OS Type ID.
   private boolean forceOverwrite;
   private String diskFormat;
   private String adminDisk;
   private String guestAdditionsDvd;
   private String snapshotDescription;

   private VirtualBoxManager manager = VirtualBoxManager.createInstance("");

   protected boolean isUbuntu(String id) {
      return context.getComputeService().getNodeMetadata(id).getOperatingSystem().getDescription().contains("ubuntu");
   }

   @BeforeMethod
   protected void setupManager() {
      manager.connect(endpoint, identity, credential);
   }

   @BeforeClass
   void setupConfigurationProperties() {

      controllerIDE = System.getProperty("test." + provider + ".controllerIde", "IDE Controller");
      diskFormat = System.getProperty("test." + provider + ".diskformat", "");

      // VBOX
      settingsFile = null;
      osTypeId = System.getProperty("test." + provider + ".osTypeId", "");
      vmId = System.getProperty("test." + provider + ".vmId", null);
      forceOverwrite = true;
      vmName = System.getProperty("test." + provider + ".vmname", "jclouds-virtualbox-kickstart-admin");

      workingDir = System.getProperty("user.home") + File.separator
               + System.getProperty("test." + provider + ".workingDir", "jclouds-virtualbox-test");
      if (new File(workingDir).mkdir())
         ;
      distroIsoName = System.getProperty("test." + provider + ".distroIsoName", "ubuntu-11.04-server-i386.iso");
      adminDisk = workingDir + File.separator + System.getProperty("test." + provider + ".adminDisk", "admin.vdi");
      String majorVersion = Iterables.get(Splitter.on('r').split(apiversion), 0);
      guestAdditionsDvd = workingDir
               + File.separator
               + System.getProperty("test." + provider + ".guestAdditionsDvd", "VBoxGuestAdditions_" + majorVersion
                        + ".iso");
      snapshotDescription = System
               .getProperty("test." + provider + "snapshotdescription", "jclouds-virtualbox-snaphot");

      keyboardSequence = System.getProperty("test." + provider + ".keyboardSequence", "<Esc><Esc><Enter> "
               + "/install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg "
               + "debian-installer=en_US auto locale=en_US kbd-chooser/method=us " + "hostname=" + vmName + " "
               + "fb=false debconf/frontend=noninteractive "
               + "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
               + "initrd=/install/initrd.gz -- <Enter>");

   }

   public void sendKeyboardSequence(String keyboardSequence) throws InterruptedException {
      String[] sequenceSplited = keyboardSequence.split(" ");
      StringBuilder sb = new StringBuilder();
      for (String line : sequenceSplited) {
         String converted = stringToKeycode(line);
         for (String word : converted.split("  ")) {
            sb.append("vboxmanage controlvm " + vmName + " keyboardputscancode " + word + "; ");

            if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Enter>"))) {
               runScriptOnNode(hostId, sb.toString(), runAsRoot(false).wrapInInitScript(false));
               sb.delete(0, sb.length() - 1);
            }

            if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Return>"))) {
               runScriptOnNode(hostId, sb.toString(), runAsRoot(false).wrapInInitScript(false));
               sb.delete(0, sb.length() - 1);
            }

         }
      }
   }

   public ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      ExecResponse toReturn = context.getComputeService().runScriptOnNode(nodeId, command, options);
      assert toReturn.getExitCode() == 0 : toReturn;
      return toReturn;
   }

   public ExecResponse runScriptOnNode(String nodeId, String command) {
      return runScriptOnNode(nodeId, command, wrapInInitScript(false));
   }

   public String stringToKeycode(String s) {
      StringBuilder keycodes = new StringBuilder();
      if (s.startsWith("<")) {
         String[] specials = s.split("<");
         for (int i = 1; i < specials.length; i++) {
            keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<" + specials[i]) + "  ");
         }
         return keycodes.toString();
      }

      int i = 0;
      while (i < s.length()) {
         String digit = s.substring(i, i + 1);
         String hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP.get(digit);
         keycodes.append(hex + " ");
         if (i != 0 && i % 14 == 0)
            keycodes.append(" ");
         i++;
      }
      keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Spacebar>") + " ");

      return keycodes.toString();
   }

   @AfterMethod
   protected void disconnectAndClenaupManager() throws RemoteException, MalformedURLException {
      manager.disconnect();
      manager.cleanup();
   }

   @Test
   public void testCreateVirtualMachine() {
      IMachine newVM = manager.getVBox().createMachine(settingsFile, vmName, osTypeId, vmId, forceOverwrite);
      manager.getVBox().registerMachine(newVM);
      assertNotNull(newVM.getName());
   }

   @Test(dependsOnMethods = "testCreateVirtualMachine")
   public void testChangeRAM() {
      Long memorySize = new Long(1024);
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
   public void testCreateIdeController() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.addStorageController(controllerIDE, StorageBus.IDE);
      mutable.saveSettings();
      session.unlockMachine();
      assertEquals(manager.getVBox().findMachine(vmName).getStorageControllers().size(), 1);
   }

   @Test(dependsOnMethods = "testCreateIdeController")
   public void testAttachIsoDvd() {
      IMedium distroMedium = manager.getVBox().openMedium(workingDir + "/" + distroIsoName, DeviceType.DVD,
               AccessMode.ReadOnly, forceOverwrite);

      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.attachDevice(controllerIDE, 0, 0, DeviceType.DVD, distroMedium);
      mutable.saveSettings();
      session.unlockMachine();
      assertEquals(distroMedium.getId().equals(""), false);
   }

   @Test(dependsOnMethods = "testAttachIsoDvd")
   public void testCreateAndAttachHardDisk() throws InterruptedException {
      IMedium hd = null;
      if (new File(adminDisk).exists()) {
         new File(adminDisk).delete();
      }
      hd = manager.getVBox().createHardDisk(diskFormat, adminDisk);
      long size = 4L * 1024L * 1024L * 1024L - 4L;
      IProgress progress = hd.createBaseStorage(new Long(size), new Long(MediumVariant.STANDARD.ordinal()));
      // TODO: poll?

      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.attachDevice(controllerIDE, 0, 1, DeviceType.HardDisk, hd);
      mutable.saveSettings();
      session.unlockMachine();
      assertEquals(hd.getId().equals(""), false);
   }

   @Test(dependsOnMethods = "testCreateAndAttachHardDisk")
   public void testConfigureNIC() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();

      // NAT
      mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.NAT);
      machine.getNetworkAdapter(new Long(0)).getNatDriver().addRedirect("guestssh", NATProtocol.TCP, "127.0.0.1", 2222,
               "", 22);
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);

      mutable.saveSettings();
      session.unlockMachine();
   }

   @Test(dependsOnMethods = "testConfigureNIC")
   public void testAttachGuestAdditions() {
      ISession session = manager.getSessionObject();
      IMachine machine = manager.getVBox().findMachine(vmName);

      IMedium distroMedium = manager.getVBox().openMedium(guestAdditionsDvd, DeviceType.DVD, AccessMode.ReadOnly,
               forceOverwrite);
      machine.lockMachine(session, LockType.Write);
      IMachine mutable = session.getMachine();
      mutable.attachDevice(controllerIDE, 1, 1, DeviceType.DVD, distroMedium);
      mutable.saveSettings(); // write settings to xml
      session.unlockMachine();
      assertEquals(distroMedium.getId().equals(""), false);
   }

   @Test(dependsOnMethods = "testAttachGuestAdditions")
   public void testStartVirtualMachine() throws InterruptedException {
      IMachine machine = manager.getVBox().findMachine(vmName);
      ISession session = manager.getSessionObject();
      launchVMProcess(machine, session);
      assertEquals(machine.getState(), MachineState.Running);
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         propagate(e);
      }

      sendKeyboardSequence(keyboardSequence);

      // test if the sshd on the guest is ready and meanwhile apply AdminAccess
      boolean sshDeamonIsRunning = false;
      while (!sshDeamonIsRunning) {
         try {
            AdminAccess.standard().init(new DefaultConfiguration()).render(OsFamily.UNIX);
            if (runScriptOnNode(guestId, "id").getExitCode() == 0)
               sshDeamonIsRunning = true;
         } catch (SshException e) {
            System.err.println("connection reset");
         }
      }
   }

   @Test(dependsOnMethods = "testStartVirtualMachine")
   public void testConfigureGuestAdditions() {
      // TODO generalize
      if (isUbuntu(guestId)) {
         /*
          * runScriptOnNode(guestId, "m-a prepare -i"); runScriptOnNode(guestId,
          * "mount -o loop /dev/dvd /media/cdrom"); runScriptOnNode(guestId,
          * "sh /media/cdrom/VBoxLinuxAdditions.run");
          * 
          * runScriptOnNode(guestId, "/etc/init.d/vboxadd setup");
          */
         runScriptOnNode(guestId, "rm /etc/udev/rules.d/70-persistent-net.rules");
         runScriptOnNode(guestId, "mkdir /etc/udev/rules.d/70-persistent-net.rules");
         runScriptOnNode(guestId, "rm -rf /dev/.udev/");
         runScriptOnNode(guestId, "rm /lib/udev/rules.d/75-persistent-net-generator.rules");
      }
   }

   @Test(dependsOnMethods = "testConfigureGuestAdditions")
   public void testStopVirtualMachine() {
      IMachine machine = manager.getVBox().findMachine(vmName);
      powerDownMachine(machine);
      assertEquals(machine.getState(), MachineState.PoweredOff);
   }

   @Test(dependsOnMethods = "testStopVirtualMachine")
   public void testChangeNICtoBridged() {
      ISession session = manager.getSessionObject();
      IMachine adminNode = manager.getVBox().findMachine(vmName);
      adminNode.lockMachine(session, LockType.Write);
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

      mutable.getNetworkAdapter(new Long(0)).setAttachmentType(NetworkAttachmentType.Bridged);
      mutable.getNetworkAdapter(new Long(0)).setAdapterType(NetworkAdapterType.Am79C973);
      mutable.getNetworkAdapter(new Long(0)).setMACAddress(manager.getVBox().getHost().generateMACAddress());
      mutable.getNetworkAdapter(new Long(0)).setBridgedInterface(hostInterface.trim());
      mutable.getNetworkAdapter(new Long(0)).setEnabled(true);
      mutable.saveSettings();
      session.unlockMachine();
   }

   @Test(dependsOnMethods = "testChangeNICtoBridged")
   public void testTakeAdminNodeSnapshot() {
      ISession session = manager.getSessionObject();
      IMachine adminNode = manager.getVBox().findMachine(vmName);
      adminNode.lockMachine(session, LockType.Write);
      if (adminNode.getCurrentSnapshot() == null
               || !adminNode.getCurrentSnapshot().getDescription().equals(snapshotDescription)) {
         manager.getSessionObject().getConsole().takeSnapshot(adminNode.getId(), snapshotDescription);
      }
      session.unlockMachine();
   }

   /**
    * 
    * @param machine
    * @param session
    */
   public void launchVMProcess(IMachine machine, ISession session) {
      IProgress prog = machine.launchVMProcess(session, "gui", "");
      prog.waitForCompletion(-1);
      session.unlockMachine();
   }

   /**
    * @param machine
    */
   public void powerDownMachine(IMachine machine) {
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
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
         e.printStackTrace();
      }
   }

}