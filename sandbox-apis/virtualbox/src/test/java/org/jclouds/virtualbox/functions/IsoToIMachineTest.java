/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions;

/**
 * @author Andrea Turli
 */

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.experiment.TestUtils;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.IVirtualBox;
import org.virtualbox_4_1.VirtualBoxManager;

//TODO should it be a live test?
@Test(groups = "unit")
public class IsoToIMachineTest {

	private String settingsFile = "";
	private boolean forceOverwrite = true;
	private String vmId = null;
	private String osTypeId = null;
	private String controllerIDE = "test-IDE";
	private String diskFormat = "";
	private String adminDisk = "testAdmin.vdi";
	private String guestId = "guestId";
	private String hostId = "hostId";

	@Test
	public void testConvert() throws Exception {
		String vmName = "virtualbox-iso-to-machine-test";
		VirtualBoxManager vbm = createNiceMock(VirtualBoxManager.class);
		IVirtualBox vBox = createNiceMock(IVirtualBox.class);
		IMachine vm = createNiceMock(IMachine.class);
		ISession session = createNiceMock(ISession.class);
		expect(vbm.getVBox()).andReturn(vBox).anyTimes();
		expect(vbm.getSessionObject()).andReturn(session).anyTimes();
		expect(vBox.findMachine(vmName)).andReturn(vm).anyTimes();
		expect(
				vBox.createMachine(settingsFile, vmName, osTypeId, vmId,
						forceOverwrite)).andReturn(vm).anyTimes();
		expect(vm.getName()).andReturn(vmName).anyTimes();
		// expect(vm.lockMachine(session, LockType.Write)).and
		expect(session.getMachine()).andReturn(vm).anyTimes();
		replay(vbm, vBox, vm, session);

		ComputeServiceContext context = TestUtils
				.computeServiceForLocalhostAndGuest();
		IMachine iMachine = new IsoToIMachine(vbm, adminDisk, diskFormat,
				settingsFile, vmName, osTypeId, vmId, forceOverwrite,
				controllerIDE, context, hostId, guestId)
				.apply(VirtualBoxConstants.VIRTUALBOX_DISTRO_ISO_NAME);

		assertEquals(iMachine.getName(), vmName);

	}

}
