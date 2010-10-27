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

import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class EncodeTagIntoNameRunNodesAndAddToSetStrategy implements RunNodesAndAddToSetStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final AddNodeWithTagStrategy addNodeWithTagStrategy;
   protected final ListNodesStrategy listNodesStrategy;
   protected final String nodeNamingConvention;
   protected final ComputeUtils utils;
   protected final ExecutorService executor;

   @Inject
   protected EncodeTagIntoNameRunNodesAndAddToSetStrategy(AddNodeWithTagStrategy addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy, @Named("NAMING_CONVENTION") String nodeNamingConvention,
            ComputeUtils utils, @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.addNodeWithTagStrategy = addNodeWithTagStrategy;
      this.listNodesStrategy = listNodesStrategy;
      this.nodeNamingConvention = nodeNamingConvention;
      this.utils = utils;
      this.executor = executor;
   }

   /**
    * This implementation gets a list of acceptable node names to encode the tag into, then it
    * simultaneously runs the nodes and applies options to them.
    */
   @Override
   public Map<?, Future<Void>> execute(final String tag, final int count, final Template template,
            final Set<NodeMetadata> nodes, final Map<NodeMetadata, Exception> badNodes) {
      Map<String, Future<Void>> responses = Maps.newHashMap();
      for (final String name : getNextNames(tag, template, count)) {
         responses.put(name, executor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
               NodeMetadata node = null;
               logger.debug(">> starting node(%s) tag(%s)", name, tag);
               node = addNodeWithTagStrategy.addNodeWithTag(tag, name, template);
               logger.debug("<< %s node(%s)", node.getState(), node.getId());
               utils.runOptionsOnNodeAndAddToGoodSetOrPutExceptionIntoBadMap(node, badNodes, nodes,
                        template.getOptions()).call();
               return null;
            }
         }));
      }
      return responses;
   }

   /**
    * Find the next node names that can be used. These will be derived from the tag and the
    * template. We will pre-allocate a specified quantity, and attempt to verify that there is no
    * name conflict with the current service.
    * 
    * @param tag
    * @param count
    * @param template
    * @return
    */
   protected Set<String> getNextNames(final String tag, final Template template, int count) {
      Set<String> names = Sets.newHashSet();
      Iterable<? extends ComputeMetadata> currentNodes = listNodesStrategy.listNodes();
      int maxTries = 100;
      int currentTries = 0;
      while (names.size() < count && currentTries++ < maxTries) {
         final String name = getNextName(tag, template);
         if (!Iterables.any(currentNodes, new Predicate<ComputeMetadata>() {

            @Override
            public boolean apply(ComputeMetadata input) {
               return name.equals(input.getName());
            }

         })) {
            names.add(name);
         }
      }
      return names;
   }

   /**
    * Get a name using a random mechanism that still ties all nodes in a tag together.
    * 
    * This implementation will pass the tag and a hex formatted random number to the configured
    * naming convention.
    * 
    */
   protected String getNextName(final String tag, final Template template) {
      return String.format(nodeNamingConvention, tag, Integer.toHexString(new SecureRandom().nextInt(4095)));
   }

}