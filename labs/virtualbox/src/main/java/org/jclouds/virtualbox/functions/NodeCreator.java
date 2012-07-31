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
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.statements.DeleteGShadowLock;
import org.jclouds.virtualbox.statements.EnableNetworkInterface;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.HostNetworkInterfaceType;
import org.virtualbox_4_1.IDHCPServer;
import org.virtualbox_4_1.IHostNetworkInterface;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
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

   private final Supplier<VirtualBoxManager> manager;
   private final Function<CloneSpec, IMachine> cloner;
   private final MachineUtils machineUtils;
   private final MachineController machineController;
   private final Factory runScriptOnNodeFactory;
   private final Supplier<NodeMetadata> host;


   @Inject
   public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner,  Factory runScriptOnNodeFactory,
            Supplier<NodeMetadata> host,
            MachineUtils machineUtils, RunScriptOnNode.Factory scriptRunnerFactory, MachineController machineController) {
      this.manager = manager;
      this.cloner = cloner;
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.host = checkNotNull(host, "host");

      this.machineUtils = machineUtils;
      this.machineController = machineController;
   }

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
         IProgress progress = session.getConsole().deleteSnapshot(master.getMachine().getCurrentSnapshot().getId());
         progress.waitForCompletion(-1);
         session.unlockMachine();
      }
      String masterNameWithoutPrefix = master.getMachine().getName().replace(VIRTUALBOX_IMAGE_PREFIX, "");

      String cloneName = VIRTUALBOX_NODE_PREFIX + masterNameWithoutPrefix + VIRTUALBOX_NODE_NAME_SEPARATOR
               + nodeSpec.getTag() + VIRTUALBOX_NODE_NAME_SEPARATOR + nodeSpec.getName();

      int ram = 512;
      if (nodeSpec.getTemplate() != null && nodeSpec.getTemplate().getHardware() != null
               && nodeSpec.getTemplate().getHardware().getRam() > 0) {
         ram = nodeSpec.getTemplate().getHardware().getRam();
      }
      
      VmSpec cloneVmSpec = VmSpec.builder().id(cloneName).name(cloneName).memoryMB(ram).cleanUpMode(CleanupMode.Full)
               .forceOverwrite(true).build();

      // CASE NAT + HOST-ONLY
      NetworkAdapter natAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT)
               .build();
      NetworkInterfaceCard natIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(natAdapter).slot(1L).build();

      NetworkAdapter hostOnlyAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.HostOnly)
               .build();

      // create new hostOnly interface if needed, otherwise use the one already there with dhcp enabled ...
      String hostOnlyIfName = getHostOnlyIfOrCreate();
      
      NetworkInterfaceCard hostOnlyIfaceCard = NetworkInterfaceCard.builder().addNetworkAdapter(hostOnlyAdapter)
               .addHostInterfaceName(hostOnlyIfName).slot(0L).build();

      NetworkSpec networkSpec = createNetworkSpecForHostOnlyNATNICs(natIfaceCard, hostOnlyIfaceCard);

      CloneSpec cloneSpec = CloneSpec.builder().linked(true).master(master.getMachine()).network(networkSpec)
               .vm(cloneVmSpec).build();

      IMachine cloned = cloner.apply(cloneSpec);

      machineController.ensureMachineIsLaunched(cloneVmSpec.getVmName());

      // IMachineToNodeMetadata produces the final ip's but these need to be set before so we build a
      // NodeMetadata just for the sake of running the gshadow and setip scripts 
      NodeMetadata partialNodeMetadata = buildPartialNodeMetadata(cloned);

      // see DeleteGShadowLock for a detailed explanation
      machineUtils.runScriptOnNode(partialNodeMetadata, new DeleteGShadowLock(), RunScriptOptions.NONE);

      // CASE NAT + HOST-ONLY
      machineUtils.runScriptOnNode(partialNodeMetadata, new EnableNetworkInterface(natIfaceCard), RunScriptOptions.NONE);
      

      // TODO get credentials from somewhere else (they are also HC in
      // IMachineToSshClient)
      NodeAndInitialCredentials<IMachine> nodeAndInitialCredentials = new NodeAndInitialCredentials<IMachine>(cloned,
               cloneName, LoginCredentials.builder().user("toor").password("password").authenticateSudo(true).build());

      return nodeAndInitialCredentials;
   }
   
   private NodeMetadata buildPartialNodeMetadata(IMachine clone) {
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.id(clone.getName());
      nodeMetadataBuilder.status(VirtualBoxComputeServiceContextModule.toPortableNodeStatus.get(clone.getState()));
      nodeMetadataBuilder.publicAddresses(ImmutableSet.of(machineUtils.getIpAddressFromHostOnlyNIC(clone.getName())));
      
      LoginCredentials loginCredentials = new LoginCredentials("toor", "password", null, true);
      nodeMetadataBuilder.credentials(loginCredentials);
      
      return  nodeMetadataBuilder.build();
   }

   private NetworkSpec createNetworkSpecForHostOnlyNATNICs(NetworkInterfaceCard natIfaceCard,
            NetworkInterfaceCard hostOnlyIfaceCard) {
      return NetworkSpec.builder().addNIC(natIfaceCard).addNIC(hostOnlyIfaceCard).build();
   }
   
   /**
    * @return
    */
   private String getHostOnlyIfOrCreate() {     
      
      IHostNetworkInterface availableHostInterfaceIf = returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(manager
               .get().getVBox().getHost().getNetworkInterfaces());
      if (availableHostInterfaceIf==null) {
         final String hostOnlyIfName = createHostOnlyIf();
         assignDHCPtoHostOnlyInterface(hostOnlyIfName);
         return hostOnlyIfName;
      } else {
         return availableHostInterfaceIf.getName();
      }
      
   }

   private void assignDHCPtoHostOnlyInterface(final String hostOnlyIfName) {
      List<IHostNetworkInterface> availableNetworkInterfaces = manager.get().getVBox().getHost()
               .getNetworkInterfaces();
      
      IHostNetworkInterface iHostNetworkInterfaceWithHostOnlyIfName = Iterables.getOnlyElement(Iterables.filter(availableNetworkInterfaces, new Predicate<IHostNetworkInterface>() {

         @Override
         public boolean apply(IHostNetworkInterface iHostNetworkInterface) {
            return iHostNetworkInterface.getName().equals(hostOnlyIfName);
         }
      }));
      
      String hostOnlyIfIpAddress = iHostNetworkInterfaceWithHostOnlyIfName.getIPAddress();
      String dhcpIpAddress = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".254";
      String dhcpNetmask = "255.255.255.0";
      String dhcpLowerIp = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".2";
      String dhcpUpperIp = hostOnlyIfIpAddress.substring(0, hostOnlyIfIpAddress.lastIndexOf(".")) + ".253";
      
      ExecResponse response = runScriptOnNodeFactory
               .create(host.get(),
                        Statements.exec(String
                                 .format("VBoxManage dhcpserver add --ifname %s --ip %s --netmask %s --lowerip %s --upperip %s --enable",
                                          hostOnlyIfName, dhcpIpAddress, dhcpNetmask, dhcpLowerIp, dhcpUpperIp)), runAsRoot(false).wrapInInitScript(false)).init().call();
      checkState(response.getExitStatus()==0);
      /*
      runScriptOnNodeFactory
               .create(host.get(),
                        Statements.exec(String.format("VBoxManage hostonlyif ipconfig %s --ip %s",
                                 hostOnlyIfName, hostOnlyIfIpAddress)), runAsRoot(false).wrapInInitScript(false)).init().call();
      */
   }

   private String createHostOnlyIf() {
      final String hostOnlyIfName;
      ExecResponse createHostOnlyResponse = runScriptOnNodeFactory
               .create(host.get(), Statements.exec("VBoxManage hostonlyif create"),
                        runAsRoot(false).wrapInInitScript(false)).init().call();
      String output = createHostOnlyResponse.getOutput();
      checkState(createHostOnlyResponse.getExitStatus()==0);
      checkState(output.contains("'"), "cannot create hostonlyif");
      hostOnlyIfName = output.substring(output.indexOf("'") + 1, output.lastIndexOf("'"));
      return hostOnlyIfName;
   }

   private IHostNetworkInterface returnExistingHostNetworkInterfaceWithDHCPenabledOrNull(Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      checkNotNull(availableNetworkInterfaces);
      return Iterables.getFirst(filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(availableNetworkInterfaces), null);
   }

   /**
    * @param availableNetworkInterfaces 
    * @param hostOnlyIfIpAddress
    * @return
    */
   private Iterable<IHostNetworkInterface> filterAvailableNetworkInterfaceByHostOnlyAndDHCPenabled(Iterable<IHostNetworkInterface> availableNetworkInterfaces) {
      Iterable<IHostNetworkInterface> filteredNetworkInterfaces = Iterables.filter(availableNetworkInterfaces, new Predicate<IHostNetworkInterface>() {
         @Override
         public boolean apply(IHostNetworkInterface iHostNetworkInterface) {
            // this is an horrible workaround cause iHostNetworkInterface.getDhcpEnabled is working only for windows host
            boolean match = false;
            List<IDHCPServer> availableDHCPservers = manager.get().getVBox().getDHCPServers();
            for (IDHCPServer idhcpServer : availableDHCPservers) {
               if(idhcpServer.getEnabled() && idhcpServer.getNetworkName().equals(iHostNetworkInterface.getNetworkName()))
                  match  = true;
            }
            return iHostNetworkInterface.getInterfaceType().equals(HostNetworkInterfaceType.HostOnly) &&
                    match;
            }
      });
      return filteredNetworkInterfaces;
   }

}
