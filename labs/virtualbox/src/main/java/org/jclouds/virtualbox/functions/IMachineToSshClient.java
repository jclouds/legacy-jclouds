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

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

@Singleton
public class IMachineToSshClient implements Function<IMachine, SshClient> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SshClient.Factory sshClientFactory;
   private final RunScriptOnNode.Factory factory;
   private final Supplier<NodeMetadata> nodeMetadataSupplier;

   @Inject
   public IMachineToSshClient(SshClient.Factory sshClientFactory, RunScriptOnNode.Factory factory, Supplier<NodeMetadata> nodeMetadataSupplier) {
      this.sshClientFactory = sshClientFactory;
      this.nodeMetadataSupplier = nodeMetadataSupplier;
      this.factory = factory;
   }

   @Override
   public SshClient apply(final IMachine vm) {
      IMachineToNodeMetadata iMachineToNodeMetadata = new IMachineToNodeMetadata(factory, nodeMetadataSupplier);
      NodeMetadata nodeMetadata = iMachineToNodeMetadata.apply(vm);
      String ipAddress = Iterables.get(nodeMetadata.getPrivateAddresses(), 0);
         
      return checkNotNull(sshClientFactory.create(new IPSocket(ipAddress, 22), LoginCredentials.builder().user("toor")
               .password("password").authenticateSudo(true).build()), "ssh client");
   }
}