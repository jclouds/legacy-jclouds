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

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

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
public abstract class BaseComputeService implements ComputeService {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final Provider<Map<String, ? extends Image>> images;
   protected final Provider<Map<String, ? extends Size>> sizes;
   protected final Provider<Map<String, ? extends Location>> locations;
   protected final Provider<TemplateBuilder> templateBuilderProvider;
   protected final String nodeNamingConvention;
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

   protected BaseComputeService(Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations,
            Provider<TemplateBuilder> templateBuilderProvider, String nodeNamingConvention,
            ComputeUtils utils, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.images = images;
      this.sizes = sizes;
      this.locations = locations;
      this.templateBuilderProvider = templateBuilderProvider;
      this.nodeNamingConvention = nodeNamingConvention;
      this.utils = utils;
      this.executor = executor;
   }

   @Override
   public Map<String, ? extends NodeMetadata> runNodesWithTag(final String tag, int count,
            final Template template) {
      checkArgument(tag.indexOf('-') == -1, "tag cannot contain hyphens");
      checkNotNull(template.getLocation(), "location");
      final Set<NodeMetadata> nodes = Sets.newHashSet();
      logger.debug(">> running %d node%s tag(%s) location(%s) image(%s) size(%s) options(%s)",
               count, count > 1 ? "s" : "", tag, template.getLocation().getId(), template
                        .getImage().getId(), template.getSize().getId(), template.getOptions());

      Map<String, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final String name : getNextNames(tag, count)) {
         responses.put(name, makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               NodeMetadata node = null;
               logger.debug(">> starting node(%s) tag(%s)", name, tag);
               node = startNode(tag, name, template);
               logger.debug("<< running node(%s)", node.getId());
               try {
                  utils.runOptionsOnNode(node, template.getOptions());
                  logger.debug("<< options applied node(%s)", node.getId());
                  nodes.add(node);
               } catch (Exception e) {
                  if (!template.getOptions().shouldDestroyOnError())
                     nodes.add(node);
               }
               return null;
            }
         }), executor));
      }
      Map<String, Exception> exceptions = awaitCompletion(responses, executor, null, logger,
               "starting nodes");
      if (exceptions.size() > 0 && template.getOptions().shouldDestroyOnError()) {
         ImmutableMap<String, ? extends ComputeMetadata> currentNodes = Maps.uniqueIndex(doGetNodes(),
                  METADATA_TO_ID);
         for (Entry<String, Exception> entry : exceptions.entrySet()) {
            logger.error(entry.getValue(), "<< error applying nodes(%s) [%s] destroying ", entry
                     .getKey(), entry.getValue().getMessage());
            destroyNode(currentNodes.get(entry.getKey()));
         }
      }
      return Maps.uniqueIndex(nodes, METADATA_TO_ID);
   }

   protected Set<String> getNextNames(final String tag, int count) {
      Set<String> names = Sets.newHashSet();
      int nodeIndex = new SecureRandom().nextInt(8096);
      Iterable<? extends ComputeMetadata> allNodes = doGetNodes();
      Map<String, ? extends ComputeMetadata> currentNodes = Maps.uniqueIndex(allNodes, METADATA_TO_NAME);
      while (names.size() < count) {
         String name = String.format(nodeNamingConvention, tag, nodeIndex++);
         if (!currentNodes.containsKey(name)) {
            names.add(name);
         }
      }
      return names;
   }

   protected abstract NodeMetadata startNode(String tag, String name, Template template);

   protected abstract boolean doDestroyNode(ComputeMetadata node);

   protected abstract Iterable<? extends ComputeMetadata> doGetNodes();

   @Override
   public void destroyNodesWithTag(String tag) { // TODO parallel
      logger.debug(">> terminating nodes by tag(%s)", tag);
      Iterable<? extends NodeMetadata> nodesToDestroy = Iterables.filter(doGetNodes(tag).values(),
               new Predicate<NodeMetadata>() {
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
   public Map<String, ? extends Location> getLocations() {
      return locations.get();
   }

   @Override
   public Map<String, ? extends ComputeMetadata> getNodes() {
      logger.debug(">> listing servers");
      ImmutableMap<String, ? extends ComputeMetadata> map = Maps.uniqueIndex(doGetNodes(), METADATA_TO_ID);
      logger.debug("<< list(%d)", map.size());
      return map;
   }

   @Override
   public void destroyNode(ComputeMetadata node) {
      checkArgument(node.getType() == ComputeType.NODE, "this is only valid for nodes, not "
               + node.getType());
      checkNotNull(node.getId(), "node.id");
      logger.debug(">> deleting node(%s)", node.getId());
      boolean successful = doDestroyNode(node);
      logger.debug("<< deleted node(%s) success(%s)", node.getId(), successful);
   }

   protected Map<String, ? extends NodeMetadata> doGetNodes(final String tag) {
      Iterable<? extends NodeMetadata> nodes = Iterables.filter(Iterables.transform(doGetNodes(),
               new Function<ComputeMetadata, NodeMetadata>() {

                  @Override
                  public NodeMetadata apply(ComputeMetadata from) {
                     return getNodeMetadata(from);
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
      Map<String, ? extends NodeMetadata> nodes = doGetNodes(tag);
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
   public TemplateBuilder templateBuilder() {
      return templateBuilderProvider.get();
   }

}