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

import javax.inject.Inject;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.domain.ovf.VCloudHardDisk;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class SizeForVAppTemplate implements Function<VAppTemplate, Size> {
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

      OvfEnvelope ovf = client.getOvfEnvelopeForVAppTemplate(from.getHref());

      Location location = findLocationForResource.apply(checkNotNull(parent, "parent"));
      int ram = (int) find(ovf.getVirtualSystem().getHardware().getResourceAllocations(),
               resourceType(ResourceType.MEMORY)).getVirtualQuantity();
      ResourceAllocation diskR = find(ovf.getVirtualSystem().getHardware().getResourceAllocations(),
               resourceType(ResourceType.DISK_DRIVE));
      int disk = (int) (((diskR instanceof VCloudHardDisk) ? VCloudHardDisk.class.cast(diskR).getCapacity() : diskR
               .getVirtualQuantity()) / 1024l);

      double cores = (int) find(ovf.getVirtualSystem().getHardware().getResourceAllocations(),
               resourceType(ResourceType.PROCESSOR)).getVirtualQuantity();

      return new SizeImpl(from.getHref().toASCIIString(), from.getName()
               + String.format(": vpu(%.1f), ram(%d), disk(%d)", cores, ram, disk), from.getHref().toASCIIString(),
               location, null, ImmutableMap.<String, String> of(), cores, ram, disk, ImagePredicates.idEquals(from
                        .getHref().toASCIIString()));

   }

   protected String getName(String name) {
      return name;
   }
}