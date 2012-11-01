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

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.StorageBus;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Suppliers;

/**
 * @author Mattias Holmqvist
 */
@Test(groups = "unit", testName = "CreateAndRegisterMachineFromIsoIfNotAlreadyExistsTest")
public class CreateAndRegisterMachineFromIsoIfNotAlreadyExistsTest {

    @Test(enabled = false)
    public void testCreateAndSetMemoryWhenNotAlreadyExists() throws Exception {

        MachineUtils machineUtils = createMock(MachineUtils.class);
        VirtualBoxManager manager = createMock(VirtualBoxManager.class);
        IVirtualBox vBox = createMock(IVirtualBox.class);
        String vmName = "jclouds-image-my-ubuntu-image";
        StorageController ideController = StorageController.builder().name("IDE Controller").bus(StorageBus.IDE).build();
        VmSpec vmSpec = VmSpec.builder().id(vmName).name(vmName).osTypeId("").memoryMB(1024).controller(ideController).cleanUpMode(
                CleanupMode.Full).build();
        MasterSpec machineSpec = MasterSpec.builder()
                .iso(IsoSpec.builder().sourcePath("some.iso").installationScript("").build())
                .vm(vmSpec)
                .network(NetworkSpec.builder().build()).build();
        IMachine createdMachine = createMock(IMachine.class);
        ISession session = createMock(ISession.class);

        expect(manager.getVBox()).andReturn(vBox).anyTimes();
        expect(vBox.composeMachineFilename(vmName, "/tmp/workingDir")).andReturn("settingsFile");

        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessageBuilder.append("VirtualBox error: Could not find a registered machine with UUID {");
        errorMessageBuilder.append("'jclouds-image-virtualbox-iso-to-machine-test'} (0x80BB0001)");
        String errorMessage = errorMessageBuilder.toString();
        VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

        expect(vBox.findMachine(vmName)).andThrow(vBoxException);

        expect(vBox.createMachine(anyString(), eq(vmName), anyString(), anyString(), anyBoolean())).andReturn(
                createdMachine).anyTimes();
        vBox.registerMachine(createdMachine);

        expect(vBox.findMachine(vmName)).andReturn(createdMachine).anyTimes();
        expect(manager.getSessionObject()).andReturn(session);
        expect(session.getMachine()).andReturn(createdMachine);
        createdMachine.lockMachine(session, LockType.Write);
        createdMachine.setMemorySize(1024l);
        createdMachine.saveSettings();
        session.unlockMachine();


        //TODO: this mock test is not finished.
        replay(manager, createdMachine, vBox, session);

        new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, "/tmp/workingDir").apply(machineSpec);

        verify(manager, createdMachine, vBox, session);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testFailIfMachineIsAlreadyRegistered() throws Exception {

        MachineUtils machineUtils = createMock(MachineUtils.class);

        VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
        IVirtualBox vBox = createNiceMock(IVirtualBox.class);
        String vmName = "jclouds-image-my-ubuntu-image";

        IMachine registeredMachine = createMock(IMachine.class);

        expect(manager.getVBox()).andReturn(vBox).anyTimes();
        expect(vBox.findMachine(vmName)).andReturn(registeredMachine).anyTimes();

        replay(manager, vBox, machineUtils);

        VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName).osTypeId("").memoryMB(1024).cleanUpMode(
                CleanupMode.Full).build();

        MasterSpec machineSpec = MasterSpec.builder()
                .iso(IsoSpec.builder()
                        .sourcePath("some.iso")
                        .installationScript("dostuff").build())
                .vm(launchSpecification)
                .network(NetworkSpec.builder().build()).build();
        new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, "/tmp/workingDir").apply(machineSpec);
    }

    @Test(expectedExceptions = VBoxException.class)
    public void testFailIfOtherVBoxExceptionIsThrown() throws Exception {

        MachineUtils machineUtils = createMock(MachineUtils.class);

        VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
        IVirtualBox vBox = createNiceMock(IVirtualBox.class);
        String vmName = "jclouds-image-my-ubuntu-image";

        String errorMessage = "VirtualBox error: Some other VBox error";
        VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

        expect(manager.getVBox()).andReturn(vBox).anyTimes();

        vBox.findMachine(vmName);
        expectLastCall().andThrow(vBoxException);

        replay(manager, vBox, machineUtils);

        VmSpec launchSpecification = VmSpec.builder().id(vmName).name(vmName).osTypeId("").cleanUpMode(CleanupMode.Full)
                .memoryMB(1024).build();
        MasterSpec machineSpec = MasterSpec.builder()
                .iso(IsoSpec.builder()
                        .sourcePath("some.iso")
                        .installationScript("dostuff").build())
                .vm(launchSpecification)
                .network(NetworkSpec.builder().build()).build();

        new CreateAndRegisterMachineFromIsoIfNotAlreadyExists(Suppliers.ofInstance(manager), machineUtils, "/tmp/workingDir").apply(machineSpec);

    }

    private String anyString() {
        return EasyMock.<String>anyObject();
    }
}
