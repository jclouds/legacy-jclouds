/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.ByteArrayInputStream;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.util.ComputeServiceUtils.SshCallable;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class AuthorizeRSAPublicKey implements SshCallable<ExecResponse> {
   private SshClient ssh;
   private final NodeMetadata node;
   private final String publicKey;

   private Logger logger = Logger.NULL;

   public AuthorizeRSAPublicKey(NodeMetadata node, String publicKey) {
      this.node = checkNotNull(node, "node");
      this.publicKey = checkNotNull(publicKey, "publicKey");
   }

   @Override
   public ExecResponse call() throws Exception {
      ssh.exec("mkdir .ssh");
      ssh.put(".ssh/id_rsa.pub", new ByteArrayInputStream(publicKey.getBytes()));
      logger.debug(">> authorizing rsa public key for %s@%s", node.getCredentials().identity,
               Iterables.get(node.getPublicAddresses(), 0));
      ExecResponse returnVal = ssh.exec("cat .ssh/id_rsa.pub >> .ssh/authorized_keys");
      returnVal = ssh.exec("chmod 600 .ssh/authorized_keys");
      logger.debug("<< complete(%d)", returnVal.getExitCode());
      return returnVal;
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