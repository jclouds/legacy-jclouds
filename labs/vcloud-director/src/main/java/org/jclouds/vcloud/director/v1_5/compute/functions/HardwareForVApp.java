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

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;
import org.jclouds.vcloud.director.v1_5.functions.SectionForVApp;
import org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.name.Named;

/**
 * @author Adrian Cole
 */
public class HardwareForVApp implements Function<VApp, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Function<Reference, Location> findLocationForResource;
   private final VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder;
   private final SectionForVApp<VirtualHardwareSection> findVirtualHardwareSectionForVm;

   @Inject
   protected HardwareForVApp(Function<Reference, Location> findLocationForResource,
            VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder,
            SectionForVApp<VirtualHardwareSection> findVirtualHardwareSectionForVm) {
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.rasdToHardwareBuilder = checkNotNull(rasdToHardwareBuilder, "rasdToHardwareBuilder");
      this.findVirtualHardwareSectionForVm = checkNotNull(findVirtualHardwareSectionForVm, "findVirtualHardwareSectionForVm");
   }

   @Override
   public Hardware apply(VApp from) {
      checkNotNull(from, "VApp");
      // TODO make this work with composite vApps
      Vm vm = from.getChildren().getVms().size() == 0 ? null : Iterables.get(from.getChildren().getVms(), 0);
      if (vm == null)
         return null;
      
      VirtualHardwareSection hardware = findVirtualHardwareSectionForVm.apply(vm);
      HardwareBuilder builder = rasdToHardwareBuilder.apply(hardware.getItems());
      builder.location(findLocationForResource.apply(Iterables.find(checkNotNull(from, "from").getLinks(), 
            LinkPredicates.typeEquals(VCloudDirectorMediaType.VDC))));
      builder.ids(from.getHref().toASCIIString()).name(from.getName()).supportsImage(
               ImagePredicates.idEquals(from.getHref().toASCIIString()));
      builder.hypervisor("VMware");
      return builder.build();
   }
}