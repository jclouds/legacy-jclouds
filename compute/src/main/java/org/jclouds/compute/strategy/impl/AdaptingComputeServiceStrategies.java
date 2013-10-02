/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.strategy.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.withIds;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class AdaptingComputeServiceStrategies<N, H, I, L> implements CreateNodeWithGroupEncodedIntoName,
         DestroyNodeStrategy, GetNodeMetadataStrategy, GetImageStrategy, ListNodesStrategy, RebootNodeStrategy,
         ResumeNodeStrategy, SuspendNodeStrategy {
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<String, Credentials> credentialStore;
   private final PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;
   private final ComputeServiceAdapter<N, H, I, L> client;
   private final Function<N, NodeMetadata> nodeMetadataAdapter;
   private final Function<I, Image> imageAdapter;

   @Inject
   public AdaptingComputeServiceStrategies(Map<String, Credentials> credentialStore,
            PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate,
            ComputeServiceAdapter<N, H, I, L> client, Function<N, NodeMetadata> nodeMetadataAdapter,
            Function<I, Image> imageAdapter) {
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.prioritizeCredentialsFromTemplate = checkNotNull(prioritizeCredentialsFromTemplate,
               "prioritizeCredentialsFromTemplate");
      this.client = checkNotNull(client, "client");
      this.nodeMetadataAdapter = Functions.compose(addLoginCredentials, checkNotNull(nodeMetadataAdapter,
               "nodeMetadataAdapter"));
      this.imageAdapter = checkNotNull(imageAdapter, "imageAdapter");
   }

   private final Function<NodeMetadata, NodeMetadata> addLoginCredentials = new Function<NodeMetadata, NodeMetadata>() {

      @Override
      public NodeMetadata apply(NodeMetadata arg0) {
         return credentialStore.containsKey("node#" + arg0.getId()) ? NodeMetadataBuilder.fromNodeMetadata(arg0)
                  .credentials(LoginCredentials.fromCredentials(credentialStore.get("node#" + arg0.getId()))).build()
                  : arg0;
      }

      @Override
      public String toString() {
         return "addLoginCredentialsFromCredentialStore()";
      }
   };

   @Override
   public Iterable<? extends ComputeMetadata> listNodes() {
      return listDetailsOnNodesMatching(NodePredicates.all());
   }

   @Override
   public Iterable<? extends NodeMetadata> listNodesByIds(Iterable<String> ids) {
      return FluentIterable.from(listDetailsOnNodesMatching(all())).filter(withIds(toArray(ids, String.class))).toSet();
   }

   @Override
   public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      return filter(transform(client.listNodes(), nodeMetadataAdapter), filter);
   }
   
   @Override
   public Image getImage(String id) {
      I image = client.getImage(checkNotNull(id, "id"));
      if (image == null)
         return null;
      return imageAdapter.apply(image);
   }
   
   @Override
   public NodeMetadata getNode(String id) {
      N node = client.getNode(checkNotNull(id, "id"));
      if (node == null)
         return null;
      return nodeMetadataAdapter.apply(node);
   }

   // TODO: make reboot/resume/suspend return the node they affected
   @Override
   public NodeMetadata rebootNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      checkStateAvailable(node);
      client.rebootNode(id);
      // invalidate state of node
      return getNode(checkNotNull(id, "id"));
   }

   private void checkStateAvailable(NodeMetadata node) {
      checkState(node != null && node.getStatus() != Status.TERMINATED,
               "node %s terminated or unavailable! current status: %s", node, formatStatus(node));
   }

   @Override
   public NodeMetadata resumeNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      checkStateAvailable(node);
      client.resumeNode(id);
      // invalidate state of node
      return getNode(checkNotNull(id, "id"));
   }

   @Override
   public NodeMetadata suspendNode(String id) {
      NodeMetadata node = getNode(checkNotNull(id, "id"));
      checkStateAvailable(node);
      client.suspendNode(id);
      // invalidate state of node
      return getNode(checkNotNull(id, "id"));
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
      checkNotNull(group, "group (that which groups identical nodes together) must be specified");
      checkNotNull(name, "name should have %s encoded into it", group);
      checkNotNull(template, "template was null");
      checkNotNull(template.getOptions(), "template options was null");

      NodeAndInitialCredentials<N> from = client.createNodeWithGroupEncodedIntoName(group, name, template);
      LoginCredentials fromNode = from.getCredentials();
      LoginCredentials creds = prioritizeCredentialsFromTemplate.apply(template, fromNode);
      String credsKey = "node#" + from.getNodeId();
      if (creds != null) {
         credentialStore.put(credsKey, creds);
      } else {
         logger.trace("node(%s) creation did not return login credentials", from.getNodeId());
      }
      NodeMetadata node = nodeMetadataAdapter.apply(from.getNode());
      //TODO: test case that proves this
      checkState(node.getId().equals(from.getNodeId()),
               "nodeAndInitialCredentials.getNodeId() returned %s, while parsed nodemetadata.getId() returned %s", from
                        .getNodeId(), node.getId());
      if (creds != null) {
         Credentials credsFromCache = credentialStore.get(credsKey);
         //TODO: test case that proves this
         checkState(node.getCredentials().equals(credsFromCache),
                  "credentialStore.get(%s): %s does not match node(%s).getCredentials(): %s", credsKey, credsFromCache,
                  node.getId(), node.getCredentials());
      }
      return node;
   }

}
