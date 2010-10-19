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

package org.jclouds.servermanager.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.servermanager.Server;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.compute.functions.ServerToNodeMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerManagerGetAndListNodesStrategy implements ListNodesStrategy, GetNodeMetadataStrategy {
   private final ServerManager client;
   private final ServerToNodeMetadata serverToNodeMetadata;

   @Inject
   protected ServerManagerGetAndListNodesStrategy(ServerManager client, ServerToNodeMetadata serverToNodeMetadata) {
      this.client = checkNotNull(client, "client");
      this.serverToNodeMetadata = checkNotNull(serverToNodeMetadata, "serverToNodeMetadata");
   }

   @Override
   public NodeMetadata execute(String id) {
      int serverId = Integer.parseInt(id);
      Server server = client.getServer(serverId);
      return server == null ? null : serverToNodeMetadata.apply(server);
   }

   @Override
   public Iterable<? extends ComputeMetadata> list() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      return Iterables.filter(Iterables.transform(client.listServers(), serverToNodeMetadata), filter);
   }
}