/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.net.HostAndPort;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 *
 */
@Singleton
public class CreateSshClientOncePortIsListeningOnNode implements Function<NodeMetadata, SshClient> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   SshClient.Factory sshFactory;
   
   private final OpenSocketFinder openSocketFinder;

   private final long timeoutMs;
   
   @Inject
   public CreateSshClientOncePortIsListeningOnNode(OpenSocketFinder openSocketFinder, Timeouts timeouts) {
      this.openSocketFinder = openSocketFinder;
      this.timeoutMs = timeouts.portOpen;
   }

   @Override
   public SshClient apply(NodeMetadata node) {
      checkState(sshFactory != null, "ssh requested, but no SshModule configured");
      checkNotNull(node.getCredentials(), "no credentials found for node %s", node.getId());
      checkNotNull(node.getCredentials().identity, "no login identity found for node %s", node.getId());
      checkNotNull(node.getCredentials().credential, "no credential found for %s on node %s", node
               .getCredentials().identity, node.getId());
      HostAndPort socket = openSocketFinder.findOpenSocketOnNode(node, node.getLoginPort(), 
               timeoutMs, TimeUnit.MILLISECONDS);
      return sshFactory.create(socket, node.getCredentials());
   }
}
