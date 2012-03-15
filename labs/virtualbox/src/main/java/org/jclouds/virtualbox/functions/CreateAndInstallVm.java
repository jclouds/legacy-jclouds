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
import static com.google.common.collect.Iterables.transform;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.Preconfiguration;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.predicates.GuestAdditionsInstaller;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;

@Singleton
public class CreateAndInstallVm implements Function<MasterSpec, IMachine> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final Supplier<VirtualBoxManager> manager;
	private final CreateAndRegisterMachineFromIsoIfNotAlreadyExists createAndRegisterMachineFromIsoIfNotAlreadyExists;
	private final GuestAdditionsInstaller guestAdditionsInstaller;
	private final Predicate<SshClient> sshResponds;
	private LoadingCache<IsoSpec, URI> preConfiguration;
	private final Function<IMachine, SshClient> sshClientForIMachine;
	private final MachineUtils machineUtils;
	private final IMachineToNodeMetadata imachineToNodeMetadata;
	private final MachineController machineController;
	

	@Inject
	public CreateAndInstallVm(
			Supplier<VirtualBoxManager> manager,
			CreateAndRegisterMachineFromIsoIfNotAlreadyExists CreateAndRegisterMachineFromIsoIfNotAlreadyExists,
			GuestAdditionsInstaller guestAdditionsInstaller,
			IMachineToNodeMetadata imachineToNodeMetadata,
			Predicate<SshClient> sshResponds,
			Function<IMachine, SshClient> sshClientForIMachine,
			MachineUtils machineUtils,
			@Preconfiguration LoadingCache<IsoSpec, URI> preConfiguration, MachineController machineController) {
		this.manager = manager;
		this.createAndRegisterMachineFromIsoIfNotAlreadyExists = CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
		this.sshResponds = sshResponds;
		this.sshClientForIMachine = sshClientForIMachine;
		this.machineUtils = machineUtils;
		this.preConfiguration = preConfiguration;
		this.guestAdditionsInstaller = guestAdditionsInstaller;
		this.imachineToNodeMetadata = imachineToNodeMetadata;
		this.machineController = machineController;
	}

	@Override
	public IMachine apply(MasterSpec masterSpec) {

		VmSpec vmSpec = masterSpec.getVmSpec();
		IsoSpec isoSpec = masterSpec.getIsoSpec();
		String vmName = vmSpec.getVmName();

		IMachine vm = createAndRegisterMachineFromIsoIfNotAlreadyExists
				.apply(masterSpec);

		// Launch machine and wait for it to come online
		machineController.ensureMachineIsLaunched(vmName);

		URI uri = preConfiguration.getUnchecked(isoSpec);
		String installationKeySequence = isoSpec.getInstallationKeySequence()
				.replace("PRECONFIGURATION_URL", uri.toASCIIString());

		configureOsInstallationWithKeyboardSequence(vmName,
				installationKeySequence);
		
		SshClient client = sshClientForIMachine.apply(vm);

		logger.debug(">> awaiting installation to finish node(%s)", vmName);

		checkState(sshResponds.apply(client),
				"timed out waiting for guest %s to be accessible via ssh",
				vmName);

		logger.debug(">> awaiting installation of guest additions on vm: %s", vmName);
		checkState(guestAdditionsInstaller.apply(vm));

		logger.debug(">> awaiting post-installation actions on vm: %s", vmName);

//		NodeMetadata vmMetadata = imachineToNodeMetadata.apply(vm);

//		ListenableFuture<ExecResponse> execFuture =
//		machineUtils.runScriptOnNode(vmMetadata, call("cleanupUdevIfNeeded"), RunScriptOptions.NONE);

//		ExecResponse execResponse = Futures.getUnchecked(execFuture);
//		checkState(execResponse.getExitStatus() == 0);

		logger.debug(
				"<< installation of image complete. Powering down node(%s)",
				vmName);

		machineController.ensureMachineHasPowerDown(vmName);
		return vm;
	}

	private void configureOsInstallationWithKeyboardSequence(String vmName,
			String installationKeySequence) {
		Iterable<List<Integer>> scancodelist = transform(Splitter.on(" ")
				.split(installationKeySequence), new StringToKeyCode());

		for (List<Integer> scancodes : scancodelist) {
			machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared,
					new SendScancodes(scancodes));
		}
	}

}
