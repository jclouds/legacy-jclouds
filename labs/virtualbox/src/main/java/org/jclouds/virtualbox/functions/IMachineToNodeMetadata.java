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

import static org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule.machineToNodeState;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.MachineState;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class IMachineToNodeMetadata implements Function<IMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Override
   public NodeMetadata apply(@Nullable IMachine vm) {

      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.name(vm.getName()).ids(vm.getName());

      // TODO Set up location properly
      LocationBuilder locationBuilder = new LocationBuilder();
      locationBuilder.description("");
      locationBuilder.id("");
      locationBuilder.scope(LocationScope.HOST);
      nodeMetadataBuilder.location(locationBuilder.build());

      HardwareBuilder hardwareBuilder = new HardwareBuilder();
      hardwareBuilder.ram(vm.getMemorySize().intValue());

      // TODO: Get more processor information
      Set<Processor> processors = new HashSet<Processor>();
      for (int i = 0; i < vm.getCPUCount(); i++) {
         Processor processor = new Processor(1, 0);
         processors.add(processor);
      }
      hardwareBuilder.processors(processors);

      // TODO: How to get this?
      hardwareBuilder.is64Bit(false);

      nodeMetadataBuilder.hostname(vm.getName());
      

      MachineState vmState = vm.getState();
      NodeState nodeState = machineToNodeState.get(vmState);
      if (nodeState == null)
         nodeState = NodeState.UNRECOGNIZED;
      nodeMetadataBuilder.state(nodeState);

      logger.debug("Setting virtualbox node to: " + nodeState + " from machine state: " + vmState);

      INetworkAdapter networkAdapter = vm.getNetworkAdapter(0l);
      if (networkAdapter != null) {
         nodeMetadataBuilder.privateAddresses(ImmutableSet.of(networkAdapter.getNatDriver().getHostIP()));
         for (String nameProtocolnumberAddressInboudportGuestTargetport : networkAdapter.getNatDriver().getRedirects()){
            Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
            String protocolNumber  = Iterables.get(stuff, 1);
            String hostAddress= Iterables.get(stuff, 2);
            String inboundPort= Iterables.get(stuff, 3);
            String targetPort= Iterables.get(stuff, 5);
            if ("1".equals(protocolNumber) && "22".equals(targetPort))
               nodeMetadataBuilder.privateAddresses(ImmutableSet.of(hostAddress)).loginPort(Integer.parseInt(inboundPort));
         }
      }

      LoginCredentials loginCredentials = new LoginCredentials("toor", "password", null, true);
      nodeMetadataBuilder.credentials(loginCredentials);
      
      return nodeMetadataBuilder.build();
   }

}
