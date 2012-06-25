/*
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

package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.BACKING_GROUP_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.BACKING_TEMPLATE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.MAX_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.MIN_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.NodePoolComputeServiceProperties.REMOVE_DESTROYED_PROPERTY;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * An eager {@link NodePoolComputeService}. Eagerly builds and maintains a pool of nodes. It's only
 * "started" after min nodes are allocated and available.
 * 
 * @author David Alves
 * 
 */
@Singleton
public class EagerNodePoolComputeService extends BaseNodePoolComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final int maxSize;
   private final int minSize;
   private final boolean reuseDestroyed;

   @Inject
   public EagerNodePoolComputeService(ComputeServiceContext backingComputeServiceContext,
            @Named(BACKING_GROUP_PROPERTY) String poolGroupPrefix, @Named(MAX_SIZE_PROPERTY) int maxSize,
            @Named(MIN_SIZE_PROPERTY) int minSize, @Named(REMOVE_DESTROYED_PROPERTY) boolean readdDestroyed,
            @Nullable @Named(BACKING_TEMPLATE_PROPERTY) Template backingTemplate, NodeMetadataStore storage) {
      super(backingComputeServiceContext, poolGroupPrefix, backingTemplate, storage);
      this.maxSize = maxSize;
      this.minSize = minSize;
      this.reuseDestroyed = readdDestroyed;
   }

   @PostConstruct
   public void startEagerPool() {
      Set<NodeMetadata> backendNodes = getBackendNodes();
      if (backendNodes.size() < minSize) {
         addToPool(backendNodes.size() - minSize);
      }
   }

   @Override
   public synchronized Set<? extends NodeMetadata> createNodesInGroup(String group, int count,
            TemplateOptions templateOptions) throws RunNodesException {

      Set<NodeMetadata> backendNodes = getBackendNodes();
      Set<NodeMetadata> frontendNodes = metadataStore.loadAll(backendNodes);

      checkState(frontendNodes.size() + count < maxSize,
               "cannot add more nodes to pool [requested: %s, current: %s, max: %s]", count, frontendNodes.size(),
               maxSize);

      SetView<NodeMetadata> availableNodes = Sets.difference(backendNodes, frontendNodes);

      Set<NodeMetadata> newFrontEndAssignments = Sets.newHashSet();

      int i = 0;
      for (Iterator<NodeMetadata> iter = availableNodes.iterator(); iter.hasNext() && i < count; i++) {
         // TODO here we should run stuff on the nodes, like the initial scripts and
         // Credentials handling.
         newFrontEndAssignments.add(metadataStore.store(iter.next(), templateOptions, group));
      }

      return newFrontEndAssignments;
   }

   @Override
   public synchronized void destroyNode(String id) {
      checkState(getNodeMetadata(id) != null);
      metadataStore.deleteMapping(id);

      if (!reuseDestroyed) {
         backingComputeService.destroyNode(id);
         addToPool(1);
      }
   }

   @Override
   public synchronized Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      Set<NodeMetadata> frontendNodes = Sets.filter(metadataStore.loadAll(getBackendNodes()), filter);
      for (NodeMetadata node : frontendNodes) {
         metadataStore.deleteMapping(node.getId());
      }

      if (!reuseDestroyed) {
         backingComputeService.destroyNodesMatching(transformUserPredicateInSpecificIdPredicate(filter));
         addToPool(frontendNodes.size());
      }
      return frontendNodes;
   }

   @Override
   public synchronized void close() throws IOException {
      metadataStore.deleteAllMappings();
      backingComputeService.destroyNodesMatching(NodePredicates.inGroup(poolGroupName));
   }

   @Override
   public int currentSize() {
      return getBackendNodes().size();
   }

   @Override
   public int idleNodes() {
      Set<NodeMetadata> backendNodes = getBackendNodes();
      Set<NodeMetadata> frontendNodes = metadataStore.loadAll(backendNodes);
      return backendNodes.size() - frontendNodes.size();
   }

   @Override
   public int maxNodes() {
      return maxSize;
   }

   @Override
   public int minNodes() {
      return minSize;
   }

   @Override
   public int usedNodes() {
      return metadataStore.loadAll(getBackendNodes()).size();
   }

}
