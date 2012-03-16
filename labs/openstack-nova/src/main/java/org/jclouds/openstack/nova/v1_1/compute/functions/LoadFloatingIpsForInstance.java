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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.domain.RegionAndName;
import org.jclouds.openstack.nova.v1_1.domain.FloatingIP;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPClient;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Adam Lowe
 */
@Singleton
public class LoadFloatingIpsForInstance extends CacheLoader<RegionAndName, Iterable<String>> {
   private final NovaClient client;
   private final Iterable<String> regions;
   
   @Inject
   public LoadFloatingIpsForInstance(NovaClient client) {
      this.client = client;
      this.regions = client.getConfiguredRegions();
   }

   @Override
   public Iterable<String> load(final RegionAndName key) throws Exception {
      String region = key.getRegion() == null ? regions.iterator().next() : key.getRegion();
      Optional<FloatingIPClient> ipClientOptional = client.getFloatingIPExtensionForRegion(region);
      if (ipClientOptional.isPresent()) {
         return Iterables.transform(Iterables.filter(ipClientOptional.get().listFloatingIPs(),
               new Predicate<FloatingIP>() {
                  @Override
                  public boolean apply(FloatingIP input) {
                     return key.getName().equals(input.getInstanceId());
                  }

               }),
               new Function<FloatingIP, String>() {
                  @Override
                  public String apply(FloatingIP input) {
                     return input.getInstanceId();
                  }
               });
      }
      return ImmutableSet.of();
   }
}