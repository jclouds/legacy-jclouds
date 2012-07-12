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

import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_GROUP;
import static org.jclouds.nodepool.config.NodePoolProperties.POOL_ADMIN_ACCESS;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
import org.jclouds.nodepool.Backend;
import org.jclouds.nodepool.NodePoolComputeServiceAdapter;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.AdminAccessBuilderSpec;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * A base class for {@link NodePoolComputeService}, takes care of keeping (not changing assignments)
 * and of everything that does not change the pool.
 * 
 * @author David Alves
 * 
 */
public abstract class BaseNodePoolComputeServiceAdapter implements NodePoolComputeServiceAdapter {

   protected final Supplier<ComputeService> backendComputeService;
   protected final Supplier<Template> backendTemplate;
   protected final String poolGroupName;
   protected final NodeMetadataStore metadataStore;
   protected final AdminAccess.Builder initialCredentialsBuilder;

   public BaseNodePoolComputeServiceAdapter(@Backend Supplier<ComputeService> backendComputeService,
            @Backend Supplier<Template> backendTemplate, @Named(BACKEND_GROUP) String poolGroupName,
            NodeMetadataStore metadataStore, @Named(POOL_ADMIN_ACCESS) String poolNodeAdminAccess,
            AdminAccess.Configuration configuration) {
      this.backendComputeService = backendComputeService;
      this.poolGroupName = poolGroupName;
      this.backendTemplate = backendTemplate;
      this.metadataStore = metadataStore;
      this.initialCredentialsBuilder = AdminAccessBuilderSpec.parse(poolNodeAdminAccess).copyTo(
               new AdminAccess.Builder());
   }

   @Override
   public NodeMetadata getNode(String id) {
      NodeMetadata backendMetadata = backendComputeService.get().getNodeMetadata(id);
      if (backendMetadata == null) {
         return null;
      }
      return metadataStore.load(backendMetadata);
   }

   @Override
   public Iterable<NodeMetadata> listNodes() {
      return metadataStore.loadAll(getBackendNodes());
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return ImmutableSet.of(backendTemplate.get().getHardware());
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.of(backendTemplate.get().getImage());

   }

   @Override
   public Iterable<Location> listLocations() {
      return ImmutableSet.of(backendTemplate.get().getLocation());
   }

   @Override
   public Image getImage(String id) {
      Image backendImage = backendTemplate.get().getImage();
      return backendImage.getId().equals(id) ? backendImage : null;
   }

   @Override
   public void suspendNode(String id) {
      if (getNode(id) != null) {
         backendComputeService.get().suspendNode(id);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public void resumeNode(String id) {
      if (getNode(id) != null) {
         backendComputeService.get().resumeNode(id);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public void rebootNode(String id) {
      if (getNode(id) != null) {
         backendComputeService.get().rebootNode(id);
      }
      throw new NoSuchElementException(id);
   }

   protected Set<NodeMetadata> getBackendNodes() {
      return ImmutableSet.copyOf(Iterables.filter(
               backendComputeService.get().listNodesDetailsMatching(NodePredicates.all()),
               NodePredicates.inGroup(poolGroupName)));
   }

   @Override
   public void destroyPool() {
      metadataStore.deleteAllMappings();
      backendComputeService.get().destroyNodesMatching(NodePredicates.inGroup(poolGroupName));
   }

   @Override
   public ComputeServiceContext getBackendComputeServiceContext() {
      return backendComputeService.get().getContext();
   }

   protected Set<? extends NodeMetadata> addToPool(int number) {
      try {
         Template template = backendTemplate.get().clone();
         template.getOptions().runScript(initialCredentialsBuilder.build());
         return backendComputeService.get().createNodesInGroup(poolGroupName, number, template);
      } catch (RunNodesException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public String getPoolGroupName() {
      return this.poolGroupName;
   }

}
