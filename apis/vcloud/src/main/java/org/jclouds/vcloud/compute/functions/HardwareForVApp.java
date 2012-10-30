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
package org.jclouds.vcloud.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.Vm;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
public class HardwareForVApp implements Function<VApp, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Function<ReferenceType, Location> findLocationForResource;
   private final VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder;

   @Inject
   protected HardwareForVApp(Function<ReferenceType, Location> findLocationForResource,
            VCloudHardwareBuilderFromResourceAllocations rasdToHardwareBuilder) {
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.rasdToHardwareBuilder = checkNotNull(rasdToHardwareBuilder, "rasdToHardwareBuilder");
   }

   @Override
   public Hardware apply(VApp from) {
      checkNotNull(from, "VApp");
      // TODO make this work with composite vApps
      Vm vm = from.getChildren().size() == 0 ? null : Iterables.get(from.getChildren(), 0);
      if (vm == null)
         return null;

      VirtualHardwareSection hardware = vm.getVirtualHardwareSection();
      HardwareBuilder builder = rasdToHardwareBuilder.apply(hardware.getItems());
      builder.location(findLocationForResource.apply(checkNotNull(from, "from").getVDC()));
      builder.ids(from.getHref().toASCIIString()).name(from.getName()).supportsImage(
               ImagePredicates.idEquals(from.getHref().toASCIIString()));
      builder.hypervisor("VMware");
      return builder.build();
   }
}
