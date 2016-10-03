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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {
   
   private final Map<VirtualMachinePowerState, Status> toPortableNodeStatus;
   private final GroupNamingConvention.Factory namingConvention;

   @Inject
   public VirtualMachineToNodeMetadata(Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus,
           GroupNamingConvention.Factory namingConvention) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.namingConvention = namingConvention;
   }
   
	@Override
	public NodeMetadata apply(VirtualMachine vm) {

		String group = namingConvention.create().extractGroup(vm.getName());

		// TODO Set up location properly
		LocationBuilder locationBuilder = new LocationBuilder();
		locationBuilder.description("");
		locationBuilder.id("");
		locationBuilder.scope(LocationScope.HOST);

		NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
		nodeMetadataBuilder.name(vm.getName()).ids(vm.getName())
				.location(locationBuilder.build()).group(group)
				.hostname(vm.getName());

		String clientIpAddress = Strings.nullToEmpty(vm.getGuest()
				.getIpAddress());
		boolean passed = false;
		if (!vm.getGuest().getToolsStatus()
				.equals(VirtualMachineToolsStatus.toolsNotInstalled)) {
			int timeoutValue = 1000, timeoutUnits = 500;
			RetryablePredicate<String> tester = new RetryablePredicate<String>(
					ipAddressTester, timeoutValue, timeoutUnits,
					TimeUnit.MILLISECONDS);
			while (vm.getRuntime().getPowerState()
					.equals(VirtualMachinePowerState.poweredOn)
					&& !passed) {
				clientIpAddress = Strings.nullToEmpty(vm.getGuest()
						.getIpAddress());
				passed = tester.apply(clientIpAddress);
			}

			nodeMetadataBuilder.publicAddresses(ImmutableList
					.of(clientIpAddress));
			nodeMetadataBuilder.privateAddresses(ImmutableList
					.of(clientIpAddress));
		}
		VirtualMachinePowerState vmState = vm.getRuntime().getPowerState();
		NodeMetadata.Status nodeState = toPortableNodeStatus.get(vmState);
		if (nodeState == null)
			nodeState = Status.UNRECOGNIZED;
		nodeMetadataBuilder.status(nodeState);
		return nodeMetadataBuilder.build();
	}
	
	Predicate<String> ipAddressTester = new Predicate<String>() {

		@Override
		public boolean apply(String input) {
			return !input.isEmpty();
		}
		
	};
}