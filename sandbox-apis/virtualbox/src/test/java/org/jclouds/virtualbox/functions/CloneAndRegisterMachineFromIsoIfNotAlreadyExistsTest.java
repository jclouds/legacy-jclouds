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

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IConsole;
import org.virtualbox_4_1.IHost;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit", testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsTest {

   private String hostId = "host";
	private String guestId = "guest";
	
	/* TODO Create a Test
	 * 
		Name:            eth0
		GUID:            30687465-0000-4000-8000-00261834d0cb
		Dhcp:            Disabled
		IPAddress:       209.x.x.x
		NetworkMask:     255.255.255.0
		IPV6Address:     fe80:0000:0000:0000:0226:18ff:fe34:d0cb
		IPV6NetworkMaskPrefixLength: 64
		HardwareAddress: 00:26:18:34:d0:cb
		MediumType:      Ethernet
		Status:          Up
		VBoxNetworkName: HostInterfaceNetworking-eth0
		
		Name:            vbox0
		GUID:            786f6276-0030-4000-8000-5a3ded993fed
		Dhcp:            Disabled
		IPAddress:       192.168.56.1
		NetworkMask:     255.255.255.0
		IPV6Address:
		IPV6NetworkMaskPrefixLength: 0
		HardwareAddress: 5a:3d:ed:99:3f:ed
		MediumType:      Ethernet
		Status:          Down
		VBoxNetworkName: HostInterfaceNetworking-vbox0
	 */
	
	@Test
   public void testCloneIfNotAlreadyExists() throws Exception {
		ComputeServiceContext context = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createMock(IVirtualBox.class);
      ISession iSession = createMock(ISession.class);
   	IConsole iConsole = createMock(IConsole.class);
		IProgress iProgress  = createMock(IProgress.class);
      IHost iHost = createMock(IHost.class);

      String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
      String cloneName = vmName + "_clone";
      IMachine master = createMock(IMachine.class);
      IMachine createdMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(master.getName()).andReturn(vmName).anyTimes();
      
		expect(manager.openMachineSession(master)).andReturn(iSession).anyTimes();
		expect(iSession.getConsole()).andReturn(iConsole).anyTimes(); 
		expect(iConsole.takeSnapshot("test", "desc test")).andReturn(iProgress).anyTimes();
		
		iSession.unlockMachine();
		expectLastCall().atLeastOnce();
		
      StringBuilder errorMessageBuilder = new StringBuilder();
      errorMessageBuilder.append("VirtualBox error: Could not find a registered machine named ");
      errorMessageBuilder.append("'jclouds-image-virtualbox-machine-to-machine-test' (0x80BB0001)");
      String errorMessage = errorMessageBuilder.toString();
      VBoxException vBoxException = new VBoxException(createNiceMock(Throwable.class), errorMessage);

      vBox.findMachine(cloneName);
      expectLastCall().andThrow(vBoxException);
      
      expect(vBox.createMachine(anyString(), eq(cloneName), anyString(), anyString(), anyBoolean())).andReturn(createdMachine).anyTimes();
		expect(vBox.getHost()).andReturn(iHost).anyTimes();
		expect(iHost.generateMACAddress()).andReturn("112233445566");

      List<CloneOptions> options = new ArrayList<CloneOptions>();
      options.add(CloneOptions.Link);
		ISnapshot iSnapshot = createNiceMock(ISnapshot.class);
		expect(master.getCurrentSnapshot()).andReturn(iSnapshot).anyTimes();
		expect(iSnapshot.getMachine()).andReturn(master).anyTimes();
		expect(master.cloneTo(createdMachine, CloneMode.MachineState,
            options)).andReturn(iProgress).anyTimes();
		expect(iProgress.getCompleted()).andReturn(true).anyTimes();
      vBox.registerMachine(createdMachine);

      replay(manager, vBox, master, iProgress, iSnapshot, iConsole, iSession);

      new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, context, "", "", "", false, cloneName, hostId).apply(master);

      verify(manager, vBox);
   }
   

   @Test(expectedExceptions = IllegalStateException.class)
   public void testFailIfMachineIsAlreadyRegistered() throws Exception {
		ComputeServiceContext context = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));

      VirtualBoxManager manager = createNiceMock(VirtualBoxManager.class);
      IVirtualBox vBox = createNiceMock(IVirtualBox.class);
      String vmName = "jclouds-image-my-ubuntu-image";
      String cloneName = vmName + "_clone";

      IMachine master = createMock(IMachine.class);
      IMachine registeredMachine = createMock(IMachine.class);

      expect(manager.getVBox()).andReturn(vBox).anyTimes();
      expect(vBox.findMachine(cloneName)).andReturn(registeredMachine).anyTimes();

      replay(manager, vBox);

      new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, context, "", "", "", false, cloneName, hostId).apply(master);
   }
	
   private String anyString() {
      return EasyMock.<String>anyObject();
   }
}
