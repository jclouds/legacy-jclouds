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
package org.jclouds.rackspace.cloudloadbalancers.v1.loadbalancer.functions;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.loadbalancer.domain.LoadBalancerMetadata;
import org.jclouds.loadbalancer.domain.LoadBalancerType;
import org.jclouds.loadbalancer.domain.internal.LoadBalancerMetadataImpl;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIPWithId;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class LoadBalancerToLoadBalancerMetadata implements Function<LoadBalancer, LoadBalancerMetadata> {
   protected final Supplier<Set<? extends Location>> locations;
   protected final Supplier<Location> defaultLocationSupplier;

   @Inject
   public LoadBalancerToLoadBalancerMetadata(Supplier<Location> defaultLocationSupplier,
            @Memoized Supplier<Set<? extends Location>> locations) {
      this.locations = locations;
      this.defaultLocationSupplier = defaultLocationSupplier;
   }

   @Override
   public LoadBalancerMetadata apply(LoadBalancer input) {

      Location location = Iterables.find(locations.get(), LocationPredicates.idEquals(input.getRegion()));

      String id = input.getRegion() + "/" + input.getId();
      // TODO Builder
      return new LoadBalancerMetadataImpl(LoadBalancerType.LB, String.valueOf(input.getId()), input.getName(), id, location, null,
               ImmutableMap.<String, String> of(), Iterables.transform(input.getVirtualIPs(),
                        new Function<VirtualIPWithId, String>() {

                           @Override
                           public String apply(VirtualIPWithId arg0) {
                              return arg0.getAddress();
                           }

                        }));
   }
}
