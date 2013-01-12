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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_PASSWORD;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_USER;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.NetworkAttachmentType;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class IMachineToNodeMetadata implements Function<IMachine, NodeMetadata> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Supplier<VirtualBoxManager> virtualboxManager;
   private final Map<MachineState, Status> toPortableNodeStatus;
   private final NetworkUtils networkUtils;
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public IMachineToNodeMetadata(Supplier<VirtualBoxManager> virtualboxManager, Map<MachineState, NodeMetadata.Status> toPortableNodeStatus, 
         NetworkUtils networkUtils, Map<OsFamily, Map<String, String>> osVersionMap) {
      this.virtualboxManager = checkNotNull(virtualboxManager, "virtualboxManager");
      this.toPortableNodeStatus = checkNotNull(toPortableNodeStatus, "toPortableNodeStatus");
      this.networkUtils = checkNotNull(networkUtils, "networkUtils");
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
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
      NodeMetadata.Status nodeState = toPortableNodeStatus.get(vmState);
      if (nodeState == null)
         nodeState = Status.UNRECOGNIZED;
      nodeMetadataBuilder.status(nodeState);
      nodeMetadataBuilder = getIpAddresses(vm, nodeMetadataBuilder);
      
      IGuestOSType guestOSType = virtualboxManager.get().getVBox().getGuestOSType(vm.getOSTypeId());
      OsFamily family = parseOsFamilyOrUnrecognized(guestOSType.getDescription());
      String version = parseVersionOrReturnEmptyString(family, guestOSType.getDescription(), osVersionMap);
      OperatingSystem os = OperatingSystem.builder().description(guestOSType.getDescription()).family(family)
               .version(version).is64Bit(guestOSType.getIs64Bit()).build();
      nodeMetadataBuilder.operatingSystem(os);

      String guestOsUser = vm.getExtraData(GUEST_OS_USER);
      String guestOsPassword = vm.getExtraData(GUEST_OS_PASSWORD);
      nodeMetadataBuilder.credentials(LoginCredentials.builder()
                                                      .user(guestOsUser)
                                                      .password(guestOsPassword)
                                                      .authenticateSudo(true).build());
      return nodeMetadataBuilder.build();
   }
   
   private NodeMetadataBuilder getIpAddresses(IMachine vm, NodeMetadataBuilder nodeMetadataBuilder) {
      List<String> publicIpAddresses = Lists.newArrayList();
      List<String> privateIpAddresses = Lists.newArrayList();
      for(long slot = 0; slot < 4; slot ++) {
         INetworkAdapter adapter = vm.getNetworkAdapter(slot);
         if(adapter != null) {
            if (adapter.getAttachmentType() == NetworkAttachmentType.NAT) {
               String hostIP = adapter.getNATEngine().getHostIP();
               if(!hostIP.isEmpty())
                  publicIpAddresses.add(hostIP);
               for (String nameProtocolnumberAddressInboudportGuestTargetport : adapter.getNATEngine().getRedirects()) {
                  Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
                  String protocolNumber = Iterables.get(stuff, 1);
                  String hostAddress = Iterables.get(stuff, 2);
                  String inboundPort = Iterables.get(stuff, 3);
                  String targetPort = Iterables.get(stuff, 5);
                  if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
                     int inPort = Integer.parseInt(inboundPort);
                     publicIpAddresses.add(hostAddress);
                     nodeMetadataBuilder.loginPort(inPort);
                  }
               }
            } else if (adapter.getAttachmentType() == NetworkAttachmentType.Bridged) {
               String clientIpAddress = networkUtils.getIpAddressFromNicSlot(vm.getName(), adapter.getSlot());
               privateIpAddresses.add(clientIpAddress);

            } else if (adapter.getAttachmentType() == NetworkAttachmentType.HostOnly) {
               String clientIpAddress = networkUtils.getValidHostOnlyIpFromVm(vm.getName());             
               publicIpAddresses.add(clientIpAddress);
            }
         }
      }
      nodeMetadataBuilder.publicAddresses(publicIpAddresses);
      nodeMetadataBuilder.privateAddresses(publicIpAddresses);
      return nodeMetadataBuilder;
   }   

}
