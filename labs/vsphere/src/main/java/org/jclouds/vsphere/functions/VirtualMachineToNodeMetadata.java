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

import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_PREFIX;
import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_SEPARATOR;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Map<VirtualMachinePowerState, Status> toPortableNodeStatus;

   @Inject
   public VirtualMachineToNodeMetadata(Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus) {
      this.toPortableNodeStatus = toPortableNodeStatus;
   }
   
   @Override
   public NodeMetadata apply(@Nullable VirtualMachine vm) {
      
      String group = "";
      String[] encodedInVmName = vm.getName().split(VSPHERE_SEPARATOR);
      if (vm.getName().startsWith(VSPHERE_PREFIX)){
         group = encodedInVmName[0].substring(VSPHERE_PREFIX.length());
      }
      
      // TODO Set up location properly
      LocationBuilder locationBuilder = new LocationBuilder();
      locationBuilder.description("");
      locationBuilder.id("");
      locationBuilder.scope(LocationScope.HOST);
      
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.name(vm.getName())
         .ids(vm.getName())
         .location(locationBuilder.build())
         .group(group)
         .hostname(vm.getName());
      
      String clientIpAddress = Strings.nullToEmpty(vm.getGuest().getIpAddress());
      if (vm.getGuest().getToolsStatus().equals(VirtualMachineToolsStatus.toolsNotInstalled))
         logger.debug("No VMware tools installed");
      else {
         while (!vm.getRuntime().getPowerState().equals(VirtualMachinePowerState.poweredOff)
                  && !vm.getGuest().getToolsStatus().equals(VirtualMachineToolsStatus.toolsOk)) {
            try {
               Thread.sleep(1000l);
               clientIpAddress = Strings.nullToEmpty(vm.getGuest().getIpAddress());
            } catch (InterruptedException e) {
               logger.error("Problem in waiting vmware tools", e);
            }
         }
         nodeMetadataBuilder.publicAddresses(ImmutableList.of(clientIpAddress));
         nodeMetadataBuilder.privateAddresses(ImmutableList.of(clientIpAddress));
      }
      VirtualMachinePowerState vmState = vm.getRuntime().getPowerState();
      NodeMetadata.Status nodeState = toPortableNodeStatus.get(vmState);
      if (nodeState == null)
         nodeState = Status.UNRECOGNIZED;
      nodeMetadataBuilder.status(nodeState);
      return nodeMetadataBuilder.build();
   }
}
