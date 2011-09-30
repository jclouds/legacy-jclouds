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
package org.jclouds.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a node is active.
 * 
 * @author Adrian Cole
 */
@Singleton
public class NodePresentAndInIntendedState implements Predicate<NodeMetadata> {

   private final ComputeService client;
   private final NodeState intended;
   private final Set<NodeState> invalids;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public NodePresentAndInIntendedState(NodeState intended, ComputeService client) {
      this(intended, ImmutableSet.of(NodeState.ERROR), client);
   }

   public NodePresentAndInIntendedState(NodeState intended, Set<NodeState> invalids, ComputeService client) {
      this.intended = intended;
      this.client = client;
      this.invalids = invalids;
   }

   public boolean apply(NodeMetadata node) {
      logger.trace("looking for state on node %s", checkNotNull(node, "node"));
      node = refresh(node);
      if (node == null)
         return false;
      logger.trace("%s: looking for node state %s: currently: %s", node.getId(), intended, node.getState());
      if (invalids.contains(node.getState()))
         throw new IllegalStateException("node " + node.getId() + " in location " + node.getLocation()
                  + " is in invalid state "+node.getState());
      return node.getState() == intended;
   }

   private NodeMetadata refresh(NodeMetadata node) {
      if (node == null || node.getId() == null)
         return null;
      return client.getNodeMetadata(node.getId());
   }
}
