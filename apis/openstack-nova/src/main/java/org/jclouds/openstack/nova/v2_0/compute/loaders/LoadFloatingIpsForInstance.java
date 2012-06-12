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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Each zone may or may not have the floating ip function present. In order to safely proceed, we
 * must allow the user to determine if a zone has floating ip services before attempting to use
 * them.
 * 
 * @author Adam Lowe
 */
@Singleton
public class LoadFloatingIpsForInstance extends CacheLoader<ZoneAndId, Iterable<String>> {
   private final NovaClient client;

   @Inject
   public LoadFloatingIpsForInstance(NovaClient client) {
      this.client = client;
   }

   @Override
   public Iterable<String> load(final ZoneAndId key) throws Exception {
      String zone = key.getZone();
      Optional<FloatingIPClient> ipClientOptional = client.getFloatingIPExtensionForZone(zone);
      if (ipClientOptional.isPresent()) {
         return Iterables.transform(Iterables.filter(ipClientOptional.get().listFloatingIPs(),
                  new Predicate<FloatingIP>() {
                     @Override
                     public boolean apply(FloatingIP input) {
                        return key.getId().equals(input.getInstanceId());
                     }
                  }), new Function<FloatingIP, String>() {
            @Override
            public String apply(FloatingIP input) {
               return input.getIp();
            }
         });
      }
      return ImmutableSet.of();
   }
}