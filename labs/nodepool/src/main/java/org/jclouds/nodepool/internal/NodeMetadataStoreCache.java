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

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Singleton
public class NodeMetadataStoreCache implements NodeMetadataStore {

   private Map<String, NodeMetadata> frontendMetadataCache = Maps.newHashMap();
   private NodeMetadataStore backend;

   @Inject
   public NodeMetadataStoreCache(NodeMetadataStore backend) {
      this.backend = backend;
   }

   @Override
   public synchronized NodeMetadata store(NodeMetadata backendNode, TemplateOptions userOptions, String userGroup) {
      NodeMetadata frontEndNode = backend.store(backendNode, userOptions, userGroup);
      frontendMetadataCache.put(backendNode.getId(), frontEndNode);
      return frontEndNode;
   }

   @Override
   public synchronized void deleteMapping(String backendNodeId) {
      frontendMetadataCache.remove(backendNodeId);
      backend.deleteMapping(backendNodeId);

   }

   @Override
   public synchronized void deleteAllMappings() {
      frontendMetadataCache.clear();
      backend.deleteAllMappings();
   }

   @Override
   public synchronized NodeMetadata load(NodeMetadata backendNode) {
      NodeMetadata frontendNode = frontendMetadataCache.get(backendNode.getId());
      if (frontendNode == null) {
         frontendNode = backend.load(backendNode);
         if (frontendNode != null) {
            frontendMetadataCache.put(backendNode.getId(), frontendNode);
         }
      }
      return frontendNode;
   }

   @Override
   public synchronized Set<NodeMetadata> loadAll(Set<NodeMetadata> backendNodes) {
      return ImmutableSet.copyOf(Iterables.transform(backendNodes, new Function<NodeMetadata, NodeMetadata>() {
         @Override
         public NodeMetadata apply(NodeMetadata input) {
            return load(input);
         }
      }));
   }

}
