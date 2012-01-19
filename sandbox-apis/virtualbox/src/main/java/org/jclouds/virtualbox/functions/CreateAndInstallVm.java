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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.Preconfiguration;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.IMachineSpec;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;

@Singleton
public class CreateAndInstallVm implements Function<IMachineSpec, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final CreateAndRegisterMachineFromIsoIfNotAlreadyExists createAndRegisterMachineFromIsoIfNotAlreadyExists;

   private final Predicate<SshClient> sshResponds;
   private final ExecutionType executionType;

   private final Factory scriptRunner;
   private final Supplier<NodeMetadata> host;

   private final Function<IMachine, SshClient> sshClientForIMachine;

   private final MachineUtils machineUtils;

   private LockType lockType;

   @Inject
   public CreateAndInstallVm(
         Supplier<VirtualBoxManager> manager,
         CreateAndRegisterMachineFromIsoIfNotAlreadyExists CreateAndRegisterMachineFromIsoIfNotAlreadyExists,
         ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull,
         Predicate<SshClient> sshResponds,
         Function<IMachine, SshClient> sshClientForIMachine,
         Supplier<NodeMetadata> host, RunScriptOnNode.Factory scriptRunner,
         @Preconfiguration Supplier<URI> preconfiguration,
         ExecutionType executionType, MachineUtils machineUtils) {
      this.manager = manager;
      this.createAndRegisterMachineFromIsoIfNotAlreadyExists = CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
      this.sshResponds = sshResponds;
      this.sshClientForIMachine = sshClientForIMachine;
      this.scriptRunner = scriptRunner;
      this.host = host;
      this.executionType = executionType;
      this.machineUtils = machineUtils;
   }

   @Override
   public IMachine apply(IMachineSpec machineSpec) {

      VmSpec vmSpec = machineSpec.getVmSpec();
      IsoSpec isoSpec = machineSpec.getIsoSpec();

      String vmName = vmSpec.getVmName();

      final IMachine vm = createAndRegisterMachineFromIsoIfNotAlreadyExists
            .apply(machineSpec);

      // Launch machine and wait for it to come online
      ensureMachineIsLaunched(vmName);

      URI uri = isoSpec.getPreConfigurationUri().get();
      String installationKeySequence = isoSpec.getInstallationKeySequence()
            .replace("PRECONFIGURATION_URL", uri.toASCIIString());
      sendKeyboardSequence(installationKeySequence, vmName);

      SshClient client = sshClientForIMachine.apply(vm);

      logger.debug(">> awaiting installation to finish node(%s)", vmName);

      checkState(sshResponds.apply(client),
            "timed out waiting for guest %s to be accessible via ssh", vmName);

      logger.debug("<< installation of image complete. Powering down node(%s)",
            vmName);
      ensureMachineHasPowerDown(vmName);
      return vm;
   }

   private void ensureMachineHasPowerDown(String vmName) {
      machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
            new Function<ISession, Void>() {
               @Override
               public Void apply(ISession session) {
                  IProgress powerDownProgress = session.getConsole()
                        .powerDown();
                  powerDownProgress.waitForCompletion(-1);
                  return null;
               }
            });
   }

   private void ensureMachineIsLaunched(String vmName) {
      machineUtils.applyForMachine(vmName,
            new LaunchMachineIfNotAlreadyRunning(manager.get(), executionType,
                  ""));
   }

   private void sendKeyboardSequence(String keyboardSequence, String vmName) {
      String[] splitSequence = keyboardSequence.split(" ");
      StringBuilder sb = new StringBuilder();
      for (String line : splitSequence) {
         String converted = stringToKeycode(line);
         for (String word : converted.split("  ")) {
            sb.append("vboxmanage controlvm ").append(vmName)
                  .append(" keyboardputscancode ").append(word).append("; ");
            runScriptIfWordEndsWith(sb, word, "<Enter>");
            runScriptIfWordEndsWith(sb, word, "<Return>");
         }
      }
   }

   private void runScriptIfWordEndsWith(StringBuilder sb, String word,
         String key) {
      if (word.endsWith(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get(key))) {
         scriptRunner
               .create(host.get(), Statements.exec(sb.toString()),
                     runAsRoot(false).wrapInInitScript(false)).init().call();
         sb.delete(0, sb.length() - 1);
      }
   }

   private String stringToKeycode(String s) {
      StringBuilder keycodes = new StringBuilder();
      if (s.startsWith("<")) {
         String[] specials = s.split("<");
         for (int i = 1; i < specials.length; i++) {
            keycodes.append(
                  KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<"
                        + specials[i])).append("  ");
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
      keycodes.append(
            KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP.get("<Spacebar>"))
            .append(" ");

      return keycodes.toString();
   }

}
