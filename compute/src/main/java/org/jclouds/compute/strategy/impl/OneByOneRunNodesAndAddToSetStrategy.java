/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.options.GetNodesOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.logging.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class OneByOneRunNodesAndAddToSetStrategy implements RunNodesAndAddToSetStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final AddNodeWithTagStrategy addNodeWithTagStrategy;
   protected final ListNodesStrategy listNodesStrategy;
   protected final String nodeNamingConvention;
   protected final ComputeUtils utils;
   protected final ExecutorService executor;

   @Inject
   protected OneByOneRunNodesAndAddToSetStrategy(AddNodeWithTagStrategy addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.addNodeWithTagStrategy = addNodeWithTagStrategy;
      this.listNodesStrategy = listNodesStrategy;
      this.nodeNamingConvention = nodeNamingConvention;
      this.utils = utils;
      this.executor = executor;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(final String tag, final int count,
            final Template template, final Set<NodeMetadata> nodes) {
      Map<String, ListenableFuture<Void>> responses = Maps.newHashMap();
      for (final String name : getNextNames(tag, count)) {
         responses.put(name, makeListenable(executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               NodeMetadata node = null;
               logger.debug(">> starting node(%s) tag(%s)", name, tag);
               node = addNodeWithTagStrategy.execute(tag, name, template);
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
      return responses;
   }

   protected Set<String> getNextNames(final String tag, int count) {
      Set<String> names = Sets.newHashSet();
      int nodeIndex = new SecureRandom().nextInt(8096);
      Map<String, ? extends ComputeMetadata> currentNodes = Maps.uniqueIndex(listNodesStrategy
               .execute(GetNodesOptions.NONE), BaseComputeService.METADATA_TO_NAME);
      while (names.size() < count) {
         String name = String.format(nodeNamingConvention, tag, nodeIndex++);
         if (!currentNodes.containsKey(name)) {
            names.add(name);
         }
      }
      return names;
   }

}