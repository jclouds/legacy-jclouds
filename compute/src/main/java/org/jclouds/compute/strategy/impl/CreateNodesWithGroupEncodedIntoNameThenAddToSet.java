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

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.concurrent.Futures.compose;

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
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateNodesWithGroupEncodedIntoNameThenAddToSet implements CreateNodesInGroupThenAddToSet {

   private class AddNode implements Callable<NodeMetadata> {
      private final String name;
      private final String tag;
      private final Template template;

      private AddNode(String name, String tag, Template template) {
         this.name = checkNotNull(name, "name");
         this.tag = checkNotNull(tag, "tag");
         this.template = checkNotNull(template, "template");
      }

      @Override
      public NodeMetadata call() throws Exception {
         NodeMetadata node = null;
         logger.debug(">> adding node location(%s) name(%s) image(%s) hardware(%s)",
                  template.getLocation().getId(), name, template.getImage().getProviderId(), template.getHardware()
                           .getProviderId());
         node = addNodeWithTagStrategy.createNodeWithGroupEncodedIntoName(tag, name, template);
         logger.debug("<< %s node(%s)", node.getState(), node.getId());
         return node;
      }

      public String toString() {
         return toStringHelper(this).add("name", name).add("tag", tag).add("template", template).toString();
      }

   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy;
   protected final ListNodesStrategy listNodesStrategy;
   protected final String nodeNamingConvention;
   protected final ExecutorService executor;
   protected final CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;

   @Inject
   protected CreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory) {
      this.addNodeWithTagStrategy = addNodeWithTagStrategy;
      this.listNodesStrategy = listNodesStrategy;
      this.nodeNamingConvention = nodeNamingConvention;
      this.executor = executor;
      this.customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   }

   /**
    * This implementation gets a list of acceptable node names to encode the tag into, then it
    * simultaneously runs the nodes and applies options to them.
    */
   @Override
   public Map<?, Future<Void>> execute(String tag, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      Map<String, Future<Void>> responses = newLinkedHashMap();
      for (String name : getNextNames(tag, template, count)) {
         responses.put(name, compose(executor.submit(new AddNode(name, tag, template)),
                  customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory.create(template.getOptions(),
                           goodNodes, badNodes, customizationResponses), executor));
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
      Set<String> names = newLinkedHashSet();
      Iterable<? extends ComputeMetadata> currentNodes = listNodesStrategy.listNodes();
      int maxTries = 100;
      int currentTries = 0;
      while (names.size() < count && currentTries++ < maxTries) {
         final String name = getNextName(tag, template);
         if (!any(currentNodes, new Predicate<ComputeMetadata>() {

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