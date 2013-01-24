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

 import com.google.common.base.*;
 import com.google.common.collect.ImmutableSet;
 import com.google.common.collect.Iterables;
 import com.google.common.io.Files;
 import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
 import org.jclouds.compute.domain.NodeMetadata;
 import org.jclouds.compute.domain.NodeMetadataBuilder;
 import org.jclouds.compute.options.RunScriptOptions;
 import org.jclouds.compute.reference.ComputeServiceConstants;
 import org.jclouds.domain.LoginCredentials;
 import org.jclouds.logging.Logger;
 import org.jclouds.util.Strings2;
 import org.jclouds.virtualbox.config.VirtualBoxComputeServiceContextModule;
 import org.jclouds.virtualbox.domain.*;
 import org.jclouds.virtualbox.statements.DeleteGShadowLock;
 import org.jclouds.virtualbox.statements.PasswordlessSudo;
 import org.jclouds.virtualbox.util.MachineController;
 import org.jclouds.virtualbox.util.MachineUtils;
 import org.jclouds.virtualbox.util.NetworkUtils;
 import org.virtualbox_4_2.*;
 import com.google.common.collect.ImmutableList;

 import javax.annotation.Resource;
 import javax.inject.Inject;
 import javax.inject.Named;
 import javax.inject.Singleton;

 import java.io.File;
 import java.io.IOException;

 import static com.google.common.base.Preconditions.checkNotNull;
 import static com.google.common.base.Preconditions.checkState;
 import static org.jclouds.virtualbox.config.VirtualBoxConstants.*;

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
   private final String workingDir;
   
   @Inject
   public NodeCreator(Supplier<VirtualBoxManager> manager, Function<CloneSpec, IMachine> cloner,
            MachineUtils machineUtils, MachineController machineController,
            NetworkUtils networkUtils,
            @Named(VIRTUALBOX_GUEST_MEMORY) String ram,
            @Named(VIRTUALBOX_WORKINGDIR) String workingDir) {
      this.manager = checkNotNull(manager, "manager");
      this.cloner = checkNotNull(cloner, "cloner");
      this.networkUtils = checkNotNull(networkUtils, "networkUtils");
      this.machineUtils = checkNotNull(machineUtils, "machineUtils");
      this.machineController = checkNotNull(machineController, "machineController");
      this.ram = checkNotNull(Integer.valueOf(ram), "ram");
      this.workingDir = checkNotNull(workingDir, "workingDir");
   }

   @Override
   public synchronized NodeAndInitialCredentials<IMachine> apply(NodeSpec nodeSpec) {
      checkNotNull(nodeSpec, "NodeSpec");
      Master master = checkNotNull(nodeSpec.getMaster(), "Master");
      IMachine masterMachine = master.getMachine();
      String guestOsUser = masterMachine.getExtraData(GUEST_OS_USER);
      String guestOsPassword = masterMachine.getExtraData(GUEST_OS_PASSWORD);

      cleanUpMaster(master);
      CloneSpec cloneSpec = configureCloneSpec(nodeSpec, guestOsUser, guestOsPassword);
      IMachine clone = cloner.apply(cloneSpec);
      String cloneName =  cloneSpec.getVmSpec().getVmName();
      logger.debug("<< cloned a vm(%s) from master(%s)", cloneName, nodeSpec.getMaster().getMachine().getName());
      machineController.ensureMachineIsLaunched(cloneName);
      logger.debug("<< cloned vm(%s) is up and running", cloneName);

      reconfigureNetworkInterfaces(masterMachine, guestOsUser, guestOsPassword, cloneSpec.getNetworkSpec(), clone);

      postConfigurations(clone, guestOsUser, guestOsPassword);

      LoginCredentials credentials = LoginCredentials.builder()
                                                     .user(guestOsUser)
                                                     .password(guestOsPassword)
                                                     .authenticateSudo(true)
                                                     .build();
      return new NodeAndInitialCredentials<IMachine>(clone, cloneName, credentials);
   }

   private void reconfigureNetworkInterfaces(IMachine masterMachine, String guestOsUser, String guestOsPassword, NetworkSpec networkSpec, IMachine clone) {
      reconfigureHostOnlyInterfaceIfNeeded(guestOsUser, guestOsPassword, clone.getName(), masterMachine.getOSTypeId());
      logger.debug("<< reconfigured hostOnly interface of node(%s)", clone.getName());
      reconfigureNatInterfaceIfNeeded(guestOsUser, guestOsPassword, clone.getOSTypeId(), clone, networkSpec);
      logger.debug("<< reconfigured NAT interface of node(%s)", clone.getName());
   }

   /**
    * {@see DeleteGShadowLock} and {@see PasswordlessSudo} for a detailed explanation
    *
    * @param clone the target machine
    * @param guestOsUser the user to access the target machine
    * @param guestOsPassword the password to access the target machine
    */
   private void postConfigurations(IMachine clone, String guestOsUser, String guestOsPassword) {
      NodeMetadata partialNodeMetadata = buildPartialNodeMetadata(clone, guestOsUser, guestOsPassword);
      machineUtils.runScriptOnNode(partialNodeMetadata, new DeleteGShadowLock(), RunScriptOptions.NONE);
      machineUtils.runScriptOnNode(partialNodeMetadata, new PasswordlessSudo(partialNodeMetadata.getCredentials().identity), RunScriptOptions.Builder.runAsRoot(true));
   }

   private CloneSpec configureCloneSpec(
           NodeSpec nodeSpec, String guestOsUser, String guestOsPassword) {

      String cloneName = generateCloneName(nodeSpec);

      VmSpec cloneVmSpec = VmSpec.builder()
              .id(cloneName)
              .name(cloneName)
              .memoryMB(ram)
              .osTypeId(nodeSpec.getMaster().getMachine().getOSTypeId())
              .guestUser(guestOsUser)
              .guestPassword(guestOsPassword)
              .cleanUpMode(CleanupMode.Full)
              .forceOverwrite(true)
              .build();

      // case 'vbox host is localhost': NAT + HOST-ONLY
      NetworkSpec networkSpec = networkUtils.createNetworkSpecWhenVboxIsLocalhost();

      return CloneSpec.builder()
              .linked(true)
              .master(nodeSpec.getMaster().getMachine())
              .network(networkSpec)
              .vm(cloneVmSpec).build();
   }

   private void cleanUpMaster(Master master) {
      deleteExistingSnapshot(master);
   }

   private void reconfigureHostOnlyInterfaceIfNeeded(final String username, final String password,
                                                         String vmName, String osTypeId) {
      final String scriptName = "hostOnly";
      if (osTypeId.contains("RedHat")) {
         File scriptFile = copyScriptToWorkingDir("redHatAndDerivatives", scriptName);
         copyToNodeAndExecScript(username, password, vmName, scriptFile);
      }
   }

   private void reconfigureNatInterfaceIfNeeded(final String guestOsUser, final String guestOsPassword,
                                                String osTypeId, IMachine clone, NetworkSpec networkSpec) {

      final String scriptName = "nat";
      final String folder = "redHatAndDerivatives";
      if (osTypeId.contains("RedHat")) {
         File scriptFile = copyScriptToWorkingDir(folder, scriptName);
         copyToNodeAndExecScript(guestOsUser, guestOsPassword, clone.getName(), scriptFile);
      } else if (osTypeId.contains("Ubuntu") || osTypeId.contains("Debian")) {
         NodeMetadata partialNodeMetadata = buildPartialNodeMetadata(clone, guestOsUser, guestOsPassword);

         Optional<NetworkInterfaceCard> optionalNatIfaceCard = Iterables.tryFind(
                 networkSpec.getNetworkInterfaceCards(),
                 new Predicate<NetworkInterfaceCard>() {

                    @Override
                    public boolean apply(NetworkInterfaceCard nic) {
                       return nic.getNetworkAdapter().getNetworkAttachmentType()
                               .equals(NetworkAttachmentType.NAT);
                    }
                 });

         checkState(networkUtils.enableNetworkInterface(partialNodeMetadata, optionalNatIfaceCard.get()),
                 "cannot enable NAT Interface on vm(%s)", clone.getName());
      }
   }

   private File copyScriptToWorkingDir(String folder, String scriptName) {
      File scriptFile = new File(workingDir + "/conf/" + "/" + folder + "/" + scriptName);
      scriptFile.getParentFile().mkdirs();
      if (!scriptFile.exists()) {
         try {
            Files.write(Strings2.toStringAndClose(getClass().getResourceAsStream("/" + folder + "/" + scriptName)), scriptFile, Charsets.UTF_8);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
      return scriptFile;
   }

   private void copyToNodeAndExecScript(final String username, final String password,
                                        String vmName, final File scriptFile) {
      machineUtils.sharedLockMachineAndApplyToSession(vmName, new Function<ISession, Void>() {

         @Override
         public Void apply(ISession session) {
            String scriptName = scriptFile.getName();

            manager.get().getSessionObject().getConsole().getGuest()
                    .createSession(username, password, null, null)
                    .copyTo(scriptFile.getAbsolutePath(), "/tmp/" + scriptName, null);

            manager.get().getSessionObject().getConsole().getGuest()
                    .createSession(username, password, null, null)
                    .processCreate("/bin/chmod", ImmutableList.of("777", "/tmp/" + scriptName), null, null, 5 * 1000l);

            manager.get().getSessionObject().getConsole().getGuest()
                    .createSession(username, password, null, null)
                    .processCreate("/bin/sh", ImmutableList.of("/tmp/" + scriptName), null, null, 5 * 1000l);
            return null;
         }
      });
   }

   private String generateCloneName(NodeSpec nodeSpec) {
      String masterNameWithoutPrefix = nodeSpec.getMaster().getMachine().getName().replace(VIRTUALBOX_IMAGE_PREFIX, "");
      return VIRTUALBOX_NODE_PREFIX + masterNameWithoutPrefix + VIRTUALBOX_NODE_NAME_SEPARATOR
               + nodeSpec.getTag() + VIRTUALBOX_NODE_NAME_SEPARATOR + nodeSpec.getName();
   }

   private void deleteExistingSnapshot(Master master) {
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
         logger.debug("<< deleted an existing snapshot of vm(%s)", master.getMachine().getName());
      }
   }

   private NodeMetadata buildPartialNodeMetadata(IMachine clone, String guestOsUser, String guestOsPassword) {
      NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
      nodeMetadataBuilder.id(clone.getName());
      nodeMetadataBuilder.status(VirtualBoxComputeServiceContextModule.toPortableNodeStatus.get(clone.getState()));
      nodeMetadataBuilder.publicAddresses(ImmutableSet.of(networkUtils.getValidHostOnlyIpFromVm(clone.getName())));
      nodeMetadataBuilder.credentials(LoginCredentials.builder()
                                                      .user(guestOsUser)
                                                      .password(guestOsPassword)
                                                      .authenticateSudo(true).build());
      return nodeMetadataBuilder.build();
   }

}
