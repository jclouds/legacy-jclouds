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
package org.jclouds.vcloud.director.v1_5.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.dmtf.ovf.VirtualHardwareSection;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class HardwareForVAppTemplate implements Function<VAppTemplate, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Function<VAppTemplate, Envelope> templateToEnvelope;
   private final FindLocationForResource findLocationForResource;
   private final VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder;

   @Inject
   protected HardwareForVAppTemplate(Function<VAppTemplate, Envelope> templateToEnvelope,
            FindLocationForResource findLocationForResource,
            VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder) {
      this.templateToEnvelope = checkNotNull(templateToEnvelope, "templateToEnvelope");
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.rasdToHardwareBuilder = checkNotNull(rasdToHardwareBuilder, "rasdToHardwareBuilder");
   }

   @Override
   public Hardware apply(VAppTemplate from) {
      checkNotNull(from, "VAppTemplate");

      Envelope ovf = templateToEnvelope.apply(from);

      if (ovf.getVirtualSystem().getVirtualHardwareSections().size() > 1) {
         logger.warn("multiple hardware choices found. using first", ovf);
      }
      VirtualHardwareSection hardware = Iterables.get(ovf.getVirtualSystem().getVirtualHardwareSections(), 0);
      HardwareBuilder builder = rasdToHardwareBuilder.apply(hardware.getItems());
      Link vdc = Iterables.find(checkNotNull(from, "from").getLinks(), LinkPredicates.typeEquals(VCloudDirectorMediaType.VDC));
      if (vdc != null) {
         builder.location(findLocationForResource.apply(vdc));
      } else {
         // otherwise, it could be in a public catalog, which is not assigned to a VDC
      }
      builder.ids(from.getHref().toASCIIString()).name(from.getName()).supportsImage(
               ImagePredicates.idEquals(from.getHref().toASCIIString()));
      builder.hypervisor("VMware");
      return builder.build();

   }

   protected String getName(String name) {
      return name;
   }
}