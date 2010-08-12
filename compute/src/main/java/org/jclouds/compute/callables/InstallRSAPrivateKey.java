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

package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.io.Payload;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class InstallRSAPrivateKey implements SshCallable<ExecResponse> {
   private SshClient ssh;
   private final NodeMetadata node;
   private final Payload privateKey;

   private Logger logger = Logger.NULL;

   public InstallRSAPrivateKey(NodeMetadata node, Payload privateKey) {
      this.node = checkNotNull(node, "node");
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   @Override
   public ExecResponse call() throws Exception {
      ssh.exec("mkdir .ssh");
      ssh.put(".ssh/id_rsa", privateKey);
      logger.debug(">> installing rsa key for %s@%s", node.getCredentials().identity, Iterables.get(node
            .getPublicAddresses(), 0));
      return ssh.exec("chmod 600 .ssh/id_rsa");
   }

   @Override
   public void setConnection(SshClient ssh, Logger logger) {
      this.logger = checkNotNull(logger, "logger");
      this.ssh = checkNotNull(ssh, "ssh");
   }

   @Override
   public NodeMetadata getNode() {
      return node;
   }
}