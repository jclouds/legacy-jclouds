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
package org.jclouds.openstack.nova.v2_0.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.cache.LoadingCache;

/**
 * A function for removing and deallocating an ip address from a node
 * 
 * @author Adrian Cole
 */
public class RemoveFloatingIpFromNodeAndDeallocate implements Function<ZoneAndId, ZoneAndId> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final NovaClient novaClient;
   private final LoadingCache<ZoneAndId, Iterable<String>> floatingIpCache;

   @Inject
   public RemoveFloatingIpFromNodeAndDeallocate(NovaClient novaClient,
            @Named("FLOATINGIP") LoadingCache<ZoneAndId, Iterable<String>> floatingIpCache) {
      this.novaClient = checkNotNull(novaClient, "novaClient");
      this.floatingIpCache = checkNotNull(floatingIpCache, "floatingIpCache");
   }

   @Override
   public ZoneAndId apply(ZoneAndId id) {
      FloatingIPClient floatingIpClient = novaClient.getFloatingIPExtensionForZone(id.getZone()).get();
      for (String ip : floatingIpCache.getUnchecked(id)) {
         logger.debug(">> removing floatingIp(%s) from node(%s)", ip, id);
         floatingIpClient.removeFloatingIPFromServer(ip, id.getId());
         logger.debug(">> deallocating floatingIp(%s)", ip);
         floatingIpClient.deallocate(ip);
      }
      floatingIpCache.invalidate(id);
      return id;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("RemoveFloatingIpFromNodeAndDeallocate").toString();
   }
}