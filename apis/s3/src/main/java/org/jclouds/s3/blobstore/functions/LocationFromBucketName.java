/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3.blobstore.functions;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.location.predicates.LocationPredicates.idEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.s3.Bucket;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class LocationFromBucketName implements Function<String, Location> {
   private final Supplier<Set<? extends Location>> locations;
   private final Function<String, Optional<String>> bucketToRegion;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   LocationFromBucketName(@Bucket Function<String, Optional<String>> bucketToRegion,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.bucketToRegion = bucketToRegion;
      this.locations = locations;
   }

   public Location apply(String bucket) {
      Set<? extends Location> locations = this.locations.get();
      if (locations.size() == 1)
         return get(locations, 0);
      final Optional<String> region = bucketToRegion.apply(bucket);
      if (region.isPresent()) {
         try {
            return find(locations, idEquals(region.get()));
         } catch (NoSuchElementException e) {
            logger.debug("could not get location for region %s in %s", region, locations);
         }
      } else {
         logger.debug("could not get region for %s", bucket);
      }
      return null;
   }
}
