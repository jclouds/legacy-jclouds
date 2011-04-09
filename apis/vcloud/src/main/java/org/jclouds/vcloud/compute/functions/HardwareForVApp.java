/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class HardwareForVApp implements Function<VApp, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final FindLocationForResource findLocationForResource;
   private final ResourceAllocationsToVolumes resourceAllocationsToVolumes;

   @Inject
   protected HardwareForVApp(FindLocationForResource findLocationForResource,
         ResourceAllocationsToVolumes resourceAllocationsToVolumes) {
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.resourceAllocationsToVolumes = checkNotNull(resourceAllocationsToVolumes, "resourceAllocationsToVolumes");
   }

   @Override
   public Hardware apply(VApp from) {
      checkNotNull(from, "VApp");
      // TODO make this work with composite vApps
      Vm vm = from.getChildren().size() == 0 ? null : Iterables.get(from.getChildren(), 0);
      if (vm == null)
         return null;
      Location location = findLocationForResource.apply(checkNotNull(from, "from").getVDC());

      VirtualHardwareSection hardware = vm.getVirtualHardwareSection();

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
}