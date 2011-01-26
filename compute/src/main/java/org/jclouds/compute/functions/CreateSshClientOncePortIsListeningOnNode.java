/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.compute.util.ComputeServiceUtils;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 *
 */
@Singleton
public class CreateSshClientOncePortIsListeningOnNode implements Function<NodeMetadata, SshClient> {
   @Inject(optional = true)
   SshClient.Factory sshFactory;
   private final RetryIfSocketNotYetOpen socketTester;

   @Inject
   public CreateSshClientOncePortIsListeningOnNode(RetryIfSocketNotYetOpen socketTester) {
      this.socketTester = socketTester;
   }

   @Override
   public SshClient apply(NodeMetadata node) {
      checkState(sshFactory != null, "ssh requested, but no SshModule configured");
      checkNotNull(node.getCredentials(), "no credentials found for node %s", node.getId());
      checkNotNull(node.getCredentials().identity, "no login identity found for node %s", node.getId());
      checkNotNull(node.getCredentials().credential, "no credential found for %s on node %s", node
               .getCredentials().identity, node.getId());
      IPSocket socket = ComputeServiceUtils.findReachableSocketOnNode(socketTester, node, node.getLoginPort());
      return sshFactory.create(socket, node.getCredentials());
   }
}