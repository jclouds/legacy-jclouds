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
package org.jclouds.vcloud.compute.strategy;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.compute.functions.VCloudGetNodeMetadata;
import org.jclouds.vcloud.domain.NamedResource;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudListNodesStrategy implements ListNodesStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;
   protected final VCloudGetNodeMetadata getNodeMetadata;
   protected final VCloudClient client;
   protected final FindLocationForResource findLocationForResourceInVDC;

   @Inject
   protected VCloudListNodesStrategy(VCloudClient client, VCloudGetNodeMetadata getNodeMetadata,
            FindLocationForResource findLocationForResourceInVDC) {
      this.client = client;
      this.getNodeMetadata = getNodeMetadata;
      this.findLocationForResourceInVDC = findLocationForResourceInVDC;
   }

   @Override
   public Iterable<ComputeMetadata> list() {
      Set<ComputeMetadata> nodes = Sets.newHashSet();
      for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
         for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
            if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
               nodes.add(convertVAppToComputeMetadata(vdc, resource));
            }
         }
      }
      return nodes;
   }

   private ComputeMetadata convertVAppToComputeMetadata(NamedResource vdc, NamedResource resource) {
      Location location = findLocationForResourceInVDC.apply(vdc);
      return new ComputeMetadataImpl(ComputeType.NODE, resource.getId(), resource.getName(), resource.getId(),
               location, null, ImmutableMap.<String, String> of());
   }

   @Override
   public Iterable<NodeMetadata> listDetailsOnNodesMatching(Predicate<ComputeMetadata> filter) {
      Set<NodeMetadata> nodes = Sets.newHashSet();
      for (NamedResource vdc : client.getDefaultOrganization().getVDCs().values()) {
         for (NamedResource resource : client.getVDC(vdc.getId()).getResourceEntities().values()) {
            if (resource.getType().equals(VCloudMediaType.VAPP_XML)
                     && filter.apply(convertVAppToComputeMetadata(vdc, resource))) {
               addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
            }
         }
      }
      return nodes;
   }

   @VisibleForTesting
   void addVAppToSetRetryingIfNotYetPresent(Set<NodeMetadata> nodes, NamedResource vdc, NamedResource resource) {
      NodeMetadata node = null;
      int i = 0;
      while (node == null && i++ < 3) {
         try {
            node = getNodeMetadata.execute(resource.getId());
            nodes.add(node);
         } catch (NullPointerException e) {
            logger.warn("vApp %s not yet present in vdc %s", resource.getId(), vdc.getId());
         }
      }
   }

}