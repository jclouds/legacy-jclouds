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
package org.jclouds.openstack.nova.v1_1.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.concurrent.Futures;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.functions.AllocateAndAddFloatingIpToNode;
import org.jclouds.openstack.nova.v1_1.compute.options.NovaTemplateOptions;

import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
         CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   private final AllocateAndAddFloatingIpToNode allocateAndAddFloatingIpToNode;
   private final NovaClient novaClient;

   @Inject
   protected ApplyNovaTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet(
            CreateNodeWithGroupEncodedIntoName addNodeWithTagStrategy,
            ListNodesStrategy listNodesStrategy,
            @Named("NAMING_CONVENTION") String nodeNamingConvention,
            CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor,
            AllocateAndAddFloatingIpToNode allocateAndAddFloatingIpToNode, NovaClient novaClient) {
      super(addNodeWithTagStrategy, listNodesStrategy, nodeNamingConvention, executor,
               customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);
      this.allocateAndAddFloatingIpToNode = checkNotNull(allocateAndAddFloatingIpToNode,
               "allocateAndAddFloatingIpToNode");
      this.novaClient = checkNotNull(novaClient, "novaClient");
   }

   @Override
   public Map<?, Future<Void>> execute(String group, int count, Template template, Set<NodeMetadata> goodNodes,
            Map<NodeMetadata, Exception> badNodes, Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {
      // TODO: make NovaTemplateOptions with the following:
      // security group, key pair
      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(template.getOptions());

      String zone = template.getLocation().getId();

      if (templateOptions.isAutoAssignFloatingIp()) {
         checkArgument(novaClient.getFloatingIPExtensionForZone(zone).isPresent(),
                  "Floating IP settings are required by configuration, but the extension is not available!");
      }

      return super.execute(group, count, template, goodNodes, badNodes, customizationResponses);
   }

   @Override
   protected Future<AtomicReference<NodeMetadata>> createNodeInGroupWithNameAndTemplate(String group,
            final String name, Template template) {

      Future<AtomicReference<NodeMetadata>> future = super.createNodeInGroupWithNameAndTemplate(group, name, template);
      NovaTemplateOptions templateOptions = NovaTemplateOptions.class.cast(template.getOptions());

      if (templateOptions.isAutoAssignFloatingIp()) {
         return Futures.compose(future, allocateAndAddFloatingIpToNode, executor);
      } else {
         return future;
      }
   }

}