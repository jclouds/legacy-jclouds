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
package org.jclouds.gogrid.compute.strategy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.predicates.ServerLatestJobCompleted;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GoGridLifeCycleStrategy implements RebootNodeStrategy, ResumeNodeStrategy, SuspendNodeStrategy {
   private final GoGridClient client;
   private final RetryablePredicate<Server> serverLatestJobCompleted;
   private final RetryablePredicate<Server> serverLatestJobCompletedShort;
   private final GetNodeMetadataStrategy getNode;

   @Inject
   protected GoGridLifeCycleStrategy(GoGridClient client, GetNodeMetadataStrategy getNode, Timeouts timeouts) {
      this.client = client;
      this.serverLatestJobCompleted = new RetryablePredicate<Server>(new ServerLatestJobCompleted(client
               .getJobServices()), timeouts.nodeRunning * 9l / 10l);
      this.serverLatestJobCompletedShort = new RetryablePredicate<Server>(new ServerLatestJobCompleted(client
               .getJobServices()), timeouts.nodeRunning * 1l / 10l);
      this.getNode = getNode;
   }

   @Override
   public NodeMetadata rebootNode(String id) {
      executeCommandOnServer(PowerCommand.RESTART, id);
      Server server = Iterables.getOnlyElement(client.getServerServices().getServersById(new Long(id)));
      client.getServerServices().power(server.getName(), PowerCommand.START);
      serverLatestJobCompletedShort.apply(server);
      return getNode.getNode(id);
   }

   @Override
   public NodeMetadata resumeNode(String id) {
      executeCommandOnServer(PowerCommand.START, id);
      return getNode.getNode(id);

   }

   @Override
   public NodeMetadata suspendNode(String id) {
      executeCommandOnServer(PowerCommand.STOP, id);
      return getNode.getNode(id);

   }

   private boolean executeCommandOnServer(PowerCommand command, String id) {
      Server server = Iterables.getOnlyElement(client.getServerServices().getServersById(new Long(id)));
      client.getServerServices().power(server.getName(), command);
      return serverLatestJobCompleted.apply(server);
   }
}