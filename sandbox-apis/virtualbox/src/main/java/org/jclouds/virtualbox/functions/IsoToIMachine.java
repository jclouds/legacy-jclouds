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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;
import static org.virtualbox_4_1.AccessMode.ReadOnly;
import static org.virtualbox_4_1.DeviceType.DVD;
import static org.virtualbox_4_1.DeviceType.HardDisk;
import static org.virtualbox_4_1.LockType.Shared;
import static org.virtualbox_4_1.LockType.Write;

import java.io.File;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshException;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.virtualbox_4_1.AccessMode;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IMedium;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class IsoToIMachine implements Function<String, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

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
   private Credentials credentials;

   @Inject
   public IsoToIMachine(VirtualBoxManager manager, String adminDisk, String diskFormat, String settingsFile,
         String vmName, String osTypeId, String vmId, boolean forceOverwrite, String controllerIDE,
         ComputeServiceContext context, String hostId, String guestId, Credentials credentials) {
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
      this.credentials = credentials;
   }

   @Override
   public IMachine apply(@Nullable String isoName) {

      // TODO: WTF :) this is a prerequisite, so check state as opposed to
      // starting.
      // ex checkState(endpoint accessible, "please start jetty on %s",
      // endpoint)
      // Server server = new
      // StartJettyIfNotAlreadyRunning(port).apply(baseResource);

      IMachine vm = new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(settingsFile, osTypeId, vmId, forceOverwrite,
            manager).apply(vmName);

      String defaultWorkingDir = System.getProperty("user.home") + "/jclouds-virtualbox-test";
      String workingDir = System.getProperty(VIRTUALBOX_WORKINGDIR, defaultWorkingDir);

      // Change RAM
      ensureMachineHasMemory(vmName, 1024l);

      // IDE Controller
      ensureMachineHasIDEControllerNamed(vmName, controllerIDE);

      // Distribution medium
      ensureMachineHasAttachedDistroMedium(isoName, workingDir, controllerIDE);

      // DISK
      String adminDiskPath = workingDir + "/" + adminDisk;
      if (new File(adminDiskPath).exists()) {
         new File(adminDiskPath).delete();
      }

      // Create hard disk
      IMedium hardDisk = new CreateMediumIfNotAlreadyExists(manager, diskFormat, true).apply(adminDiskPath);

      // Attach hard disk to machine
      ensureMachineHasHardDiskAttached(vmName, hardDisk);

      // NAT
      ensureNATNetworkingIsAppliedToMachine(vmName);

      String guestAdditionsDvd = workingDir + "/VBoxGuestAdditions_4.1.2.iso";
      final IMedium guestAdditionsDvdMedium = manager.getVBox().openMedium(guestAdditionsDvd, DeviceType.DVD,
            AccessMode.ReadOnly, forceOverwrite);

      // Guest additions
      ensureGuestAdditionsMediumIsAttached(vmName, guestAdditionsDvdMedium);

      // Launch machine and wait for it to come online
      ensureMachineIsLaunched(vmName);

      String installKeySequence = System.getProperty(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE, defaultInstallSequence());
      sendKeyboardSequence(installKeySequence);

      boolean sshDeamonIsRunning = false;
      while (!sshDeamonIsRunning) {
         try {
            if (runScriptOnNode(guestId, "id", wrapInInitScript(false)).getExitCode() == 0) {
               logger.debug("Got response from ssh daemon.");
               sshDeamonIsRunning = true;
            }
         } catch (SshException e) {
            logger.debug("No response from ssh daemon...");
         }
      }

      logger.debug("Installation of image complete. Powering down...");
      lockSessionOnMachineAndApply(manager, Shared, vmName, new Function<ISession, Void>() {

         @Override
         public Void apply(ISession session) {
            IProgress powerDownProgress = session.getConsole().powerDown();
            powerDownProgress.waitForCompletion(-1);
            return null;
         }

      });
      // TODO: See above.
      // if you want to manage jetty, do it outside this class as it
      // has too many responsibilities otherwise. Allow this class to focus
      // solely on making an IMachine
      //
      // try {
      // logger.debug("Stopping Jetty server...");
      // server.stop();
      // logger.debug("Jetty server stopped.");
      // } catch (Exception e) {
      // logger.error(e, "Could not stop Jetty server.");
      // }
      return vm;
   }

   private void ensureMachineIsLaunched(String vmName) {
      applyForMachine(manager, vmName, new LaunchMachineIfNotAlreadyRunning(manager, ExecutionType.GUI, ""));
   }

   private void ensureGuestAdditionsMediumIsAttached(String vmName, final IMedium guestAdditionsDvdMedium) {
      lockMachineAndApply(manager, Write, vmName, new AttachMediumToMachineIfNotAlreadyAttached(controllerIDE,
            guestAdditionsDvdMedium, 1, 1, DeviceType.DVD));
   }

   private void ensureMachineHasHardDiskAttached(String vmName, IMedium hardDisk) {
      lockMachineAndApply(manager, Write, vmName, new AttachMediumToMachineIfNotAlreadyAttached(controllerIDE,
            hardDisk, 0, 1, HardDisk));
   }

   private void ensureMachineHasMemory(String vmName, final long memorySize) {
      lockMachineAndApply(manager, Write, vmName, new ApplyMemoryToMachine(memorySize));
   }

   private void ensureNATNetworkingIsAppliedToMachine(String vmName) {
      lockMachineAndApply(manager, Write, vmName, new AttachNATRedirectRuleToMachine(0l));
   }

   private void ensureMachineHasAttachedDistroMedium(String isoName, String workingDir, String controllerIDE) {
      final String pathToIsoFile = checkFileExists(workingDir + "/" + isoName);
      final IMedium distroMedium = manager.getVBox().openMedium(pathToIsoFile, DVD, ReadOnly, forceOverwrite);
      lockMachineAndApply(
            manager,
            Write,
            vmName,
            new AttachDistroMediumToMachine(checkNotNull(controllerIDE, "controllerIDE"), checkNotNull(distroMedium,
                  "distroMedium")));
   }

   public static String checkFileExists(String filePath) {
      if (new File(filePath).exists()) {
         return filePath;
      }
      throw new IllegalStateException("File " + filePath + " does not exist.");
   }

   public void ensureMachineHasIDEControllerNamed(String vmName, String controllerIDE) {
      lockMachineAndApply(manager, Write, checkNotNull(vmName, "vmName"),
            new AddIDEControllerIfNotExists(checkNotNull(controllerIDE, "controllerIDE")));
   }

   private <T> T applyForMachine(VirtualBoxManager manager, final String machineId, final Function<IMachine, T> function) {
      final IMachine immutableMachine = manager.getVBox().findMachine(machineId);
      return new Function<IMachine, T>() {
         @Override
         public T apply(IMachine machine) {
            return function.apply(machine);
         }

         @Override
         public String toString() {
            return function.toString();
         }
      }.apply(immutableMachine);
   }

   public static <T> T lockMachineAndApply(VirtualBoxManager manager, final LockType type, final String machineId,
         final Function<IMachine, T> function) {
      return lockSessionOnMachineAndApply(manager, type, machineId, new Function<ISession, T>() {

         @Override
         public T apply(ISession session) {
            return function.apply(session.getMachine());
         }

         @Override
         public String toString() {
            return function.toString();
         }

      });

   }

   public static <T> T lockSessionOnMachineAndApply(VirtualBoxManager manager, LockType type, String machineId,
         Function<ISession, T> function) {
      try {
         ISession session = manager.getSessionObject();
         IMachine immutableMachine = manager.getVBox().findMachine(machineId);
         immutableMachine.lockMachine(session, type);
         try {
            return function.apply(session);
         } finally {
            session.unlockMachine();
         }
      } catch (VBoxException e) {
         throw new RuntimeException(String.format("error applying %s to %s with %s lock: %s", function, machineId,
               type, e.getMessage()), e);
      }
   }

   private String defaultInstallSequence() {
      return "<Esc><Esc><Enter> "
            + "/install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg "
            + "debian-installer=en_US auto locale=en_US kbd-chooser/method=us " + "hostname=" + vmName + " "
            + "fb=false debconf/frontend=noninteractive "
            + "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
            + "initrd=/install/initrd.gz -- <Enter>";
   }

   private void sendKeyboardSequence(String keyboardSequence) {
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

   private String stringToKeycode(String s) {
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

   protected ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command, options);
   }

}
