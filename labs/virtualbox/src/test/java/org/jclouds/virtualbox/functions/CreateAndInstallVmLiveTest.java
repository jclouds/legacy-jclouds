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
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.predicates.SshResponds;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.jaxws.MachineState;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Andrea Turli, Mattias Holmqvist
 */
@Test(groups = "live", singleThreaded = true, testName = "CreateAndInstallVmLiveTest")
public class CreateAndInstallVmLiveTest extends BaseVirtualBoxClientLiveTest {

	Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
	}.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice
			.createInjector(new GsonModule()).getInstance(Json.class));

	private VmSpec vmSpecification;
	private MasterSpec masterSpec;
	private Injector injector;
	private Function<IMachine, SshClient> sshClientForIMachine;
	private Predicate<SshClient> sshResponds;

	@Override
	@BeforeClass(groups = "live")
	public void setupClient() {
		super.setupClient();
		String vmName = VIRTUALBOX_IMAGE_PREFIX
				+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
						.getSimpleName());

		HardDisk hardDisk = HardDisk.builder().diskpath(adminDisk)
				.autoDelete(true).controllerPort(0).deviceSlot(1).build();
		StorageController ideController = StorageController.builder()
				.name("IDE Controller").bus(StorageBus.IDE)
				.attachISO(0, 0, operatingSystemIso).attachHardDisk(hardDisk)
				.attachISO(1, 1, guestAdditionsIso).build();
		vmSpecification = VmSpec.builder().id(vmName).name(vmName)
				.memoryMB(512).osTypeId("").controller(ideController)
				.forceOverwrite(true).cleanUpMode(CleanupMode.Full).build();

		injector = context.utils().injector();
		Function<String, String> configProperties = injector
				.getInstance(ValueOfConfigurationKeyOrNull.class);

		NetworkAdapter networkAdapter = NetworkAdapter.builder()
				.networkAttachmentType(NetworkAttachmentType.NAT)
				.tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
		NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
				.builder().addNetworkAdapter(networkAdapter).build();

		NetworkSpec networkSpec = NetworkSpec.builder()
				.addNIC1(networkInterfaceCard).build();

		masterSpec = MasterSpec
				.builder()
				.vm(vmSpecification)
				.iso(IsoSpec
						.builder()
						.sourcePath(operatingSystemIso)
						.installationScript(
								configProperties.apply(
										VIRTUALBOX_INSTALLATION_KEY_SEQUENCE)
										.replace("HOSTNAME",
												vmSpecification.getVmName()))
						.build()).network(networkSpec).build();

		undoVm(vmSpecification);
	}

	public void testCreateImageMachineFromIso() throws Exception {
		IMachine imageMachine = getVmWithGuestAdditionsInstalled();

		IMachineToImage iMachineToImage = new IMachineToImage(manager, map);
		Image newImage = iMachineToImage.apply(imageMachine);
		// TODO add the description to the cache of the images or serialize to
		// YAML the image desc
		Set<? extends Image> images = context.getComputeService().listImages();
		Iterable<String> imageIds = transform(images, extractId());
		assertTrue(any(imageIds, equalTo(newImage.getId())));
	}

	@Test
	public void testGuestAdditionsAreInstalled() throws Exception {
		try {
			IMachine machine = getVmWithGuestAdditionsInstalled();

			machineUtils.applyForMachine(machine.getName(),
					new LaunchMachineIfNotAlreadyRunning(manager.get(),
							ExecutionType.GUI, ""));
			sshClientForIMachine = injector
					.getInstance(IMachineToSshClient.class);
			SshClient client = sshClientForIMachine.apply(machine);

			sshResponds = injector.getInstance(SshResponds.class);
			checkState(sshResponds.apply(client),
					"timed out waiting for guest %s to be accessible via ssh",
					machine.getName());

			assertTrue(machineUtils.lockSessionOnMachineAndApply(
					machine.getName(), LockType.Shared,
					new Function<ISession, Boolean>() {
						@Override
						public Boolean apply(ISession session) {
							String vboxVersion = Iterables
									.get(Splitter
											.on('r')
											.split(context
													.getProviderSpecificContext()
													.getBuildVersion()), 0);
							return session
									.getMachine()
									.getGuestPropertyValue(
											"/VirtualBox/GuestAdd/Version")
									.equals(vboxVersion);
						}
					}));
		} finally {
			for (VmSpec spec : ImmutableSet.of(vmSpecification)) {
				ensureMachineHasPowerDown(spec.getVmName());
			}
		}
	}

	private Function<Image, String> extractId() {
		return new Function<Image, String>() {

			@Override
			public String apply(@Nullable Image input) {
				return input.getId();
			}
		};
	}
	
	private IMachine getVmWithGuestAdditionsInstalled() {
		try {
			Injector injector = context.utils().injector();
			return injector.getInstance(CreateAndInstallVm.class).apply(
					masterSpec);
		} catch (IllegalStateException e) {
			// already created
			return manager.get().getVBox()
					.findMachine(masterSpec.getVmSpec().getVmId());
		}
	}

	private void ensureMachineHasPowerDown(String vmName) {
		while (manager.get().getVBox().findMachine(vmName).getState()
				.equals(MachineState.RUNNING)) {
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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Throwables.propagate(e);
			}
		}
	}

	@Override
	@AfterClass(groups = "live")
	protected void tearDown() throws Exception {
		for (VmSpec spec : ImmutableSet.of(vmSpecification)) {
			undoVm(spec);
		}
		super.tearDown();
	}
}
