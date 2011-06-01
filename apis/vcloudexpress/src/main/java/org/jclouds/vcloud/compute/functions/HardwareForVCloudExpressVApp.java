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

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.cim.functions.HardwareBuilderFromResourceAllocations;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudExpressVApp;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
public class HardwareForVCloudExpressVApp implements Function<VCloudExpressVApp, Hardware> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final Function<ReferenceType, Location> findLocationForResource;
   private final HardwareBuilderFromResourceAllocations rasdToHardwareBuilder;

   @Inject
   protected HardwareForVCloudExpressVApp(Function<ReferenceType, Location> findLocationForResource,
         HardwareBuilderFromResourceAllocations rasdToHardwareBuilder) {
      this.findLocationForResource = checkNotNull(findLocationForResource, "findLocationForResource");
      this.rasdToHardwareBuilder = checkNotNull(rasdToHardwareBuilder, "rasdToHardwareBuilder");
   }

   @Override
   public Hardware apply(VCloudExpressVApp from) {
      checkNotNull(from, "VApp");
      try {
         HardwareBuilder builder = rasdToHardwareBuilder.apply(from.getResourceAllocations());
         builder.location(findLocationForResource.apply(checkNotNull(from, "from").getVDC()));
         builder.ids(from.getHref().toASCIIString()).name(from.getName())
               .supportsImage(ImagePredicates.idEquals(from.getHref().toASCIIString()));
         return builder.build();
      } catch (NoSuchElementException e) {
         logger.debug("incomplete data to form vApp %s", from.getHref());
         return null;
      }
   }
}