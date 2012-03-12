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
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.statements.SetIpAddress;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

@Singleton
public class NodeCreator implements Function<NodeSpec, NodeAndInitialCredentials<IMachine>> {

   // TODO parameterize
   public static final int NODE_PORT_INIT = 3000;

   // TODO parameterize
   public static final String VMS_NETWORK = "33.33.33.";

   // TODO parameterize
   public static final String HOST_ONLY_IFACE_NAME = "vboxnet0";

   // TODO parameterize
   public static final boolean USE_LINKED = true;
   
   // TODO parameterize
   public static final ExecutionType executionType = ExecutionType.GUI;

   private final Supplier<VirtualBoxManager> manager;
   private final Function<CloneSpec, IMachine> cloner;
   private final AtomicInteger nodes;
   private MachineUtils machineUtils;
   private Function<IMachine, NodeMetadata> imachineToNodeMetadata;

   @Inject
   public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner,
            MachineUtils machineUtils, Function<IMachine, NodeMetadata> imachineToNodeMetadata) {
      this.manager = manager;
      this.cloner = cloner;
      this.nodes = new AtomicInteger(0);
      this.machineUtils = machineUtils;
      this.imachineToNodeMetadata = imachineToNodeMetadata;
   }

   /**
    * Creates a clone based on the {@link NodeSpec}. It is synchronized because it needs sole access
    * to the master. Could be improved by locking on a master basis (would allow concurrent cloning
    * as long as form different masters."
    */
   @Override
   public synchronized NodeAndInitialCredentials<IMachine> apply(NodeSpec nodeSpec) {

      checkNotNull(nodeSpec, "NodeSpec");

      Master master = nodeSpec.getMaster();
      checkNotNull(master, "Master");

      if (master.getMachine().getCurrentSnapshot() != null) {
         ISession session;
         try {
            session = manager.get().openMachineSession(master.getMachine());
         } catch (Exception e) {
            throw new RuntimeException("error opening vbox machine session: " + e.getMessage(), e);
         }
         session.getConsole().deleteSnapshot(master.getMachine().getCurrentSnapshot().getId());
         session.unlockMachine();
      }

      String masterNameWithoutPrefix = master.getSpec().getVmSpec().getVmName().replace(VIRTUALBOX_IMAGE_PREFIX, "");

      String cloneName = VIRTUALBOX_NODE_PREFIX + masterNameWithoutPrefix + "-" + nodeSpec.getTag() + "-"
               + nodeSpec.getName();

      VmSpec cloneVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(512).cleanUpMode(CleanupMode.Full)
               .forceOverwrite(true).build();

      NetworkAdapter natAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT)
               .tcpRedirectRule("127.0.0.1", NODE_PORT_INIT + this.nodes.getAndIncrement(), "", 22).build();

      NetworkInterfaceCard natIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(natAdapter).slot(0L).build();

      NetworkAdapter hostOnlyAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.HostOnly)
               .staticIp(VMS_NETWORK + this.nodes.getAndIncrement()).build();

      NetworkInterfaceCard hostOnlyIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(hostOnlyAdapter)
               .addHostInterfaceName(HOST_ONLY_IFACE_NAME).slot(1L).build();

      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(natIfaceCard).addNIC(hostOnlyIfaceCard).build();

      CloneSpec cloneSpec = CloneSpec.builder().linked(USE_LINKED).master(master.getMachine()).network(networkSpec)
               .vm(cloneVmSpec).build();

      IMachine cloned = cloner.apply(cloneSpec);

      new LaunchMachineIfNotAlreadyRunning(manager.get(), executionType, "").apply(cloned);

      machineUtils.runScriptOnNode(imachineToNodeMetadata.apply(cloned), new SetIpAddress(hostOnlyIfaceCard),
               RunScriptOptions.NONE);

      // TODO get credentials from somewhere else (they are also HC in IMachineToSshClient)
      NodeAndInitialCredentials<IMachine> nodeAndInitialCredentials = new NodeAndInitialCredentials<IMachine>(cloned,
               cloneName, LoginCredentials.builder().user("toor").password("password").authenticateSudo(true).build());

      return nodeAndInitialCredentials;
   }
}
