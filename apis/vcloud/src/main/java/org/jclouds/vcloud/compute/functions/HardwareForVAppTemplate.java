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

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.logging.Logger;
import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class HardwareForVAppTemplate implements Function<VAppTemplate, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final VCloudClient client;
   private final FindLocationForResource findLocationForResource;
   private final VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder;

   private ReferenceType parent;

   @Inject
   protected HardwareForVAppTemplate(VCloudClient client, FindLocationForResource findLocationForResource,
            VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder) {
      this.client = checkNotNull(client, "client");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.rasdToHardwareBuilder = checkNotNull(rasdToHardwareBuilder, "rasdToHardwareBuilder");
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

      Envelope ovf = client.getOvfEnvelopeForVAppTemplate(from.getHref());
      if (ovf == null) {
         logger.warn("cannot parse hardware as no ovf envelope found for %s", from);
         return null;
      }
      if (ovf.getVirtualSystem().getVirtualHardwareSections().size() == 0) {
         logger.warn("cannot parse hardware for %s as no hardware sections exist in ovf %s", ovf);
         return null;
      }
      if (ovf.getVirtualSystem().getVirtualHardwareSections().size() > 1) {
         logger.warn("multiple hardware choices found. using first", ovf);
      }
      VirtualHardwareSection hardware = Iterables.get(ovf.getVirtualSystem().getVirtualHardwareSections(), 0);
      HardwareBuilder builder = rasdToHardwareBuilder.apply(hardware.getItems());
      builder.location(findLocationForResource.apply(checkNotNull(parent, "parent")));
      builder.ids(from.getHref().toASCIIString()).name(from.getName()).supportsImage(
               ImagePredicates.idEquals(from.getHref().toASCIIString()));
      return builder.build();
   }

   protected String getName(String name) {
      return name;
   }
}