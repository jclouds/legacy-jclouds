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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.domain.RegionAndName;
import org.jclouds.openstack.nova.v1_1.domain.FloatingIP;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.NoSuchElementException;

/**
 * @author Adam Lowe
 */
@Singleton
public class LoadFloatingIpForInstanceOrNull extends CacheLoader<RegionAndName, String> {
   private final NovaClient client;

   @Inject
   public LoadFloatingIpForInstanceOrNull(NovaClient client) {
      this.client = client;
   }

   @Override
   public String load(final RegionAndName key) throws Exception {
      Optional<FloatingIPClient> ipClientOptional = client.getFloatingIPExtensionForRegion(key.getRegion());
      if (ipClientOptional.isPresent()) {
         try {
            return Iterables.find(ipClientOptional.get().listFloatingIPs(),
                  new Predicate<FloatingIP>() {
                     @Override
                     public boolean apply(FloatingIP input) {
                        return key.getName().equals(input.getInstanceId());
                     }

                  }).getIp();
         } catch (NoSuchElementException e) {
            return null;
         }
      }
      return null;
   }

}