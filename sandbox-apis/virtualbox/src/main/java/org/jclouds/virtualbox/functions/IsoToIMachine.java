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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshException;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.virtualbox_4_1.*;

import javax.annotation.Resource;
import javax.inject.Named;
import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.virtualbox.util.MachineUtils.*;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;
import static org.virtualbox_4_1.AccessMode.ReadOnly;
import static org.virtualbox_4_1.DeviceType.DVD;
import static org.virtualbox_4_1.DeviceType.HardDisk;
import static org.virtualbox_4_1.LockType.Shared;
import static org.virtualbox_4_1.LockType.Write;

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
   private Predicate<IPSocket> socketTester;
   private String webServerHost;
   private int webServerPort;

   @Inject
   public IsoToIMachine(VirtualBoxManager manager, String adminDisk, String diskFormat, String settingsFile,
                        String vmName, String osTypeId, String vmId, boolean forceOverwrite, String controllerIDE,
                        ComputeServiceContext context, String hostId, String guestId, Predicate<IPSocket> socketTester,
                        String webServerHost, int webServerPort) {
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
      this.socketTester = socketTester;
      this.webServerHost = webServerHost;
      this.webServerPort = webServerPort;
   }

   @Override
   public IMachine apply(@Nullable String isoName) {

      ensureWebServerIsRunning();

      final IMachine vm = new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(settingsFile, osTypeId, vmId, forceOverwrite,
              manager).apply(vmName);

      final String defaultWorkingDir = System.getProperty("user.home") + "/jclouds-virtualbox-test";
      final String workingDir = System.getProperty(VIRTUALBOX_WORKINGDIR, defaultWorkingDir);

      // Change RAM
      ensureMachineHasMemory(vmName, 1024l);

      // IDE Controller
      ensureMachineHasIDEControllerNamed(vmName, controllerIDE);

      // Distribution medium
      ensureMachineHasAttachedDistroMedium(isoName, workingDir, controllerIDE);

      // DISK
      final String adminDiskPath = workingDir + "/" + adminDisk;
      if (new File(adminDiskPath).exists()) {
         boolean deleted = new File(adminDiskPath).delete();
         if (!deleted) {
            logger.error(String.format("File %s could not be deleted.", adminDiskPath));
         }
      }

      // Create hard disk
      final IMedium hardDisk = new CreateMediumIfNotAlreadyExists(manager, diskFormat, true).apply(adminDiskPath);

      // Attach hard disk to machine
      ensureMachineHasHardDiskAttached(vmName, hardDisk);

      // NAT
      ensureNATNetworkingIsAppliedToMachine(vmName);

      final String guestAdditionsDvd = workingDir + "/VBoxGuestAdditions_4.1.2.iso";
      final IMedium guestAdditionsDvdMedium = manager.getVBox().openMedium(guestAdditionsDvd, DeviceType.DVD,
              AccessMode.ReadOnly, forceOverwrite);

      // Guest additions
      ensureGuestAdditionsMediumIsAttached(vmName, guestAdditionsDvdMedium);

      // Launch machine and wait for it to come online
      ensureMachineIsLaunched(vmName);

      final String installKeySequence = System.getProperty(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE, defaultInstallSequence());
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
      return vm;
   }

   private void ensureWebServerIsRunning() {
      final IPSocket webServerSocket = new IPSocket(webServerHost, webServerPort);
      if (!socketTester.apply(webServerSocket)) {
         throw new IllegalStateException(String.format("Web server is not running on host %s:%s which is needed to serve preseed.cfg.", webServerHost, webServerPort));
      }
   }

   private void ensureMachineIsLaunched(String vmName) {
      applyForMachine(manager, vmName, new LaunchMachineIfNotAlreadyRunning(manager, ExecutionType.HEADLESS, ""));
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


   private String defaultInstallSequence() {
      return "<Esc><Esc><Enter> "
              + "/install/vmlinuz noapic preseed/url=http://10.0.2.2:" + webServerPort + "/src/test/resources/preseed.cfg "
              + "debian-installer=en_US auto locale=en_US kbd-chooser/method=us " + "hostname=" + vmName + " "
              + "fb=false debconf/frontend=noninteractive "
              + "keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false "
              + "initrd=/install/initrd.gz -- <Enter>";
   }

   private void sendKeyboardSequence(String keyboardSequence) {
      String[] splitSequence = keyboardSequence.split(" ");
      StringBuilder sb = new StringBuilder();
      for (String line : splitSequence) {
         String converted = stringToKeycode(line);
         for (String word : converted.split("  ")) {
            sb.append("vboxmanage controlvm ").append(vmName).append(" keyboardputscancode ").append(word).append("; ");
            runScriptIfWordEndsWith(sb, word, "<Enter>");
            runScriptIfWordEndsWith(sb, word, "<Return>");
         }
      }
   }

   private void runScriptIfWordEndsWith(StringBuilder sb, String word, String key) {
      if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get(key))) {
         runScriptOnNode(hostId, sb.toString(), runAsRoot(false).wrapInInitScript(false));
         sb.delete(0, sb.length() - 1);
      }
   }

   private String stringToKeycode(String s) {
      StringBuilder keycodes = new StringBuilder();
      if (s.startsWith("<")) {
         String[] specials = s.split("<");
         for (int i = 1; i < specials.length; i++) {
            keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<" + specials[i])).append("  ");
         }
         return keycodes.toString();
      }

      int i = 0;
      while (i < s.length()) {
         String digit = s.substring(i, i + 1);
         String hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP.get(digit);
         keycodes.append(hex).append(" ");
         if (i != 0 && i % 14 == 0)
            keycodes.append(" ");
         i++;
      }
      keycodes.append(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Spacebar>")).append(" ");

      return keycodes.toString();
   }

   protected ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command, options);
   }

}
