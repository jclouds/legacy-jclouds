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
package org.jclouds.nodepool.internal;

import java.util.Map;
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
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
import org.jclouds.nodepool.PooledComputeService;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

public class BasePooledComputeService implements PooledComputeService {

   private final ComputeService backingComputeService;
   private final String backingGroup;
   private final Template backingTemplate;
   private final int minPoolSize;
   private Map<NodeMetadata, String> groupMapping;

   public BasePooledComputeService(ComputeService backingComputeService, String backingGroup, Template backingTemplate, int minPoolSize) {
      this.backingComputeService = backingComputeService;
      this.backingGroup = backingGroup;
      this.backingTemplate = backingTemplate;
      this.minPoolSize = minPoolSize;
   }

   @Override
   public void startPool() throws RunNodesException {
      Set<? extends NodeMetadata> backingNodes =
            backingComputeService.createNodesInGroup(backingGroup, minPoolSize, backingTemplate);
      groupMapping = Maps.newHashMap();
      for (NodeMetadata node : backingNodes) {
         groupMapping.put(node, "unassigned");
      }
   }

   @Override
   public ComputeServiceContext getContext() {
      return backingComputeService.getContext();
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return backingComputeService.templateBuilder();
   }

   @Override
   public TemplateOptions templateOptions() {
      return backingComputeService.templateOptions();
   }

   @Override
   public Set<? extends Hardware> listHardwareProfiles() {
      return ImmutableSet.<Hardware>of(backingTemplate.getHardware());
   }

   @Override
   public Set<? extends Image> listImages() {
      return ImmutableSet.<Image>of(backingTemplate.getImage());
   }

   @Override
   public Set<? extends ComputeMetadata> listNodes() {
      Set<NodeMetadata> allocatedNodes = Sets.newLinkedHashSet();
      for (ComputeMetadata node : backingComputeService.listNodes()) {
         NodeMetadata metadata = backingComputeService.getNodeMetadata(node.getId());
         String group = groupMapping.get(node);
         if ("unassigned".equals(group))
            continue;
         NodeMetadata nodeWithUpdatedGroup =
               NodeMetadataBuilder.fromNodeMetadata(metadata).group(group).build();
         allocatedNodes.add(nodeWithUpdatedGroup);
      }
      return allocatedNodes;
   }

   @Override
   public Set<? extends Location> listAssignableLocations() {
      return ImmutableSet.<Location>of(backingTemplate.getLocation());
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group,
         int count, Template template) throws RunNodesException {
      throw new RuntimeException("not implemented");
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group,
         int count, TemplateOptions templateOptions)
               throws RunNodesException {
      throw new RuntimeException("not implemented");
   }

   @Override
   public Set<? extends NodeMetadata> createNodesInGroup(String group,
         int count) throws RunNodesException {
      int allocatedCount = 0;
      Set<NodeMetadata> allocatedNodes = Sets.newLinkedHashSet();
      for (NodeMetadata metadata : groupMapping.keySet()) {
         if (groupMapping.get(metadata).equals("unassigned")) {
            groupMapping.put(metadata, "group");
            NodeMetadata nodeWithUpdatedGroup =
                  NodeMetadataBuilder.fromNodeMetadata(metadata).group(group).build();
            allocatedNodes.add(nodeWithUpdatedGroup);
            allocatedCount += 1;
            if (allocatedCount == count) break;
         }
      }
      return allocatedNodes;
   }

   @Override
   public void resumeNode(String id) {
      backingComputeService.resumeNode(id);

   }

   @Override
   public void resumeNodesMatching(Predicate<NodeMetadata> filter) {
      backingComputeService.resumeNodesMatching(filter);

   }

   @Override
   public void suspendNode(String id) {
      backingComputeService.suspendNode(id);
   }

   @Override
   public void suspendNodesMatching(Predicate<NodeMetadata> filter) {
      backingComputeService.suspendNodesMatching(filter);
   }

   @Override
   public void destroyNode(String id) {

      backingComputeService.destroyNode(id);
   }

   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(
         Predicate<NodeMetadata> filter) {
      return backingComputeService.destroyNodesMatching(filter);
   }

   @Override
   public void rebootNode(String id) {
      backingComputeService.rebootNode(id);

   }

   @Override
   public void rebootNodesMatching(Predicate<NodeMetadata> filter) {
      backingComputeService.rebootNodesMatching(filter);

   }

   @Override
   public NodeMetadata getNodeMetadata(String id) {
      return backingComputeService.getNodeMetadata(id);
   }

   @Override
   public Set<? extends NodeMetadata> listNodesDetailsMatching(
         Predicate<ComputeMetadata> filter) {
      return backingComputeService.listNodesDetailsMatching(filter);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(
         Predicate<NodeMetadata> filter, String runScript)
               throws RunScriptOnNodesException {
      // TODO Auto-generated method stub
      return backingComputeService.runScriptOnNodesMatching(filter, runScript);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(
         Predicate<NodeMetadata> filter, Statement runScript)
               throws RunScriptOnNodesException {
      return backingComputeService.runScriptOnNodesMatching(filter, runScript);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(
         Predicate<NodeMetadata> filter, String runScript,
         RunScriptOptions options) throws RunScriptOnNodesException {
      return backingComputeService.runScriptOnNodesMatching(filter, runScript, options);
   }

   @Override
   public Map<? extends NodeMetadata, ExecResponse> runScriptOnNodesMatching(
         Predicate<NodeMetadata> filter, Statement runScript,
         RunScriptOptions options) throws RunScriptOnNodesException {
      return backingComputeService.runScriptOnNodesMatching(filter, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript,
         RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ListenableFuture<ExecResponse> submitScriptOnNode(String id,
         Statement runScript, RunScriptOptions options) {
      return backingComputeService.submitScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, Statement runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript,
         RunScriptOptions options) {
      return backingComputeService.runScriptOnNode(id, runScript, options);
   }

   @Override
   public ExecResponse runScriptOnNode(String id, String runScript) {
      return backingComputeService.runScriptOnNode(id, runScript);
   }

}
