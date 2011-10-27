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

import java.io.IOException;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;


/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Andrea Turli
 */
@Test(groups = "live", singleThreaded = true, testName = "IMachineToIpAddressTest")
public class IMachineToIpAddressTest extends BaseVirtualBoxClientLiveTest {

   private String hostId = "host";
	private String guestId = "guest";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private String clonedName = "jclouds-image-virtualbox-machine-to-machine-test_clone";
   private VirtualBoxManager manager;
	
	  @Test
	  public void testConvert() throws IOException {
	      manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
			ComputeServiceContext localContext = computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));
	      // TODO ensure a vm with bridged NIC is running
			IMachine master = manager.getVBox().findMachine(vmName);
			IMachine cloned = new CloneAndRegisterMachineFromIMachineIfNotAlreadyExists(manager, localContext, "", "", "", false, clonedName, hostId).apply(master);
			// TODO discover the bridged network 
	      String ipAddress = new IMachineToIpAddress(localContext, hostId).apply(cloned);
	      // TODO assert ip address is ssh-able
	  }
	  
	  
}
