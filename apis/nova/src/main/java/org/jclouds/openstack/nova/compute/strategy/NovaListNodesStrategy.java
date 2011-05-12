/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.compute.strategy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.options.ListOptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class NovaListNodesStrategy implements ListNodesStrategy {
   private final NovaClient client;
   private final Function<Server, NodeMetadata> serverToNodeMetadata;

   @Inject
   protected NovaListNodesStrategy(NovaClient client,
            Function<Server, NodeMetadata> serverToNodeMetadata) {
      this.client = client;
      this.serverToNodeMetadata = serverToNodeMetadata;
   }

   @Override
   public Iterable<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      return Iterables.filter(Iterables.transform(client.listServers(ListOptions.Builder.withDetails()),
               serverToNodeMetadata), filter);
   }
}
