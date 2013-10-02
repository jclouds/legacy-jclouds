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

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * creates futures that correlate to
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateNodesWithGroupEncodedIntoNameThenAddToSet implements CreateNodesInGroupThenAddToSet {

   protected class AddNode implements Callable<AtomicReference<NodeMetadata>> {
      private final String name;
      private final String group;
      private final Template template;

      public AddNode(String name, String group, Template template) {
         this.name = checkNotNull(name, "name");
         this.group = checkNotNull(group, "group");
         this.template = checkNotNull(template, "template");
      }

      @Override
      public AtomicReference<NodeMetadata> call() throws Exception {
         NodeMetadata node = null;
         logger.debug(">> adding node location(%s) name(%s) image(%s) hardware(%s)", template.getLocation().getId(),
                  name, template.getImage().getProviderId(), template.getHardware().getProviderId());
         node = addNodeWithGroupStrategy.createNodeWithGroupEncodedIntoName(group, name, template);
         logger.debug("<< %s node(%s)", formatStatus(node), node.getId());
         return new AtomicReference<NodeMetadata>(node);
      }

      public String toString() {
         return toStringHelper(this).add("name", name).add("group", group).add("template", template).toString();
      }

   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy;
   protected final ListNodesStrategy listNodesStrategy;
   protected final GroupNamingConvention.Factory namingConvention;
   protected final ListeningExecutorService userExecutor;
   protected final CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;

   @Inject
   protected CreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
            ListNodesStrategy listNodesStrategy,
            GroupNamingConvention.Factory namingConvention,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory) {
      this.addNodeWithGroupStrategy = addNodeWithGroupStrategy;
      this.listNodesStrategy = listNodesStrategy;
      this.namingConvention = namingConvention;
      this.userExecutor = userExecutor;
      this.customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory = customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory;
   }

   /**
    * This implementation gets a list of acceptable node names to encode the group into, then it
    * simultaneously runs the nodes and applies options to them.
    */
   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      Map<String, ListenableFuture<Void>> responses = newLinkedHashMap();
      for (String name : getNextNames(group, template, count)) {
         responses.put(name, Futures.transform(createNodeInGroupWithNameAndTemplate(group, name, template),
                  customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory.create(template.getOptions(), goodNodes,
                           badNodes, customizationResponses), userExecutor));
      }
      return responses;
   }

   /**
    * This calls logic necessary to create a node and convert it from its provider-specific object
    * to the jclouds {@link NodeMetadata} object. This call directly precedes customization, such as
    * executing scripts.
    * 
    * </p> The outcome of this operation does not imply the node is {@link Status#RUNNING
    * running}. If you want to insert logic after the node is created, yet before an attempt to
    * customize the node, then append your behaviour to this method.
    * 
    * ex. to attach an ip address post-creation
    * 
    * <pre>
    * &#064;Override
    * protected ListenableFuture&lt;AtomicReference&lt;NodeMetadata&gt;&gt; createNodeInGroupWithNameAndTemplate(String group, String name,
    *          Template template) {
    * 
    *    ListenableFuture&lt;AtomicReference&lt;NodeMetadata&gt;&gt; future = super.addNodeIntoGroupWithNameAndTemplate(group, name, template);
    *    return Futures.compose(future, new Function&lt;AtomicReference&lt;NodeMetadata&gt;, AtomicReference&lt;NodeMetadata&gt;&gt;() {
    * 
    *       &#064;Override
    *       public AtomicReference&lt;NodeMetadata&gt; apply(AtomicReference&lt;NodeMetadata&gt; input) {
    *          NodeMetadata node = input.get();
    *          // allocate and attach an ip
    *          input.set(NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.of(ip.getIp())).build());
    *          return input;
    *       }
    * 
    *    }, executor);
    * }
    * </pre>
    * 
    * @param group group the node belongs to
    * @param name generated name of the node
    * @param template user-specified template
    * @return node that is created, yet not necessarily in {@link Status#RUNNING}
    */
   protected ListenableFuture<AtomicReference<NodeMetadata>> createNodeInGroupWithNameAndTemplate(String group, String name,
            Template template) {
      return userExecutor.submit(new AddNode(name, group, template));
   }

   /**
    * Find the next node names that can be used. These will be derived from the group and the
    * template. We will pre-allocate a specified quantity, and attempt to verify that there is no
    * name conflict with the current service.
    * 
    * @param group
    * @param count
    * @param template
    * @return
    */
   protected Set<String> getNextNames(final String group, final Template template, int count) {
      Set<String> names = newLinkedHashSet();
      Iterable<? extends ComputeMetadata> currentNodes = listNodesStrategy.listNodes();
      int maxTries = 100;
      int currentTries = 0;
      while (names.size() < count && currentTries++ < maxTries) {
         final String name = namingConvention.createWithoutPrefix().uniqueNameForGroup(group);
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

}
