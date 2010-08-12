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

package org.jclouds.vcloud.terremark.compute.config.providers;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.functions.OrganizatonsForLocations;
import org.jclouds.vcloud.terremark.compute.functions.ImagesInOrganization;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class VAppTemplatesInOrgs implements Provider<Set<? extends Image>> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final Set<? extends Location> locations;
   private final OrganizatonsForLocations organizatonsForLocations;
   private final ImagesInOrganization imagesInOrganization;

   @Inject
   VAppTemplatesInOrgs(Set<? extends Location> locations, OrganizatonsForLocations organizatonsForLocations,
            ImagesInOrganization imagesInOrganization) {
      this.locations = locations;
      this.organizatonsForLocations = organizatonsForLocations;
      this.imagesInOrganization = imagesInOrganization;
   }

   /**
    * Terremark does not provide vApp templates in the vDC resourceEntity list. Rather, you must
    * query the catalog.
    */
   @Override
   public Set<? extends Image> get() {
      logger.debug(">> providing vAppTemplates");
      return newLinkedHashSet(Iterables.concat(Iterables.transform(organizatonsForLocations.apply(locations),
               imagesInOrganization)));
   }
}