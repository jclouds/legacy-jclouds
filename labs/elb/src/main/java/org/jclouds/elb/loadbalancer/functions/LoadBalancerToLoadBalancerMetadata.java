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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.elb.domain.regionscoped.LoadBalancerInRegion;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;

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
public class LoadBalancerToLoadBalancerMetadata implements Function<LoadBalancerInRegion, LoadBalancerMetadata> {

   protected final Supplier<Set<? extends Location>> locations;

   @Inject
   public LoadBalancerToLoadBalancerMetadata(@Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public LoadBalancerMetadata apply(LoadBalancerInRegion input) {

      Location location = findLocationWithId(input.getRegion());

      return new LoadBalancerMetadataImpl(LoadBalancerType.LB, input.getName(), input.getName(), input.slashEncode(),
               location, null, ImmutableMap.<String, String> of(),
               ImmutableSet.of(input.getLoadBalancer().getDnsName()));
   }

   private Location findLocationWithId(final String locationId) {
      return Iterables.find(locations.get(), new Predicate<Location>() {

         @Override
         public boolean apply(Location input) {
            return input.getId().equals(locationId);
         }

      });
   }

}
