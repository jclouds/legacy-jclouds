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

import java.io.Closeable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.util.Maps2;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * A base class for {@link NodePoolComputeService}, takes care of keeping (not changing assignments)
 * and of everything that does not change the pool.
 * 
 * @author David Alves
 * 
 */

public abstract class BaseNodePoolComputeService implements ComputeService, Closeable {

   protected final ComputeService backingComputeService;
   protected final String poolGroupName;
   protected final Template template;
   protected final Image image;
   protected final Hardware hardware;
   protected final Location location;
   protected final NodeMetadataStore metadataStore;

   public BaseNodePoolComputeService(ComputeServiceContext backingComputeServiceContext, String poolGroupNamePrefix,
            Template backingTemplate, NodeMetadataStore metadataStore) {
      this.backingComputeService = backingComputeServiceContext.getComputeService();
      this.poolGroupName = poolGroupNamePrefix;
      this.template = backingTemplate == null ? this.backingComputeService.templateBuilder().build() : backingTemplate;
      this.image = this.template.getImage();
      this.hardware = this.template.getHardware();
      this.location = this.template.getLocation();
      this.metadataStore = metadataStore;
   }

   /**
    * Checks which nodes match the users predicate and builds a predicate that returns true to their
    * specific ids.
    * 
    * @param filter
    * @return
    */
   protected Predicate<NodeMetadata> transformUserPredicateInSpecificIdPredicate(Predicate<NodeMetadata> filter) {
      final Set<NodeMetadata> filteredNodes = filterFrontendNodesBasedOnUserPredicate(filter);
      return new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return filteredNodes.contains(input);
         }
      };

   }// TODO this is n^2 expensive. s

   private <T extends NodeMetadata> Map<T, ExecResponse> transformBackendExecutionMapIntoFrontend(
            Map<T, ExecResponse> backendMap) {
      return Maps2.transformKeys(backendMap, new Function<T, T>() {
         @SuppressWarnings("unchecked")
         @Override
         public T apply(T input) {
            return (T) metadataStore.load(input);
         }
      });

   }

   /**
    * Because a lot of predicates are based on group info we need that to check wether the predicate
    * matches.
    */
   protected Set<NodeMetadata> filterFrontendNodesBasedOnUserPredicate(final Predicate<NodeMetadata> userFilter) {
      return Sets.filter(metadataStore.loadAll(getBackendNodes()), new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return userFilter.apply(input);
         }
      });
   }

   @Override
   public NodeMetadata getNodeMetadata(String id) {
      NodeMetadata backendMetadata = backingComputeService.getNodeMetadata(id);
      checkState(backendMetadata.getGroup().equals(backendMetadata));
      return metadataStore.load(backendMetadata);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript) throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, runScript, new RunScriptOptions());
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript) throws RunScriptOnNodesException {
      return runScriptOnNodesMatching(filter, runScript, new RunScriptOptions());
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            String runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateInSpecificIdPredicate(filter), runScript, options));
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(Predicate<NodeMetadata> filter,
            Statement runScript, RunScriptOptions options) throws RunScriptOnNodesException {
      return transformBackendExecutionMapIntoFrontend(backingComputeService.runScriptOnNodesMatching(
               transformUserPredicateInSpecificIdPredicate(filter), runScript, options));
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      return listNodesDetailsMatching(Predicates.alwaysTrue());
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public Set<? extends NodeMetadata> listNodesDetailsMatching(Predicate filter) {
      return filterFrontendNodesBasedOnUserPredicate(filter);
   }

   @Override
   public void rebootNodesMatching(final Predicate<NodeMetadata> filter) {
      backingComputeService.rebootNodesMatching(transformUserPredicateInSpecificIdPredicate(filter));
   }

   @Override
   public void resumeNodesMatching(Predicate<NodeMetadata> filter) {
      backingComputeService.resumeNodesMatching(transformUserPredicateInSpecificIdPredicate(filter));
   }

   @Override
   public void suspendNodesMatching(Predicate<NodeMetadata> filter) {
      backingComputeService.suspendNodesMatching(transformUserPredicateInSpecificIdPredicate(filter));
   }

   @Override
   public ComputeServiceContext getContext() {
      // not sure this is enough, should we have our own?
      return backingComputeService.getContext();
   }

   // we ignore user provided templates and options

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count, Template template)
            throws RunNodesException {
      return createNodesInGroup(group, count, template.getOptions());
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group, int count) throws RunNodesException {
      return createNodesInGroup(group, count, template.getOptions());
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return backingComputeService.templateBuilder().fromTemplate(template);
   }

   @Override
   public TemplateOptions templateOptions() {
      return backingComputeService.templateOptions();
   }

   @Override
   public Set<? extends Hardware> listHardwareProfiles() {
      return ImmutableSet.of(hardware);
   }

   @Override
   public Set<? extends Image> listImages() {
      return ImmutableSet.of(image);

   }

   @Override
   public Image getImage(String id) {
      return image.getId().equals(id) ? image : null;
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return ImmutableSet.of(location);
   }

   @Override
   public void suspendNode(String id) {
      if (getNodeMetadata(id) != null) {
         backingComputeService.suspendNode(id);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public void resumeNode(String id) {
      if (getNodeMetadata(id) != null) {
         backingComputeService.resumeNode(id);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public void rebootNode(String id) {
      if (getNodeMetadata(id) != null) {
         backingComputeService.rebootNode(id);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript) {
      if (getNodeMetadata(id) != null) {
         return runScriptOnNode(id, runScript, new RunScriptOptions());
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      if (getNodeMetadata(id) != null) {
         return runScriptOnNode(id, runScript, new RunScriptOptions());
      }
      throw new NoSuchElementException(id);

   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      if (getNodeMetadata(id) != null) {
         return backingComputeService.runScriptOnNode(id, runScript, options);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public ListenableFuture<ExecResponse> submitScriptOnNode(String id, Statement runScript, RunScriptOptions options) {
      if (getNodeMetadata(id) != null) {
         return backingComputeService.submitScriptOnNode(id, runScript, options);
      }
      throw new NoSuchElementException(id);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript, RunScriptOptions options) {
      if (getNodeMetadata(id) != null) {
         return backingComputeService.runScriptOnNode(id, runScript, options);
      }
      throw new NoSuchElementException(id);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public Set<NodeMetadata> getBackendNodes() {
      return (Set<NodeMetadata>) backingComputeService.listNodesDetailsMatching((Predicate) NodePredicates
               .inGroup(poolGroupName));
   }

   protected void addToPool(int number) {
      try {
         backingComputeService.createNodesInGroup(poolGroupName, number, template);
      } catch (RunNodesException e) {
         throw Throwables.propagate(e);
      }
   }

   public abstract int idleNodes();

   public abstract int maxNodes();

   public abstract int minNodes();

   public abstract int usedNodes();

   public abstract int currentSize();

   @Override
   public Optional<ImageExtension> getImageExtension() {
      return Optional.absent();
   }

}
