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

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.rest.annotations.Credential;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.scriptbuilder.domain.Statements;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

@Singleton
public class StartVBoxIfNotAlreadyRunning implements Supplier<VirtualBoxManager> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Factory runScriptOnNodeFactory;
   private final RetryIfSocketNotYetOpen socketTester;
   private final Supplier<NodeMetadata> host;
   private final Supplier<URI> providerSupplier;
   private final String identity;
   private final String credential;
   private final Function<Supplier<NodeMetadata>, VirtualBoxManager> managerForNode;
   private transient VirtualBoxManager manager;

   // the functions and suppliers here are to ensure we don't do heavy i/o in injection
   @Inject
   public StartVBoxIfNotAlreadyRunning(Function<Supplier<NodeMetadata>, VirtualBoxManager> managerForNode,
            Factory runScriptOnNodeFactory, RetryIfSocketNotYetOpen socketTester, Supplier<NodeMetadata> host,
            @Provider Supplier<URI> providerSupplier, @Identity String identity, @Credential String credential) {
      this.runScriptOnNodeFactory = checkNotNull(runScriptOnNodeFactory, "runScriptOnNodeFactory");
      this.socketTester = checkNotNull(socketTester, "socketTester");
      this.socketTester.seconds(3L);
      this.host = checkNotNull(host, "host");
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to virtualbox websrvd is needed");
      this.identity = checkNotNull(identity, "identity");
      this.credential = checkNotNull(credential, "credential");
      this.managerForNode = checkNotNull(managerForNode, "managerForNode");
   }

   @PostConstruct
   public void start() {
      URI provider = providerSupplier.get();
      if (!socketTester.apply(new IPSocket(provider.getHost(), provider.getPort()))) {
         logger.debug("disabling password access");
         runScriptOnNodeFactory.create(host.get(), Statements.exec("VBoxManage setproperty websrvauthlibrary null"),
                  runAsRoot(false).wrapInInitScript(false)).init().call();
         logger.debug(">> starting vboxwebsrv");
         String vboxwebsrv = "vboxwebsrv -t 10000 -v -b";
         if (host.get().getOperatingSystem() != null
                  && host.get().getOperatingSystem().getDescription().equals("Mac OS X"))
            vboxwebsrv = "cd /Applications/VirtualBox.app/Contents/MacOS/ && " + vboxwebsrv;

         runScriptOnNodeFactory.create(host.get(), Statements.exec(vboxwebsrv),
                  runAsRoot(false).wrapInInitScript(false).blockOnComplete(false).nameTask("vboxwebsrv")).init().call();
         
         if (!socketTester.apply(new IPSocket(provider.getHost(), provider.getPort()))){
            throw new RuntimeException("could not connect to virtualbox");
         }
      }
      manager = managerForNode.apply(host);
      manager.connect(provider.toASCIIString(), identity, credential);
      if (logger.isDebugEnabled())
         if (manager.getSessionObject().getState() != SessionState.Unlocked)
            logger.warn("manager is not in unlocked state " + manager.getSessionObject().getState());
   }

   @Override
   public VirtualBoxManager get() {
      checkState(manager != null, "start not called");
      return manager;
   }

}
