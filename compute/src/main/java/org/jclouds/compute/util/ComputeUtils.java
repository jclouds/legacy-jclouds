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
package org.jclouds.compute.util;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeUtils {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   @Inject(optional = true)
   private SshClient.Factory sshFactory;
   private final Predicate<InetSocketAddress> socketTester;

   @Inject
   public ComputeUtils(Predicate<InetSocketAddress> socketTester) {
      this.socketTester = socketTester;
   }

   public static Iterable<? extends ComputeMetadata> filterByName(
            Iterable<? extends ComputeMetadata> nodes, final String name) {
      return Iterables.filter(nodes, new Predicate<ComputeMetadata>() {
         @Override
         public boolean apply(ComputeMetadata input) {
            return input.getName().equalsIgnoreCase(name);
         }
      });
   }

   public void runScriptOnNode(CreateNodeResponse node, byte[] script) {
      checkState(this.sshFactory != null, "runScript requested, but no SshModule configured");

      InetSocketAddress socket = new InetSocketAddress(node.getPublicAddresses().last(), 22);
      socketTester.apply(socket);
      SshClient ssh = isKeyAuth(node) ? sshFactory.create(socket, node.getCredentials().account,
               node.getCredentials().key.getBytes()) : sshFactory.create(socket, node
               .getCredentials().account, node.getCredentials().key);
      try {
         ssh.connect();
         String scriptName = node.getId() + ".sh";
         ssh.put(scriptName, new ByteArrayInputStream(script));
         ssh.exec("chmod 755 " + scriptName);
         if (node.getCredentials().account.equals("root")) {
            logger.debug(">> running %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec("./" + scriptName).getExitCode());
         } else if (isKeyAuth(node)) {
            logger.debug(">> running sudo %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec("sudo ./" + scriptName).getExitCode());
         } else {
            logger.debug(">> running sudo -S %s as %s", scriptName, node.getCredentials().account);
            logger.debug("<< complete(%d)", ssh.exec(
                     String.format("echo %s|sudo -S ./%s", node.getCredentials().key, scriptName))
                     .getExitCode());
         }
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   public static boolean isKeyAuth(CreateNodeResponse createdNode) {
      return createdNode.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----");
   }
}
