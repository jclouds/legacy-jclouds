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

import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.testng.Assert.assertFalse;

import java.io.IOException;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;


/**
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "CloneAndRegisterMachineFromIsoIfNotAlreadyExistsLiveTest")
public class CloneAndRegisterMachineFromIsoIfNotAlreadyExistsTest extends BaseVirtualBoxClientLiveTest {
  
	private String settingsFile = null;
	private boolean forceOverwrite = true;
	private String vmId = "jclouds-image-iso-1";
	private String osTypeId = "";
	private String controllerIDE = "IDE Controller";
	private String diskFormat = "";
	private String adminDisk = "testadmin.vdi";
	private String guestId = "guest";
	private String hostId = "host";

	private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";

   public static final String TEST1 = 
   		"Name:            eth0\n" +
   		"GUID:            30687465-0000-4000-8000-00261834d0cb\n" +
   		"Dhcp:            Disabled\n" +
   		"IPAddress:       209.x.x.x\n" +
   		"NetworkMask:     255.255.255.0\n" +
   		"IPV6Address:     fe80:0000:0000:0000:0226:18ff:fe34:d0cb\n" +
   		"IPV6NetworkMaskPrefixLength: 64\n" +
   		"HardwareAddress: 00:26:18:34:d0:cb\n" +
   		"MediumType:      Ethernet\n" +
   		"Status:          Up\n" +
   		"VBoxNetworkName: HostInterfaceNetworking-eth0\n" +
   		"\n" +
   		"Name:            vbox0\n" +
   		"GUID:            786f6276-0030-4000-8000-5a3ded993fed\n" +
   		"Dhcp:            Disabled\n" +
   		"IPAddress:       192.168.56.1\n" +
   		"NetworkMask:     255.255.255.0\n" +
   		"IPV6Address: IPV6NetworkMaskPrefixLength: 0\n" +
   		"HardwareAddress: 5a:3d:ed:99:3f:ed\n" +
   		"MediumType:      Ethernet\n" +
   		"Status:          Down\n" +
   		"VBoxNetworkName: HostInterfaceNetworking-vbox0\n";
   
   public static final String TEST2 = 
   		"Name:            eth0\n" +
   		"GUID:            30687465-0000-4000-8000-00261834d0cb\n" +
   		"Dhcp:            Disabled\n" +
   		"IPAddress:       209.x.x.x\n" +
   		"NetworkMask:     255.255.255.0\n" +
   		"IPV6Address:     fe80:0000:0000:0000:0226:18ff:fe34:d0cb\n" +
   		"IPV6NetworkMaskPrefixLength: 64\n" +
   		"HardwareAddress: 00:26:18:34:d0:cb\n" +
   		"MediumType:      Ethernet\n" +
   		"Status:          Up\n" +
   		"VBoxNetworkName: HostInterfaceNetworking-eth0\n" +
   		"\n" +
   		"Name:            vbox0\n" +
   		"GUID:            786f6276-0030-4000-8000-5a3ded993fed\n" +
   		"Dhcp:            Disabled\n" +
   		"IPAddress:       192.168.56.1\n" +
   		"NetworkMask:     255.255.255.0\n" +
   		"IPV6Address: IPV6NetworkMaskPrefixLength: 0\n" +
   		"HardwareAddress: 5a:3d:ed:99:3f:ed\n" +
   		"MediumType:      Ethernet\n" +
   		"Status:          Up\n" +
   		"VBoxNetworkName: HostInterfaceNetworking-vbox0\n";
	
	@Test
	public void testCloneMachineFromAnotherMachine() throws IOException  {
		VirtualBoxManager manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
		ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));
		
		// TODO this should be idempotent
		IMachine master = new IsoToIMachine(manager,
				adminDisk,
				diskFormat,
				settingsFile,
				vmName,
				osTypeId,
				vmId,
				forceOverwrite,
				controllerIDE,
				localHostContext,
				hostId,
				guestId,
				new Credentials("toor", "password")).apply("ubuntu-11.04-server-i386.iso");

		// NB: CloneAndRegisterMachineFromIMachineIfNotAlreadyExists will take care of the followings:
		//		- cloning the master
		//		- register the clone machine
		//		- ensureBridgedNetworkingIsAppliedToMachine(cloneName, macAddress, hostInterface) -> problem in my macosx: all the 
		//      bridged interfaces are up!!
		String cloneName = vmName + "_clone";
		IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists
				(manager, localHostContext, settingsFile, osTypeId, vmId, forceOverwrite, cloneName, hostId).apply(master);
	}
	
	@Test
	public void retrieveAvailableBridgedInterfaceInfoTest() {
		assertFalse(CloneAndRegisterMachineFromIMachineIfNotAlreadyExists.retrieveAvailableBridgedInterfaceInfo(TEST1).getName().isEmpty());
		// TODO enable this test for mac osx
		//assertFalse(CloneAndRegisterMachineFromIMachineIfNotAlreadyExists.retrieveAvailableBridgedInterfaceInfo(TEST2).isEmpty());
	}
	
}
