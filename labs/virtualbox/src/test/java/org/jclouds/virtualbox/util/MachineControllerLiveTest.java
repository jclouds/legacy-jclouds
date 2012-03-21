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

package org.jclouds.virtualbox.util;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.CloneAndRegisterMachineFromIMachineIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.CreateAndInstallVanillaOs;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

@Test(groups = "live", testName = "MachineControllerLiveTest")
public class MachineControllerLiveTest extends BaseVirtualBoxClientLiveTest {

	private MasterSpec masterMachineSpec;
	private String cloneMasterName;
	private CloneSpec masterCloneMachineSpec;
	private NetworkSpec cloneNetworkSpec;
	private VmSpec clonedVmSpec;

	@Override
	@BeforeClass(groups = "live")
	public void setupClient() {
		super.setupClient();
		cloneMasterName = VIRTUALBOX_IMAGE_PREFIX
				+ CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass()
						.getSimpleName());

		StorageController ideController = StorageController
				.builder()
				.name("IDE Controller")
				.bus(StorageBus.IDE)
				.attachISO(0, 0, operatingSystemIso)
				.attachHardDisk(
						HardDisk.builder().diskpath(adminDisk(vanillaOsImageName))
								.controllerPort(0).deviceSlot(1)
								.autoDelete(true).build())
				.attachISO(1, 1, guestAdditionsIso).build();

		VmSpec masterVmSpec = VmSpec.builder().id(vanillaOsImageName)
				.name(vanillaOsImageName).osTypeId("").memoryMB(512)
				.cleanUpMode(CleanupMode.Full).controller(ideController)
				.forceOverwrite(true).build();

		Injector injector = context.utils().injector();
		Function<String, String> configProperties = injector
				.getInstance(ValueOfConfigurationKeyOrNull.class);
		IsoSpec isoSpec = IsoSpec
				.builder()
				.sourcePath(operatingSystemIso)
				.installationScript(
						configProperties.apply(
								VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace(
								"HOSTNAME", masterVmSpec.getVmName()))
				.build();

		NetworkAdapter networkAdapter = NetworkAdapter.builder()
				.networkAttachmentType(NetworkAttachmentType.NAT)
				.tcpRedirectRule("127.0.0.1", 2222, "", 22).build();
		NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
				.builder().addNetworkAdapter(networkAdapter).build();

		NetworkSpec networkSpec = NetworkSpec.builder()
				.addNIC(networkInterfaceCard).build();
		
		masterMachineSpec = MasterSpec.builder().iso(isoSpec)
				.vm(masterVmSpec).network(networkSpec).build();
		
		
		cloneNetworkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard)
				.build();

		clonedVmSpec = VmSpec.builder().id(cloneMasterName).name(cloneMasterName)
				.memoryMB(512).cleanUpMode(CleanupMode.Full).forceOverwrite(true).build();
	}

	@Test
	public void testEnsureMachineCanBeLaunchedRunScriptAndPoweredOff() {
		IMachine vm = getOrCreateVanillaOsClone();
		machineController.ensureMachineIsLaunched(cloneMasterName);

		NodeMetadata vmMetadata = context.utils().injector()
				.getInstance(IMachineToNodeMetadata.class).apply(vm);
		machineUtils.runScriptOnNode(vmMetadata, call("cleanupUdevIfNeeded"),
				RunScriptOptions.NONE);

		machineController.ensureMachineHasPowerDown(cloneMasterName);
	}

	private IMachine getOrCreateVanillaOsClone() {
		try {
			return manager.get().getVBox().findMachine(cloneMasterName);
		} catch (Exception e) {
			IMachine source = getOrCreateVanillaOs();
	        masterCloneMachineSpec = CloneSpec.builder().vm(clonedVmSpec).network(cloneNetworkSpec).master(source)
	                .linked(true).build();
			return new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
					manager, workingDir, machineUtils).apply(masterCloneMachineSpec);
		}

	}

	private IMachine getOrCreateVanillaOs() {
		try {
			return manager.get().getVBox().findMachine(vanillaOsImageName);
		} catch (Exception e) {
			Injector injector = context.utils().injector();
			return injector.getInstance(CreateAndInstallVanillaOs.class)
					.apply(masterMachineSpec);
		}
	}

	@Override
	@AfterClass(groups = "live")
	protected void tearDown() throws Exception {
		for (String vmName : ImmutableSet.of(cloneMasterName)) {
			undoVm(vmName);
		}
		super.tearDown();
	}
}
