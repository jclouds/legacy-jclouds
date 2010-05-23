/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.NamedResource;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class FindLocationForResourceInVDC {

   @Resource
   protected Logger logger = Logger.NULL;

   final Provider<Set<? extends Location>> locations;
   final Location defaultLocation;

   @Inject
   public FindLocationForResourceInVDC(Provider<Set<? extends Location>> locations,
            Location defaultLocation) {
      this.locations = locations;
      this.defaultLocation = defaultLocation;
   }

   public Location apply(NamedResource resource, final String vdcId) {
      Location location = null;
      try {
         location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(vdcId);
            }

         });
      } catch (NoSuchElementException e) {
         logger.error("unknown vdc %s for %s %s; not in %s", vdcId, resource.getType(), resource
                  .getId(), locations);
         location = new LocationImpl(LocationScope.ZONE, vdcId, vdcId, defaultLocation.getParent());
      }
      return location;
   }
}