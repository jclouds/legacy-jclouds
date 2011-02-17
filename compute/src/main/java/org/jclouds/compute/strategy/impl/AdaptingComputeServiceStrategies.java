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

package org.jclouds.compute.strategy.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class AdaptingComputeServiceStrategies<N, H, I, L> implements CreateNodeWithGroupEncodedIntoName, DestroyNodeStrategy,
         GetNodeMetadataStrategy, ListNodesStrategy, RebootNodeStrategy, ResumeNodeStrategy, SuspendNodeStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, Credentials> credentialStore;
   private final ComputeServiceAdapter<N, H, I, L> client;
   private final Function<N, NodeMetadata> nodeMetadataAdapter;

   @Inject
   public AdaptingComputeServiceStrategies(Map<String, Credentials> credentialStore,
            ComputeServiceAdapter<N, H, I, L> client, Function<N, NodeMetadata> nodeMetadataAdapter) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.client = checkNotNull(client, "client");
      this.nodeMetadataAdapter = checkNotNull(nodeMetadataAdapter, "nodeMetadataAdapter");
   }

   @Override
   public Iterable<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      return Iterables.filter(Iterables.transform(client.listNodes(), nodeMetadataAdapter), filter);
   }

   @Override
   public NodeMetadata getNode(String id) {
      N node = client.getNode(checkNotNull(id, "id"));
      return node == null ? null : nodeMetadataAdapter.apply(node);
   }

   @Override
   public NodeMetadata rebootNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      if (node == null || node.getState() == NodeState.TERMINATED)
         return node;
      client.rebootNode(id);
      return node;
   }

   @Override
   public NodeMetadata resumeNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      if (node == null || node.getState() == NodeState.TERMINATED || node.getState() == NodeState.RUNNING)
         return node;
      client.resumeNode(id);
      return node;
   }

   @Override
   public NodeMetadata suspendNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      if (node == null || node.getState() == NodeState.TERMINATED || node.getState() == NodeState.SUSPENDED)
         return node;
      client.suspendNode(id);
      return node;
   }

   @Override
   public NodeMetadata destroyNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      if (node == null)
         return node;
      client.destroyNode(id);
      return node;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoName(String group, String name, Template template) {
      checkState(group != null, "group (that which groups identical nodes together) must be specified");
      checkState(name != null && name.indexOf(group) != -1, "name should have %s encoded into it", group);
      checkState(template != null, "template must be specified");

      N from = client.createNodeWithGroupEncodedIntoNameThenStoreCredentials(group, name, template, credentialStore);
      NodeMetadata node = nodeMetadataAdapter.apply(from);
      return node;
   }

}