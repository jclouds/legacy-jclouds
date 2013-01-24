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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_PASSWORD;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_USER;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMediumAttachment;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

@Singleton
public class CreateAndInstallVm implements Function<MasterSpec, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CreateAndRegisterMachineFromIsoIfNotAlreadyExists createAndRegisterMachineFromIsoIfNotAlreadyExists;
   private final Predicate<SshClient> sshResponds;
   private final Function<IMachine, SshClient> sshClientForIMachine;
   private final MachineUtils machineUtils;
   private final IMachineToNodeMetadata imachineToNodeMetadata;
   private final MachineController machineController;
   private final String version;
   private final String preconfigurationUrl;
   
   @Inject
   public CreateAndInstallVm(
            CreateAndRegisterMachineFromIsoIfNotAlreadyExists CreateAndRegisterMachineFromIsoIfNotAlreadyExists,
            IMachineToNodeMetadata imachineToNodeMetadata, Predicate<SshClient> sshResponds,
            Function<IMachine, SshClient> sshClientForIMachine, MachineUtils machineUtils,
            MachineController machineController, @BuildVersion String version,
            @Named(VIRTUALBOX_PRECONFIGURATION_URL) String preconfigurationUrl) {
      this.createAndRegisterMachineFromIsoIfNotAlreadyExists = CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
      this.sshResponds = sshResponds;
      this.sshClientForIMachine = sshClientForIMachine;
      this.machineUtils = machineUtils;
      this.imachineToNodeMetadata = imachineToNodeMetadata;
      this.machineController = machineController;
      this.version = Iterables.get(Splitter.on('-').split(version), 0);
      this.preconfigurationUrl = preconfigurationUrl;
   }

   @Override
   public IMachine apply(MasterSpec masterSpec) {
      VmSpec vmSpec = masterSpec.getVmSpec();
      IsoSpec isoSpec = masterSpec.getIsoSpec();
      String masterName = vmSpec.getVmName();
      IMachine masterMachine =
              checkNotNull(createAndRegisterMachineFromIsoIfNotAlreadyExists.apply(masterSpec), "master machine");
      // Launch machine and wait for it to come online
      machineController.ensureMachineIsLaunched(masterName);
      String installationKeySequence = isoSpec.getInstallationKeySequence().replace("PRECONFIGURATION_URL",
               preconfigurationUrl);
      configureOsInstallationWithKeyboardSequence(masterName, installationKeySequence);

      masterMachine.setExtraData(GUEST_OS_USER, masterSpec.getLoginCredentials().getUser());
      masterMachine.setExtraData(GUEST_OS_PASSWORD, masterSpec.getLoginCredentials().getPassword());

      SshClient client = sshClientForIMachine.apply(masterMachine);
      logger.debug(">> awaiting installation to finish node(%s)", masterName);
      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      checkState(sshResponds.apply(client), "timed out waiting for guest %s to be accessible via ssh", masterName);
      stopwatch.stop();
      logger.debug(String.format("Elapsed time for the OS installation: %d minutes", TimeUnit.SECONDS.convert(stopwatch.elapsed(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)));
      NodeMetadata nodeMetadata = imachineToNodeMetadata.apply(masterMachine);

      logger.debug(">> awaiting post-installation actions on vm: %s", masterName);
      ListenableFuture<ExecResponse> execCleanup = machineUtils.runScriptOnNode(nodeMetadata,
               call("cleanupUdevIfNeeded"), RunScriptOptions.NONE);
      ExecResponse cleanupResponse = Futures.getUnchecked(execCleanup);
      checkState(cleanupResponse.getExitStatus() == 0, "post-installation actions on vm(%s) failed", masterName);

      logger.debug(">> awaiting installation of guest additions on vm: %s", masterName);
      ListenableFuture<ExecResponse> execInstallGA = machineUtils.runScriptOnNode(nodeMetadata,
               new InstallGuestAdditions(vmSpec, version), RunScriptOptions.NONE);
      ExecResponse gaInstallationResponse = Futures.getUnchecked(execInstallGA);
      checkState(gaInstallationResponse.getExitStatus() == 0, "installation of guest additions on vm(%s) failed", masterName);
      
      machineController.ensureMachineIsShutdown(masterName);

      // detach DVD and ISOs, if needed
      Iterable<IMediumAttachment> mediumAttachments = Iterables.filter(
            masterMachine.getMediumAttachmentsOfController("IDE Controller"),
            new Predicate<IMediumAttachment>() {
               public boolean apply(IMediumAttachment in) {
                  return in.getMedium() != null
                        && in.getMedium().getDeviceType()
                              .equals(DeviceType.DVD);
               }
            });
      for (IMediumAttachment iMediumAttachment : mediumAttachments) {
         logger.debug("<< iMedium(%s) detached from (%s)", iMediumAttachment.getMedium()
               .getName(), masterMachine.getName());
         machineUtils.sharedLockMachineAndApply(
               masterMachine.getName(),
               new DetachDistroMediumFromMachine(iMediumAttachment
                     .getController(), iMediumAttachment.getPort(),
                     iMediumAttachment.getDevice()));
      }
      return masterMachine;
   }

   private void configureOsInstallationWithKeyboardSequence(String vmName, String installationKeySequence) {
      Iterable<List<Integer>> scancodelist = transform(Splitter.on(" ").split(installationKeySequence),
               new StringToKeyCode());
      for (List<Integer> scancodes : scancodelist) {
         machineUtils.sharedLockMachineAndApplyToSession(vmName, new SendScancodes(scancodes));
      }
   }

}
