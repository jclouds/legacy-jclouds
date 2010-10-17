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

package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class HardwareForVAppTemplate implements Function<VAppTemplate, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final VCloudClient client;
   private final FindLocationForResource findLocationForResource;
   private final ResourceAllocationsToVolumes resourceAllocationsToVolumes;

   private ReferenceType parent;

   @Inject
   protected HardwareForVAppTemplate(VCloudClient client, FindLocationForResource findLocationForResource,
         ResourceAllocationsToVolumes resourceAllocationsToVolumes) {
      this.client = checkNotNull(client, "client");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.resourceAllocationsToVolumes = checkNotNull(resourceAllocationsToVolumes, "resourceAllocationsToVolumes");
   }

   public HardwareForVAppTemplate withParent(ReferenceType parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Hardware apply(VAppTemplate from) {
      checkNotNull(from, "VAppTemplate");

      if (!from.isOvfDescriptorUploaded()) {
         logger.warn("cannot parse hardware as ovf descriptor for %s is not uploaded", from);
         return null;
      }

      OvfEnvelope ovf = client.getOvfEnvelopeForVAppTemplate(from.getHref());
      if (ovf == null) {
         logger.warn("cannot parse hardware as no ovf envelope found for %s", from);
         return null;
      }

      Location location = findLocationForResource.apply(checkNotNull(parent, "parent"));
      if (ovf.getVirtualSystem().getHardware().size() == 0) {
         logger.warn("cannot parse hardware for %s as no hardware sections exist in ovf %s", ovf);
         return null;
      }
      if (ovf.getVirtualSystem().getHardware().size() > 1) {
         logger.warn("multiple hardware choices found. using first", ovf);
      }
      VirtualHardwareSection hardware = Iterables.get(ovf.getVirtualSystem().getHardware(), 0);

      int ram = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity();

      List<Processor> processors = Lists.newArrayList(transform(
            filter(hardware.getResourceAllocations(), resourceType(ResourceType.PROCESSOR)),
            new Function<ResourceAllocation, Processor>() {

               @Override
               public Processor apply(ResourceAllocation arg0) {
                  return new Processor(arg0.getVirtualQuantity(), 1);
               }

            }));
      List<Volume> volumes = Lists.newArrayList(resourceAllocationsToVolumes.apply(hardware.getResourceAllocations()));
      return new HardwareBuilder().ids(from.getHref().toASCIIString()).uri(from.getHref()).name(from.getName())
            .location(location).processors(processors).ram(ram).volumes(volumes)
            .supportsImage(ImagePredicates.idEquals(from.getHref().toASCIIString())).build();

   }

   protected String getName(String name) {
      return name;
   }
}