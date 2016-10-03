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
package org.jclouds.vsphere.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.VirtualMachine;
@Singleton
public class VirtualMachineToIpAddress implements Function<VirtualMachine, String> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	@Override
	public String apply(final VirtualMachine vm) {
		String clientIpAddress = vm.getGuest().getIpAddress();
		while(!vm.getGuest().getToolsStatus().equals(VirtualMachineToolsStatus.toolsOk) || clientIpAddress.isEmpty()) {
			int timeoutValue = 1000, timeoutUnits = 500;
			RetryablePredicate<String> tester = new RetryablePredicate<String>(
					ipAddressTester, timeoutValue, timeoutUnits,
					TimeUnit.MILLISECONDS);
			boolean passed = false;
			while (vm.getRuntime().getPowerState()
					.equals(VirtualMachinePowerState.poweredOn)
					&& !passed) {
				clientIpAddress = Strings.nullToEmpty(vm.getGuest()
						.getIpAddress());
				passed = tester.apply(clientIpAddress);
			}
		}

		return checkNotNull(clientIpAddress, "clientIpAddress");
	}
	
	Predicate<String> ipAddressTester = new Predicate<String>() {

		@Override
		public boolean apply(String input) {
			return !input.isEmpty();
		}
		
	};

}