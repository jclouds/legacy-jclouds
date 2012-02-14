package org.jclouds.virtualbox.statements;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.functions.CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.jclouds.virtualbox.functions.LaunchMachineIfNotAlreadyRunning;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

@Singleton
public class GuestAdditionsInstaller implements Function<String, IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ComputeServiceContext context;

   private final Supplier<VirtualBoxManager> manager;
   private final ExecutionType executionType;
   private final MachineUtils machineUtils;
   // TODO remove this hardcoded value
   private String vboxVersion = "4.1.6";

   @Inject
   public GuestAdditionsInstaller(ComputeServiceContext context, Supplier<VirtualBoxManager> manager,
         CreateAndRegisterMachineFromIsoIfNotAlreadyExists createAndRegisterMachineFromIsoIfNotAlreadyExists,
         Predicate<SshClient> installGuestAdditionsViaSshResponds, Function<IMachine, SshClient> sshClientForIMachine,
         ExecutionType executionType, MachineUtils machineUtils, Factory runScriptOnNodeFactory,
         Supplier<NodeMetadata> guest) {
      this.context = context;
      this.manager = manager;
      this.executionType = executionType;
      this.machineUtils = machineUtils;
   }

   @Override
   public IMachine apply(String vmName) {
      IMachine vm = manager.get().getVBox().findMachine(vmName);
      ensureMachineIsLaunched(vmName);

      NodeMetadata vmMetadata = new IMachineToNodeMetadata().apply(vm);

      ListenableFuture<ExecResponse> execFuture = context.getComputeService().submitScriptOnNode(vmMetadata.getId(),
            new InstallGuestAdditions(vboxVersion), RunScriptOptions.NONE);
      Futures.getUnchecked(execFuture);
      return vm;
   }

   private void ensureMachineIsLaunched(String vmName) {
      machineUtils.applyForMachine(vmName, new LaunchMachineIfNotAlreadyRunning(manager.get(), executionType, ""));
   }

}