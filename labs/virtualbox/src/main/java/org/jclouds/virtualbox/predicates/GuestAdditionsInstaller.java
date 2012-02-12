package org.jclouds.virtualbox.predicates;

import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunScriptData;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
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
      StatementList statementList = prepareStatementList();

      ListenableFuture<ExecResponse> execFuture = context.getComputeService().submitScriptOnNode(vmName, statementList,
            runAsRoot(true).wrapInInitScript(false));
      ExecResponse execResponse = null;
      try {
         execResponse = execFuture.get();
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      } catch (ExecutionException e) {
         Throwables.propagate(e);
      }
      return execResponse == null ? false : execResponse.getExitCode() == 0;
   }

   private StatementList prepareStatementList() {
      vboxVersion = Iterables.get(Splitter.on('r').split(context.getProviderSpecificContext().getBuildVersion()), 0);
      InstallGuestAdditions installGuestAdditions = new InstallGuestAdditions(vboxVersion);
      StatementList statementList = new StatementList(Statements.exec(RunScriptData.aptInstallLazyUpgrade("curl")),
            installGuestAdditions);
      return statementList;
   }

}