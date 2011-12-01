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
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;

/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "IMachineToIpAddressLiveTest")
public class IMachineToIpAddressLiveTest extends BaseVirtualBoxClientLiveTest {
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
	private String clonedName = "jclouds-image-virtualbox-machine-to-machine-test_clone";
	private String network;
	private String snapshotName = "snap";
	private String snapshotDesc = "snapDesc";

	@Test
	public void testConvert() throws IOException {
		manager = (VirtualBoxManager) context.getProviderSpecificContext()
				.getApi();
		ComputeServiceContext localContext = computeServiceForLocalhostAndGuest(
				hostId, "localhost", guestId, "localhost", new Credentials("toor",
						"password"));

		VirtualBoxManager manager = (VirtualBoxManager) context
				.getProviderSpecificContext().getApi();
		ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest(
				hostId, "localhost", guestId, "localhost", new Credentials("toor",
						"password"));

		
		try {
			  InetAddress localhost = InetAddress.getLocalHost();
			  System.out.println(" IP Addr: " + localhost.getHostAddress());
			  // Just in case this host has multiple IP addresses....
			  InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
			  //InetAddresses2.isPrivateIPAddress(in)
			  
			  System.out.println("-- " + InetAddress.getLocalHost());
			  
			  if (allMyIps != null && allMyIps.length > 1) {
				  System.out.println(" Full list of IP addresses:");
			    for (int i = 0; i < allMyIps.length; i++) {
			   	 System.out.println("    " + allMyIps[i]);
			    }
			  }
			} catch (UnknownHostException e) {
				 System.out.println(" (error retrieving server host name)");
			}


		
		
		IMachine master = null;
		try {
			Predicate<IPSocket> socketTester = new RetryablePredicate<IPSocket>(
					new InetSocketAddressConnect(), 10, 1, TimeUnit.SECONDS);
			master = new IsoToIMachine(manager, adminDisk, diskFormat,
					settingsFile, vmName, osTypeId, vmId, forceOverwrite,
					controllerIDE, localHostContext, hostId, guestId, socketTester,
					"127.0.0.1", 8080).apply("ubuntu-11.04-server-i386.iso");
		} catch (IllegalStateException e) {
			// already created
			master = manager.getVBox().findMachine(vmName);
		}

		IMachine clone = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(
				manager, localContext, "", "", "", false, clonedName, hostId,
				snapshotName, snapshotDesc, controllerIDE).apply(master);

		// TODO launch machine + check it is ready
		IProgress prog = clone.launchVMProcess(manager.getSessionObject(),
				ExecutionType.GUI.stringValue(), "");
		prog.waitForCompletion(-1);
		
		Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces();

		for (NetworkInterface networkInterface : Collections.list(interfaces)) {
			if(networkInterface.isUp())
				for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
					if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
						network = inetAddress.getHostAddress();
				}
		}

		String ipAddress = new IMachineToIpAddress(localContext, hostId, network)
				.apply(clone);
		assertTrue(!ipAddress.isEmpty());
	}
}