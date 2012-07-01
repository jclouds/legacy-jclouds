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
package org.jclouds.elb.loadbalancer.functions;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.elb.domain.CrappyLoadBalancer;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class LoadBalancerToLoadBalancerMetadata implements Function<CrappyLoadBalancer, LoadBalancerMetadata> {
   @Resource
   protected static Logger logger = Logger.NULL;
   
   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Location> defaultLocationSupplier;

   @Inject
   public LoadBalancerToLoadBalancerMetadata(Supplier<Location> defaultLocationSupplier,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
      this.defaultLocationSupplier = defaultLocationSupplier;
   }

   @Override
   public LoadBalancerMetadata apply(CrappyLoadBalancer input) {

      Location location = input.getRegion() != null ? findLocationWithId(input.getRegion()) : defaultLocationSupplier
               .get();

      String id = input.getRegion() != null ? input.getRegion() + "/" + input.getName() : input.getName();
      // TODO Builder
      return new LoadBalancerMetadataImpl(LoadBalancerType.LB, input.getName(), input.getName(), id, location, null,
               ImmutableMap.<String, String> of(), ImmutableSet.of(input.getDnsName()));
   }

   private Location findLocationWithId(final String locationId) {
      if (locationId == null)
         return null;
      try {
         Location location = Iterables.find(locations.get(), new Predicate<Location>() {

            @Override
            public boolean apply(Location input) {
               return input.getId().equals(locationId);
            }

         });
         return location;

      } catch (NoSuchElementException e) {
         logger.debug("couldn't match instance location %s in: %s", locationId, locations.get());
         return null;
      }
   }

}