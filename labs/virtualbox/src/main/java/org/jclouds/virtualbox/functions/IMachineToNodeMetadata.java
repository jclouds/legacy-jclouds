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
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.collections.Lists;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.NetworkAttachmentType;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class IMachineToNodeMetadata implements Function<IMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final MachineUtils machineUtils;

   @Inject
   public IMachineToNodeMetadata(MachineUtils machineUtils) {
      this.machineUtils = machineUtils;
   }
   
   @Override
   public NodeMetadata apply(@Nullable IMachine vm) {

      String group = "";
      String name = "";
      String[] encodedInVmName = vm.getName().split(VIRTUALBOX_NODE_NAME_SEPARATOR);
      if (vm.getName().startsWith(VIRTUALBOX_NODE_PREFIX)){
         group = encodedInVmName[2];
         name = encodedInVmName[3];
      } else {
         name = encodedInVmName[1];
      }
      
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.name(name).ids(vm.getName()).group(group);

      // TODO Set up location properly
      LocationBuilder locationBuilder = new LocationBuilder();
      locationBuilder.description("");
      locationBuilder.id("");
      locationBuilder.scope(LocationScope.HOST);
      nodeMetadataBuilder.location(locationBuilder.build());
      nodeMetadataBuilder.hostname(vm.getName());

      MachineState vmState = vm.getState();
      NodeState nodeState = machineToNodeState.get(vmState);
      if (nodeState == null)
         nodeState = NodeState.UNRECOGNIZED;
      nodeMetadataBuilder.state(nodeState);

      logger.debug("Setting virtualbox node to: " + nodeState + " from machine state: " + vmState);

      /*
      // nat adapter
      INetworkAdapter natAdapter = vm.getNetworkAdapter(0l);
      checkNotNull(natAdapter, "slot 0 networkadapter");
      checkState(natAdapter.getAttachmentType() == NetworkAttachmentType.NAT,
               "expecting slot 0 to be a NAT attachment type (was: " + natAdapter.getAttachmentType() + ")");

      int ipTermination = 0;
      int inPort = 0;
      String hostAddress = "";

      nodeMetadataBuilder.publicAddresses(ImmutableSet.of(natAdapter.getNatDriver().getHostIP()));
      for (String nameProtocolnumberAddressInboudportGuestTargetport : natAdapter.getNatDriver().getRedirects()) {
         Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
         String protocolNumber = Iterables.get(stuff, 1);
         hostAddress = Iterables.get(stuff, 2);
         String inboundPort = Iterables.get(stuff, 3);
         String targetPort = Iterables.get(stuff, 5);
         if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
            inPort = Integer.parseInt(inboundPort);
            ipTermination = inPort % NodeCreator.NODE_PORT_INIT + 2;
         }
      }
      
      // only masters use 2222 port
      if (inPort == MastersLoadingCache.MASTER_PORT) {
         nodeMetadataBuilder.publicAddresses(ImmutableSet.of(hostAddress)).loginPort(inPort);
      } else {
         nodeMetadataBuilder.privateAddresses(ImmutableSet.of((NodeCreator.VMS_NETWORK + ipTermination) + ""));
         nodeMetadataBuilder.publicAddresses(ImmutableSet.of((NodeCreator.VMS_NETWORK + ipTermination) + ""));
      } 
      */
      
      nodeMetadataBuilder = getIpAddresses(vm, nodeMetadataBuilder);
      LoginCredentials loginCredentials = new LoginCredentials("toor", "password", null, true);
      nodeMetadataBuilder.credentials(loginCredentials);

      return nodeMetadataBuilder.build();
   }
   
   private NodeMetadataBuilder getIpAddresses(IMachine vm, NodeMetadataBuilder nodeMetadataBuilder) {
      List<String> publicIpAddresses = Lists.newArrayList();
      List<String> privateIpAddresses = Lists.newArrayList();

      for(long slot = 0; slot < 4; slot ++) {
         INetworkAdapter adapter = vm.getNetworkAdapter(slot);
         if(adapter != null) {
            if (adapter.getAttachmentType() == NetworkAttachmentType.NAT) {
               publicIpAddresses.add(adapter.getNatDriver().getHostIP());
               for (String nameProtocolnumberAddressInboudportGuestTargetport : adapter.getNatDriver().getRedirects()) {
                  Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
                  String protocolNumber = Iterables.get(stuff, 1);
                  String hostAddress = Iterables.get(stuff, 2);
                  String inboundPort = Iterables.get(stuff, 3);
                  String targetPort = Iterables.get(stuff, 5);
                  if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
                     int inPort = Integer.parseInt(inboundPort);
                     nodeMetadataBuilder.publicAddresses(ImmutableSet.of(hostAddress)).loginPort(inPort);
                  }
                  //privateIpAddresses.add((NodeCreator.VMS_NETWORK + ipTermination) + "");
               }
               // TODO this could be a public and private address
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.Bridged) {
               String clientIpAddress = machineUtils.getIpAddressFromBridgedNIC(vm.getName());
               //privateIpAddresses.add(clientIpAddress);
               publicIpAddresses.add(clientIpAddress);

            } else if (adapter.getAttachmentType() == NetworkAttachmentType.HostOnly) {
               String clientIpAddress = machineUtils.getIpAddressFromHostOnlyNIC(vm.getName());
               publicIpAddresses.add(clientIpAddress);

            }
         }
      }
      nodeMetadataBuilder.publicAddresses(publicIpAddresses);
      nodeMetadataBuilder.privateAddresses(publicIpAddresses);

      return nodeMetadataBuilder;
   }   

}
