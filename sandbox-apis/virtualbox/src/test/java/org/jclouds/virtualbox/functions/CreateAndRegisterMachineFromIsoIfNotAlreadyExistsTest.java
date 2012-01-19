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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.LoadingCache;
import org.easymock.EasyMock;
import org.jclouds.virtualbox.domain.*;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.*;

import java.net.URI;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

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
        LoadingCache<IsoSpec, URI> preconfiguration = createNiceMock(LoadingCache.class);
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
        Supplier<URI> preconfiguration = createNiceMock(Supplier.class);
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
        Supplier<URI> preconfiguration = createNiceMock(Supplier.class);
        String vmName = "jclouds-image-my-ubuntu-image";

        String errorMessage = "VirtualBox error: Soem other VBox error";
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
