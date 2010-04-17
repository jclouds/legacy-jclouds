/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;
import static org.jclouds.util.Utils.checkNotEmpty;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BaseComputeService implements ComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final ComputeServiceContext context;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final ListNodesStrategy listNodesStrategy;
   protected final GetNodeMetadataStrategy getNodeMetadataStrategy;
   protected final RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy;
   protected final RebootNodeStrategy rebootNodeStrategy;
   protected final DestroyNodeStrategy destroyNodeStrategy;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   protected final ComputeUtils utils;
   protected final ExecutorService executor;

   private static class NodeMatchesTag implements Predicate<NodeMetadata> {
      private final String tag;

      public NodeMatchesTag(String tag) {
         this.tag = tag;
      }

      @Override
      public boolean apply(NodeMetadata from) {
         return from.getTag().equals(tag);
      }

   };

   public static Function<ComputeMetadata, String> METADATA_TO_ID = new Function<ComputeMetadata, String>() {
      @Override
      public String apply(ComputeMetadata from) {
         return from.getId();
      }
   };

   public static Function<ComputeMetadata, String> METADATA_TO_NAME = new Function<ComputeMetadata, String>() {
      @Override
      public String apply(ComputeMetadata from) {
         return from.getName();
      }
   };

   @Inject
   protected BaseComputeService(ComputeServiceContext context,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations,
            ListNodesStrategy listNodesStrategy, GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            Provider<TemplateBuilder> templateBuilderProvider, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.context = checkNotNull(context, "context");
      this.images = checkNotNull(images, "images");
      this.sizes = checkNotNull(sizes, "sizes");
      this.locations = checkNotNull(locations, "locations");
      this.listNodesStrategy = checkNotNull(listNodesStrategy, "listNodesStrategy");
      this.getNodeMetadataStrategy = checkNotNull(getNodeMetadataStrategy,
               "getNodeMetadataStrategy");
      this.runNodesAndAddToSetStrategy = checkNotNull(runNodesAndAddToSetStrategy,
               "runNodesAndAddToSetStrategy");
      this.rebootNodeStrategy = checkNotNull(rebootNodeStrategy, "rebootNodeStrategy");
      this.destroyNodeStrategy = checkNotNull(destroyNodeStrategy, "destroyNodeStrategy");
      this.templateBuilderProvider = checkNotNull(templateBuilderProvider,
               "templateBuilderProvider");
      this.utils = checkNotNull(utils, "utils");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public ComputeServiceContext getContext() {
      return context;
   }

   @Override
   public Map<String, ? extends NodeMetadata> runNodesWithTag(final String tag, int count,
            final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      checkNotNull(template.getLocation(), "location");
      logger.debug(">> running %d node%s tag(%s) location(%s) image(%s) size(%s) options(%s)",
               count, count > 1 ? "s" : "", tag, template.getLocation().getId(), template
                        .getImage().getId(), template.getSize().getId(), template.getOptions());
      final Set<NodeMetadata> nodes = Sets.newHashSet();
      Map<?, ListenableFuture<Void>> responses = runNodesAndAddToSetStrategy.execute(tag, count,
               template, nodes);
      Map<?, Exception> exceptions = awaitCompletion(responses, executor, null, logger,
               "starting nodes");
      if (exceptions.size() > 0 && template.getOptions().shouldDestroyOnError()) {
         ImmutableMap<?, ? extends ComputeMetadata> currentNodes = Maps.uniqueIndex(
                  listNodesStrategy.execute(), METADATA_TO_ID);
         for (Entry<?, Exception> entry : exceptions.entrySet()) {
            logger.error(entry.getValue(), "<< error applying nodes(%s) [%s] destroying ", entry
                     .getKey(), entry.getValue().getMessage());
            destroyNode(currentNodes.get(entry.getKey()));
         }
      }
      return Maps.uniqueIndex(nodes, METADATA_TO_ID);
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      logger.debug(">> destroying node(%s)", node.getId());
      boolean successful = destroyNodeStrategy.execute(node);
      logger.debug("<< destroyed node(%s) success(%s)", node.getId(), successful);
   }

   @Override
   public void destroyNodesWithTag(String tag) { // TODO parallel
      logger.debug(">> destroying nodes by tag(%s)", tag);
      Iterable<? extends NodeMetadata> nodesToDestroy = Iterables.filter(doGetNodesWithTag(tag)
               .values(), new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getState() != NodeState.TERMINATED;

         }
      });
      Map<NodeMetadata, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final NodeMetadata node : nodesToDestroy) {
         responses.put(node, makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               destroyNode(node);
               return null;
            }
         }), executor));
      }
      awaitCompletion(responses, executor, null, logger, "destroying nodes");
      logger.debug("<< destroyed");
   }

   @Override
   public Map<String, ? extends ComputeMetadata> getNodes() {
      logger.debug(">> listing servers");
      ImmutableMap<String, ? extends ComputeMetadata> map = Maps.uniqueIndex(listNodesStrategy
               .execute(), METADATA_TO_ID);
      logger.debug("<< list(%d)", map.size());
      return map;
   }

   /**
    * If the result of {@link ListNodesStrategy#execute} is a set of nodes, then return them.
    * Otherwise iteratively call {@link #getNodeMetadata}
    */
   protected Map<String, ? extends NodeMetadata> doGetNodesWithTag(final String tag) {
      Iterable<? extends NodeMetadata> nodes = Iterables.filter(Iterables.transform(
               listNodesStrategy.execute(), new Function<ComputeMetadata, NodeMetadata>() {

                  @Override
                  public NodeMetadata apply(ComputeMetadata from) {
                     return from instanceof NodeMetadata ? NodeMetadata.class.cast(from)
                              : getNodeMetadata(from);
                  }

               }), new Predicate<NodeMetadata>() {

         @Override
         public boolean apply(NodeMetadata input) {
            return tag.equals(input.getTag());
         }

      });
      return Maps.uniqueIndex(Iterables.filter(nodes, new NodeMatchesTag(tag)), METADATA_TO_ID);
   }

   @Override
   public Map<String, ? extends NodeMetadata> getNodesWithTag(String tag) {
      logger.debug(">> listing nodes by tag(%s)", tag);
      Map<String, ? extends NodeMetadata> nodes = doGetNodesWithTag(tag);
      logger.debug("<< list(%d)", nodes.size());
      return nodes;
   }

   @Override
   public Map<String, ? extends Size> getSizes() {
      return sizes.get();
   }

   @Override
   public Map<String, ? extends Image> getImages() {
      return images.get();
   }

   @Override
   public Map<String, ? extends Location> getLocations() {
      return locations.get();
   }

   @Override
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

   @Override
   public NodeMetadata getNodeMetadata(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      return getNodeMetadataStrategy.execute(node);
   }

   @Override
   public void rebootNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      logger.debug(">> rebooting node(%s)", node.getId());
      boolean successful = rebootNodeStrategy.execute(node);
      logger.debug("<< rebooted node(%s) success(%s)", node.getId(), successful);
   }

   @Override
   public void rebootNodesWithTag(String tag) { // TODO parallel
      logger.debug(">> rebooting nodes by tag(%s)", tag);
      Iterable<? extends NodeMetadata> nodesToReboot = Iterables.filter(doGetNodesWithTag(tag)
               .values(), new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getState() != NodeState.TERMINATED;

         }
      });
      Map<NodeMetadata, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final NodeMetadata node : nodesToReboot) {
         responses.put(node, makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               rebootNode(node);
               return null;
            }
         }), executor));
      }
      awaitCompletion(responses, executor, null, logger, "rebooting nodes");
      logger.debug("<< rebooted");
   }

   /**
    * @see #runScriptOnNodesWithTag(String, byte[], org.jclouds.compute.options.RunScriptOptions)
    */
   public Map<String, ExecResponse> runScriptOnNodesWithTag(String tag, byte[] runScript) {
      return runScriptOnNodesWithTag(tag, runScript, RunScriptOptions.NONE);
   }

   /**
    * Run the script on all nodes with the specific tag.
    * 
    * @param tag
    *           tag to look up the nodes
    * @param runScript
    *           script to run in byte format. If the script is a string, use
    *           {@link String#getBytes()} to retrieve the bytes
    * @param options
    *           nullable options to how to run the script, whether to override credentials
    * @return map with node identifiers and corresponding responses
    */
   @SuppressWarnings("unchecked")
   public Map<String, ExecResponse> runScriptOnNodesWithTag(String tag, byte[] runScript,
            @Nullable RunScriptOptions options) {
      checkNotEmpty(tag, "Tag must be provided");
      checkNotNull(runScript,
               "The script (represented by bytes array - use \"script\".getBytes() must be provided");
      if (options == null)
         options = RunScriptOptions.NONE;

      Map<String, ? extends NodeMetadata> nodes = getNodesWithTag(tag);
      Map<String, ExecResponse> responses = Maps.newHashMap();

      for (NodeMetadata node : nodes.values()) {
         if (NodeState.RUNNING != node.getState())
            continue; // make sure the node is active

         if (options.getOverrideCredentials() != null) {
            // override the credentials with provided to this method
            node = ComputeUtils.installNewCredentials(node, options.getOverrideCredentials());
         } else {
            // don't override
            checkNotNull(node.getCredentials(),
                     "If the default credentials need to be used, they can't be null");
         }

         ComputeUtils.SshCallable<?> callable;
         if (options.isRunAsRoot())
            callable = utils.runScriptOnNode(node, "computeserv.sh", runScript);
         else
            callable = utils.runScriptOnNodeAsDefaultUser(node, "computeserv.sh", runScript);

         Map<ComputeUtils.SshCallable<?>, ?> scriptRunResults = utils.runCallablesOnNode(node, Sets
                  .newHashSet(callable), null);
         responses.put(node.getId(), (ExecResponse) scriptRunResults.get(callable));
      }
      return responses;
   }
}