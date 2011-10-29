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


import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.URI;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;

public class StartVBoxIfNotAlreadyRunning implements Function<URI, VirtualBoxManager> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private ComputeServiceContext context;
   private String hostId;
   private Credentials credentials;

   public StartVBoxIfNotAlreadyRunning(ComputeServiceContext context, String hostId, Credentials credentials) {
      this.context = context;
      this.hostId = hostId;
      this.credentials = credentials;
   }

   @Override
   public VirtualBoxManager apply(@Nullable URI endpoint) {

      // TODO Really create new object here? Should we cache these instead?
      VirtualBoxManager manager = VirtualBoxManager.createInstance(hostId);

      if (new InetSocketAddressConnect().apply(new IPSocket(endpoint.getHost(), endpoint.getPort()))) {
         manager.connect(endpoint.toASCIIString(), credentials.identity, credentials.credential);
         return manager;
      }

      logger.debug("disabling password access");
      context.getComputeService().runScriptOnNode(hostId, "VBoxManage setproperty websrvauthlibrary null", runAsRoot(false).wrapInInitScript(false));
      logger.debug("starting vboxwebsrv");
      String vboxwebsrv = "vboxwebsrv -t 10000 -v -b";
      if (isOSX(hostId))
         vboxwebsrv = "cd /Applications/VirtualBox.app/Contents/MacOS/ && " + vboxwebsrv;

      context.getComputeService().runScriptOnNode(hostId, vboxwebsrv, runAsRoot(false).wrapInInitScript(false).blockOnComplete(false).nameTask("vboxwebsrv"));

      manager.connect(endpoint.toASCIIString(), credentials.identity, credentials.credential);
      return manager;
   }

   private boolean isOSX(String hostId) {
      return context.getComputeService().getNodeMetadata(hostId).getOperatingSystem().getDescription().equals(
              "Mac OS X");
   }

}
