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

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.compute.domain.VCloudLocation;
import org.jclouds.vcloud.domain.NamedResource;

/**
 * @author Adrian Cole
 */
@Singleton
public class FindLocationForResource {

   @Resource
   protected Logger logger = Logger.NULL;

   final Provider<Set<? extends Location>> locations;
   final Location defaultLocation;

   @Inject
   public FindLocationForResource(Provider<Set<? extends Location>> locations, Location defaultLocation) {
      this.locations = locations;
      this.defaultLocation = defaultLocation;
   }

   /**
    * searches for a location associated with this resource.
    * 
    * @throws NoSuchElementException
    *            if not found
    */
   public Location apply(NamedResource resource) {
      for (Location input : locations.get()) {
         do {
            // The "name" isn't always present, ex inside a vApp we have a rel
            // link that only includes href and type.
            if (VCloudLocation.class.cast(input).getResource().getLocation().equals(resource.getLocation()))
               return input;
            input = input.getParent();
         } while (input.getParent() != null);
      }
      throw new NoSuchElementException(String.format("resource: %s not found in locations: %s", resource, locations
            .get()));
   }
}