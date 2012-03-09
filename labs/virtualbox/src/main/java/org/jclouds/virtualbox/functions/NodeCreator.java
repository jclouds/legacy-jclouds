/**
mh * Licensed to jclouds, Inc. (jclouds) under one or more
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

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

@Singleton
public class NodeCreator implements Function<NodeSpec, NodeAndInitialCredentials<IMachine>> {

   private final Supplier<VirtualBoxManager> manager;
   private final Function<CloneSpec, IMachine> cloner;

   @Inject
   public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner,
         MachineUtils machineUtils) {
      this.manager = manager;
      this.cloner = cloner;
   }

   @Override
   public NodeAndInitialCredentials<IMachine> apply(NodeSpec nodeSpec) {

      Master master = nodeSpec.getMaster();

      String masterNameWithoutPrefix = master.getSpec().getVmSpec().getVmName().replace(VIRTUALBOX_IMAGE_PREFIX, "");

      String cloneName = VIRTUALBOX_NODE_PREFIX + masterNameWithoutPrefix + "-" + nodeSpec.getTag() + "-"
            + nodeSpec.getName();

      CleanupMode mode = CleanupMode.Full;

      VmSpec clonedVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(512).cleanUpMode(mode)
            .forceOverwrite(true).build();
      
      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.Bridged)
            .build();
      
      // TODO use RetrieveActiveBridgedInterface
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().slot(0L).addNetworkAdapter(networkAdapter)
            .addHostInterfaceName("en1: Wi-Fi (AirPort)")
            .build();
      NetworkSpec cloneNetworkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();

      CloneSpec cloneSpec = CloneSpec.builder().vm(clonedVmSpec).network(cloneNetworkSpec).master(master.getMachine()).linked(true)
            .build();

      IMachine cloned = cloner.apply(cloneSpec);

      new LaunchMachineIfNotAlreadyRunning(manager.get(), ExecutionType.GUI, "").apply(cloned);

      // TODO get credentials from somewhere else (they are also HC in
      // IMachineToSshClient)
      NodeAndInitialCredentials<IMachine> nodeAndInitialCredentials = new NodeAndInitialCredentials<IMachine>(cloned,
            cloneName, LoginCredentials.builder().user("toor").password("password").authenticateSudo(true).build());

      return nodeAndInitialCredentials;
   }
}
