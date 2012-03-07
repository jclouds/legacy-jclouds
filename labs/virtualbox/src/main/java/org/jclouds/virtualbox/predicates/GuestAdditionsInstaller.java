package org.jclouds.virtualbox.predicates;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

@Singleton
public class GuestAdditionsInstaller implements Predicate<IMachine> {

  @Resource
  @Named(ComputeServiceConstants.COMPUTE_LOGGER)
  protected Logger                          logger = Logger.NULL;

  private final IMachineToNodeMetadata      imachineToNodeMetadata;
  private final MachineUtils                machineUtils;
  private final Supplier<VirtualBoxManager> manager;

  @Inject
  public GuestAdditionsInstaller(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils,
      IMachineToNodeMetadata imachineToNodeMetadata) {
    this.machineUtils = machineUtils;
    this.imachineToNodeMetadata = imachineToNodeMetadata;
    this.manager = manager;
  }

  @Override
  public boolean apply(IMachine machine) {
    String vboxVersion = Iterables.get(Splitter.on('r').split(manager.get().getVBox().getVersion()), 0);
    ListenableFuture<ExecResponse> execFuture = machineUtils.runScriptOnNode(imachineToNodeMetadata.apply(machine),
        new InstallGuestAdditions(vboxVersion), RunScriptOptions.NONE);
    ExecResponse execResponse = Futures.getUnchecked(execFuture);
    return execResponse == null ? false : execResponse.getExitStatus() == 0;
  }

}