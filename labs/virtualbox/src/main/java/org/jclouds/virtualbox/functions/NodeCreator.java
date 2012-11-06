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
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_PASSWORD;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_USER;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_GUEST_MEMORY;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.statements.DeleteGShadowLock;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Creates nodes, by cloning a master vm and based on the provided {@link NodeSpec}. Must be
 * synchronized mainly because of snapshot creation (must be synchronized on a per-master-basis).
 * 
 * @author David Alves, Andrea Turli
 * 
 */
@Singleton
public class NodeCreator implements Function<NodeSpec, NodeAndInitialCredentials<IMachine>> {
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Supplier<VirtualBoxManager> manager;
   private final Function<CloneSpec, IMachine> cloner;
   private final MachineUtils machineUtils;
   private final MachineController machineController;
   private final NetworkUtils networkUtils;
   private final int ram;
   
   
   @Inject
   public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner,
            MachineUtils machineUtils, RunScriptOnNode.Factory scriptRunnerFactory, MachineController machineController,
            NetworkUtils networkUtils,
            @Named(VIRTUALBOX_GUEST_MEMORY) String ram) {
      this.manager = manager;
      this.cloner = cloner;
      this.networkUtils = networkUtils;
      this.machineUtils = machineUtils;
      this.machineController = machineController;
      this.ram = Integer.valueOf(ram);
   }

   @Override
   public synchronized NodeAndInitialCredentials<IMachine> apply(NodeSpec nodeSpec) {
      checkNotNull(nodeSpec, "NodeSpec");
      Master master = checkNotNull(nodeSpec.getMaster(), "Master");
      
      if (master.getMachine().getCurrentSnapshot() != null) {
         ISession session;
         try {
            session = manager.get().getSessionObject();
            master.getMachine().lockMachine(session, LockType.Write);
            IProgress progress = session.getConsole().deleteSnapshot(master.getMachine().getCurrentSnapshot().getId());
            progress.waitForCompletion(-1);
            session.unlockMachine();
         } catch (Exception e) {
            throw new RuntimeException("error opening vbox machine session: " + e.getMessage(), e);
         }
         logger.debug("Deleted an existing snapshot from %s", master.getMachine().getName());
      }
      String masterNameWithoutPrefix = master.getMachine().getName().replace(VIRTUALBOX_IMAGE_PREFIX, "");
      String cloneName = VIRTUALBOX_NODE_PREFIX + masterNameWithoutPrefix + VIRTUALBOX_NODE_NAME_SEPARATOR
               + nodeSpec.getTag() + VIRTUALBOX_NODE_NAME_SEPARATOR + nodeSpec.getName();
      
      IMachine masterMachine = master.getMachine();
      String username = masterMachine.getExtraData(GUEST_OS_USER);
      String password = masterMachine.getExtraData(GUEST_OS_PASSWORD);
      
      VmSpec cloneVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(ram)
            .guestUser(username).guestPassword(password)
            .cleanUpMode(CleanupMode.Full)
            .forceOverwrite(true).build();
      
      // case 'vbox host is localhost': NAT + HOST-ONLY
      NetworkSpec networkSpec = networkUtils.createNetworkSpecWhenVboxIsLocalhost();
      Optional<NetworkInterfaceCard> optionalNatIfaceCard = Iterables.tryFind(
            networkSpec.getNetworkInterfaceCards(),
            new Predicate<NetworkInterfaceCard>() {

               @Override
               public boolean apply(NetworkInterfaceCard nic) {
                  return nic.getNetworkAdapter().getNetworkAttachmentType()
                        .equals(NetworkAttachmentType.NAT);
               }
            });
      CloneSpec cloneSpec = CloneSpec.builder().linked(true).master(master.getMachine()).network(networkSpec)
               .vm(cloneVmSpec).build();

      logger.debug("Cloning a new guest an existing snapshot from %s ...", master.getMachine().getName());
      IMachine cloned = cloner.apply(cloneSpec);
      machineController.ensureMachineIsLaunched(cloneVmSpec.getVmName());
      
      // IMachineToNodeMetadata produces the final ip's but these need to be set before so we build a
      // NodeMetadata just for the sake of running the gshadow and setip scripts 
      NodeMetadata partialNodeMetadata = buildPartialNodeMetadata(cloned);

      // see DeleteGShadowLock for a detailed explanation
       machineUtils.runScriptOnNode(partialNodeMetadata, new DeleteGShadowLock(), RunScriptOptions.NONE);

      if(optionalNatIfaceCard.isPresent())
         checkState(networkUtils.enableNetworkInterface(partialNodeMetadata, optionalNatIfaceCard.get()),
         "cannot enable Nat Interface");
      
      LoginCredentials credentials = partialNodeMetadata.getCredentials();
      return new NodeAndInitialCredentials<IMachine>(cloned,
               cloneName, credentials);
   }

   private NodeMetadata buildPartialNodeMetadata(IMachine clone) {
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.id(clone.getName());
      nodeMetadataBuilder.status(VirtualBoxComputeServiceContextModule.toPortableNodeStatus.get(clone.getState()));
      long slot = findSlotForNetworkAttachment(clone, NetworkAttachmentType.HostOnly);
      nodeMetadataBuilder.publicAddresses(ImmutableSet.of(networkUtils.getIpAddressFromNicSlot(clone.getName(), slot)));
      String guestOsUser = clone.getExtraData(GUEST_OS_USER);
      String guestOsPassword = clone.getExtraData(GUEST_OS_PASSWORD);
      LoginCredentials loginCredentials = new LoginCredentials(guestOsUser, guestOsPassword, null, true);
      nodeMetadataBuilder.credentials(loginCredentials);    
      return  nodeMetadataBuilder.build();
   }

   private long findSlotForNetworkAttachment(IMachine clone, NetworkAttachmentType networkAttachmentType) {
      long slot = -1;
      long i = 0;
      while (slot == -1 && i < 4) {
         if(clone.getNetworkAdapter(i).getAttachmentType().equals(networkAttachmentType))
            slot = i;
         i++;
      }
      checkState(slot!=-1);
      return slot;
   }


}
