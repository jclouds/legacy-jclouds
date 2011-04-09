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
package org.jclouds.s3.blobstore.functions;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class LocationFromBucketLocation implements Function<BucketMetadata, Location> {
   private final Location onlyLocation;
   private final Supplier<Set<? extends Location>> locations;
   private final S3Client client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   LocationFromBucketLocation(S3Client client, @Memoized Supplier<Set<? extends Location>> locations) {
      this.client = client;
      this.onlyLocation = locations.get().size() == 1 ? Iterables.get(locations.get(), 0) : null;
      this.locations = locations;
   }

   public Location apply(BucketMetadata from) {
      if (onlyLocation != null)
         return onlyLocation;
      try {
         Set<? extends Location> locations = this.locations.get();
         final String region = client.getBucketLocation(from.getName());
         assert region != null : String.format("could not get region for %s", from.getName());
         if (region != null) {
            try {
               return Iterables.find(locations, new Predicate<Location>() {

                  @Override
                  public boolean apply(Location input) {
                     return input.getId().equalsIgnoreCase(region.toString());
                  }

               });
            } catch (NoSuchElementException e) {
               logger.error("could not get location for region %s in %s", region, locations);
            }
         } else {
            logger.error("could not get region for %s", from.getName());
         }
      } catch (ContainerNotFoundException e) {
         logger.error(e, "could not get region for %s, as service suggests the bucket doesn't exist", from.getName());
      }
      return null;
   }
}
