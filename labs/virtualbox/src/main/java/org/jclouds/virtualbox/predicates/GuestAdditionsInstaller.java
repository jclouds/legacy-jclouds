package org.jclouds.virtualbox.predicates;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

@Singleton
public class GuestAdditionsInstaller implements Predicate<String> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ComputeServiceContext context;
   private String vboxVersion;

   @Inject
   public GuestAdditionsInstaller(ComputeServiceContext context) {
      this.context = context;
   }

   @Override
   public boolean apply(String vmName) {
      vboxVersion = Iterables.get(Splitter.on('r').split(context.getProviderSpecificContext().getBuildVersion()), 0);
      ListenableFuture<ExecResponse> execFuture = context.getComputeService().submitScriptOnNode(vmName,
            new InstallGuestAdditions(vboxVersion), RunScriptOptions.NONE);
      ExecResponse execResponse = Futures.getUnchecked(execFuture);
      return execResponse == null ? false : execResponse.getExitStatus() == 0;
   }

}