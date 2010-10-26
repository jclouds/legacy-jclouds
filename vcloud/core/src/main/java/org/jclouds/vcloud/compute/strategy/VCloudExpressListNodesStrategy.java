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

package org.jclouds.vcloud.compute.strategy;

import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_BLACKLIST_NODES;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeMetadataBuilder;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.Org;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.internal.util.ImmutableSet;

/**
 * @author Adrian Cole
 */
// TODO REFACTOR!!! needs to be parallel
@Singleton
public class VCloudExpressListNodesStrategy implements ListNodesStrategy {
   @Resource
   @Named(COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   protected final VCloudExpressGetNodeMetadataStrategy getNodeMetadata;
   protected final CommonVCloudClient client;
   protected final FindLocationForResource findLocationForResourceInVDC;
   Set<String> blackListVAppNames = ImmutableSet.<String> of();

   @Inject(optional = true)
   void setBlackList(@Named(PROPERTY_BLACKLIST_NODES) String blackListNodes) {
      if (blackListNodes != null && !"".equals(blackListNodes))
         this.blackListVAppNames = ImmutableSet.copyOf(Splitter.on(',').split(blackListNodes));
   }

   private final Supplier<Map<String, ReferenceType>> orgNameToEndpoint;

   @Inject
   protected VCloudExpressListNodesStrategy(CommonVCloudClient client,
         @Org Supplier<Map<String, ReferenceType>> orgNameToEndpoint,
         VCloudExpressGetNodeMetadataStrategy getNodeMetadata, FindLocationForResource findLocationForResourceInVDC) {
      this.client = client;
      this.orgNameToEndpoint = orgNameToEndpoint;
      this.getNodeMetadata = getNodeMetadata;
      this.findLocationForResourceInVDC = findLocationForResourceInVDC;
   }

   @Override
   public Iterable<ComputeMetadata> listNodes() {
      Set<ComputeMetadata> nodes = Sets.newHashSet();
      for (String org : orgNameToEndpoint.get().keySet()) {
         for (ReferenceType vdc : client.findOrgNamed(org).getVDCs().values()) {
            for (ReferenceType resource : client.getVDC(vdc.getHref()).getResourceEntities().values()) {
               if (validVApp(resource)) {
                  nodes.add(convertVAppToComputeMetadata(vdc, resource));
               }
            }
         }
      }
      return nodes;
   }

   private boolean validVApp(ReferenceType resource) {
      return resource.getType().equals(VCloudMediaType.VAPP_XML) && !blackListVAppNames.contains(resource.getName());
   }

   private ComputeMetadata convertVAppToComputeMetadata(ReferenceType vdc, ReferenceType resource) {
      ComputeMetadataBuilder builder = new ComputeMetadataBuilder(ComputeType.NODE);
      builder.providerId(resource.getHref().toASCIIString());
      builder.name(resource.getName());
      builder.id(resource.getHref().toASCIIString());
      builder.location(findLocationForResourceInVDC.apply(vdc));
      return builder.build();
   }

   @Override
   public Iterable<NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      Set<NodeMetadata> nodes = Sets.newHashSet();
      for (String org : orgNameToEndpoint.get().keySet()) {
         for (ReferenceType vdc : client.findOrgNamed(org).getVDCs().values()) {
            for (ReferenceType resource : client.getVDC(vdc.getHref()).getResourceEntities().values()) {
               if (validVApp(resource) && filter.apply(convertVAppToComputeMetadata(vdc, resource))) {
                  addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
               }
            }
         }
      }
      return nodes;
   }

   @VisibleForTesting
   void addVAppToSetRetryingIfNotYetPresent(Set<NodeMetadata> nodes, ReferenceType vdc, ReferenceType resource) {
      NodeMetadata node = null;
      int i = 0;
      while (node == null && i++ < 3) {
         try {
            node = getNodeMetadata.getNode(resource.getHref().toASCIIString());
            nodes.add(node);
         } catch (NullPointerException e) {
            logger.warn("vApp %s not yet present in vdc %s", resource.getName(), vdc.getName());
         }
      }
   }

}