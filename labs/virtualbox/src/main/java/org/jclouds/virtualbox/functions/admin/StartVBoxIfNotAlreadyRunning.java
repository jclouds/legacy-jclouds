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

package org.jclouds.virtualbox.functions.admin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.findPid;
import static org.jclouds.scriptbuilder.domain.Statements.kill;

import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.virtualbox.functions.HardcodedHostToHostNodeMetadata;
import org.jclouds.virtualbox.predicates.RetryIfSocketNotYetOpen;
import org.virtualbox_4_2.SessionState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.UncheckedTimeoutException;

@Singleton
public class StartVBoxIfNotAlreadyRunning implements Supplier<VirtualBoxManager> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Factory runScriptOnNodeFactory;
   private final RetryIfSocketNotYetOpen socketTester;
   private final Supplier<NodeMetadata> host;
   private final Supplier<URI> providerSupplier;
   private final Function<Supplier<NodeMetadata>, VirtualBoxManager> managerForNode;
   private transient VirtualBoxManager manager;
   private final HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata;

   // the functions and suppliers here are to ensure we don't do heavy i/o in injection
   @Inject
   public StartVBoxIfNotAlreadyRunning(Function<Supplier<NodeMetadata>, VirtualBoxManager> managerForNode,
         Factory runScriptOnNodeFactory, RetryIfSocketNotYetOpen socketTester, Supplier<NodeMetadata> host,
         @Provider Supplier<URI> providerSupplier, HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata) {
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.socketTester = checkNotNull(socketTester, "socketTester");
      this.socketTester.seconds(3L);
      this.host = checkNotNull(host, "host");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to virtualbox websrvd is needed");
      this.managerForNode = checkNotNull(managerForNode, "managerForNode");
      this.hardcodedHostToHostNodeMetadata = hardcodedHostToHostNodeMetadata;
   }

   @PostConstruct
   public void start() {
      URI provider = providerSupplier.get();
      NodeMetadata hostNodeMetadata = hardcodedHostToHostNodeMetadata.apply(host.get());

      if (!socketTester.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))) {
         logger.debug("disabling password access");
         runScriptOnNodeFactory
               .create(hostNodeMetadata, exec("VBoxManage setproperty websrvauthlibrary null"),
                     runAsRoot(false).wrapInInitScript(false)).init().call();

         logger.debug(">> starting vboxwebsrv");
         String vboxwebsrv = "vboxwebsrv -t0 -v -b -H " + providerSupplier.get().getHost();
         runScriptOnNodeFactory
               .create(hostNodeMetadata, exec(vboxwebsrv),
                     runAsRoot(false).wrapInInitScript(false).blockOnComplete(false).nameTask("vboxwebsrv")).init()
               .call();

         if (!socketTester.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))) {
            throw new UncheckedTimeoutException(String.format("could not connect to virtualbox at %s", provider));
         }
      }

      manager = managerForNode.apply(host);
      manager.connect(provider.toASCIIString(), "", "");
      if (logger.isDebugEnabled())
         if (manager.getSessionObject().getState() != SessionState.Unlocked)
            logger.warn("manager is not in unlocked state " + manager.getSessionObject().getState());

   }

   public void cleanUpHost() {
      // kill previously started vboxwebsrv (possibly dirty session)
      URI provider = providerSupplier.get();
      NodeMetadata hostNodeMetadata = hardcodedHostToHostNodeMetadata.apply(host.get());
      List<Statement> statements = new ImmutableList.Builder<Statement>()
            .add(findPid("vboxwebsrv"))
            .add(kill()).build();
      StatementList statementList = new StatementList(statements);

      if (socketTester.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))) {
         logger.debug(String.format("shutting down previously started vboxwewbsrv at %s", provider));
         ExecResponse execResponse = runScriptOnNodeFactory.create(hostNodeMetadata, statementList, runAsRoot(false))
               .init().call();
         if (execResponse.getExitStatus() != 0)
            throw new RuntimeException(String.format("Cannot shutdown a running vboxwebsrv at %s. ExecResponse: %s", provider, execResponse));
      }
   }

   @Override
   public VirtualBoxManager get() {
      checkState(manager != null, "VirtualBoxManager is not initialised");
      return manager;
   }

}
