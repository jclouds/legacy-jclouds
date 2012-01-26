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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
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
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
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
   private final Function<IMachine, SshClient> sshClientForIMachine;

   private final MachineUtils machineUtils;

   @Inject
   public CreateAndInstallVm(
         Supplier<VirtualBoxManager> manager,
         CreateAndRegisterMachineFromIsoIfNotAlreadyExists CreateAndRegisterMachineFromIsoIfNotAlreadyExists,
         Predicate<SshClient> sshResponds,
         Function<IMachine, SshClient> sshClientForIMachine,
         ExecutionType executionType, MachineUtils machineUtils) {
      this.manager = manager;
      this.createAndRegisterMachineFromIsoIfNotAlreadyExists = CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
      this.sshResponds = sshResponds;
      this.sshClientForIMachine = sshClientForIMachine;
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

      configureOsInstallationWithKeyboardSequence(vmName,
            installationKeySequence);

      SshClient client = sshClientForIMachine.apply(vm);

      logger.debug(">> awaiting installation to finish node(%s)", vmName);

      checkState(sshResponds.apply(client),
            "timed out waiting for guest %s to be accessible via ssh", vmName);

      logger.debug("<< installation of image complete. Powering down node(%s)",
            vmName);
      ensureMachineHasPowerDown(vmName);
      return vm;
   }

   private void configureOsInstallationWithKeyboardSequence(String vmName,
         String installationKeySequence) {

      Iterable<List<Integer>> scancodelist = Iterables.transform(
            Splitter.on(" ").split(installationKeySequence),
            new StringToKeyCode());

      for (List<Integer> scancodes : scancodelist) {

         machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
               new SendScancode(scancodes));

         // this is needed to avoid to miss any scancode
         try {
            Thread.sleep(300);
         } catch (InterruptedException e) {
            logger.error("Problem in sleeping the current thread.", e);
         }
      }
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

   private List<Integer> stringToKeycode(String s) {
      if (containsSpecialCharacter(s)) {
         return transforSpecialCharIntoKeycodes(s);
      } else {
         return transformStandardCharacterIntoKeycodes(s);
      }
   }

   private List<Integer> transformStandardCharacterIntoKeycodes(String s) {
      List<Integer> values = new ArrayList<Integer>();
      for (String digit : Splitter.fixedLength(1).split(s)) {
         List<Integer> hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP_LIST
               .get(digit);
         if (hex != null)
            values.addAll(hex);
      }
      values.addAll(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST
            .get("<Spacebar>"));
      return values;
   }

   private List<Integer> transforSpecialCharIntoKeycodes(String s) {
      List<Integer> values = new ArrayList<Integer>();
      for (String special : s.split("<")) {
         List<Integer> value = KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST
               .get("<" + special);
         if (value != null)
            values.addAll(value);
      }
      return values;

   }

   private boolean containsSpecialCharacter(String s) {
      return s.startsWith("<");
   }

   class SendScancode implements Function<ISession, Void> {

      private List<Integer> scancodes;

      public SendScancode(List<Integer> scancodes) {
         super();
         this.scancodes = scancodes;
      }

      @Override
      public Void apply(ISession iSession) {
         for (Integer scancode : scancodes) {
            iSession.getConsole().getKeyboard().putScancode(scancode);
         }
         return null;
      }
   }

   class StringToKeyCode implements Function<String, List<Integer>> {

      @Override
      public List<Integer> apply(String subsequence) {
         return stringToKeycode(subsequence);
      }
   }

}
