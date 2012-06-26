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
import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_GROUP;
import static org.jclouds.nodepool.config.NodePoolProperties.MAX_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.MIN_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.REMOVE_DESTROYED;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.nodepool.Backend;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * An eager {@link NodePoolComputeService}. Eagerly builds and maintains a pool
 * of nodes. It's only "started" after min nodes are allocated and available.
 * 
 * @author David Alves
 * 
 */
@Singleton
public class EagerNodePoolComputeServiceAdapter extends BaseNodePoolComputeServiceAdapter {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final int maxSize;
   private final int minSize;
   private final boolean reuseDestroyed;

   @Inject
   public EagerNodePoolComputeServiceAdapter(@Backend Supplier<ComputeService> backendComputeService,
         @Backend Supplier<Template> backendTemplate, @Named(BACKEND_GROUP) String poolGroupPrefix,
         @Named(MAX_SIZE) int maxSize, @Named(MIN_SIZE) int minSize, @Named(REMOVE_DESTROYED) boolean readdDestroyed,
         NodeMetadataStore storage) {
      super(backendComputeService, backendTemplate, poolGroupPrefix, storage);
      this.maxSize = maxSize;
      this.minSize = minSize;
      this.reuseDestroyed = readdDestroyed;
   }

   @PostConstruct
   public void startEagerPool() {
      Set<? extends NodeMetadata> backendNodes = getBackendNodes();
      if (backendNodes.size() < minSize) {
         addToPool(backendNodes.size() - minSize);
      }
   }

   @Override
   public NodeWithInitialCredentials createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      int count = 1;
      synchronized (this) {
         Set<NodeMetadata> backendNodes = getBackendNodes();
         Set<NodeMetadata> frontendNodes = metadataStore.loadAll(backendNodes);

         checkState(frontendNodes.size() + count < maxSize,
               "cannot add more nodes to pool [requested: %s, current: %s, max: %s]", count, frontendNodes.size(),
               maxSize);

         SetView<NodeMetadata> availableNodes = Sets.difference(backendNodes, frontendNodes);

         NodeMetadata node = metadataStore.store(Iterables.get(availableNodes, 0), template.getOptions(), group);
         return new NodeWithInitialCredentials(node);
      }
   }

   @Override
   public synchronized void destroyNode(String id) {
      checkState(getNode(id) != null);
      metadataStore.deleteMapping(id);

      if (!reuseDestroyed) {
         backendComputeService.get().destroyNode(id);
         addToPool(1);
      }
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
