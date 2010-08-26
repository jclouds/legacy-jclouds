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
import static com.google.common.collect.Iterables.find;
import static org.jclouds.vcloud.predicates.VCloudPredicates.resourceType;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class SizeForVAppTemplate implements Function<VAppTemplate, Size> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final VCloudClient client;
   private final FindLocationForResource findLocationForResource;
   private ReferenceType parent;

   @Inject
   protected SizeForVAppTemplate(VCloudClient client, FindLocationForResource findLocationForResource) {
      this.client = checkNotNull(client, "client");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
   }

   public SizeForVAppTemplate withParent(ReferenceType parent) {
      this.parent = parent;
      return this;
   }

   @Override
   public Size apply(VAppTemplate from) {
      checkNotNull(from, "VAppTemplate");

      if (!from.isOvfDescriptorUploaded()) {
         logger.warn("cannot parse size as ovf descriptor for %s is not uploaded", from);
         return null;
      }

      OvfEnvelope ovf = client.getOvfEnvelopeForVAppTemplate(from.getHref());
      if (ovf == null) {
         logger.warn("cannot parse size as no ovf envelope found for %s", from);
         return null;
      }

      Location location = findLocationForResource.apply(checkNotNull(parent, "parent"));
      if (ovf.getVirtualSystem().getHardware().size() == 0) {
         logger.warn("cannot parse size for %s as no hardware sections exist in ovf %s", ovf);
         return null;
      }
      if (ovf.getVirtualSystem().getHardware().size() > 1) {
         logger.warn("multiple hardware choices found. using first", ovf);
      }
      VirtualHardwareSection hardware = Iterables.get(ovf.getVirtualSystem().getHardware(), 0);

      int ram = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.MEMORY)).getVirtualQuantity();
      ResourceAllocation diskR = find(hardware.getResourceAllocations(), resourceType(ResourceType.DISK_DRIVE));
      int disk = (int) (((diskR instanceof VCloudHardDisk) ? VCloudHardDisk.class.cast(diskR).getCapacity() : diskR
               .getVirtualQuantity()) / 1024l);

      double cores = (int) find(hardware.getResourceAllocations(), resourceType(ResourceType.PROCESSOR))
               .getVirtualQuantity();

      return new SizeImpl(from.getHref().toASCIIString(), from.getName()
               + String.format(": vpu(%.1f), ram(%d), disk(%d)", cores, ram, disk), from.getHref().toASCIIString(),
               location, null, ImmutableMap.<String, String> of(), cores, ram, disk, ImagePredicates.idEquals(from
                        .getHref().toASCIIString()));

   }

   protected String getName(String name) {
      return name;
   }
}