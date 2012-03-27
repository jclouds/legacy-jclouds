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
package org.jclouds.ec2.suppliers;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.http.HttpResponseException;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.logging.Logger;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Singleton
public class DescribeAvailabilityZonesInRegion implements RegionIdToZoneIdsSupplier {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AvailabilityZoneAndRegionClient client;
   private final Supplier<Set<String>> regions;

   @Inject
   public DescribeAvailabilityZonesInRegion(EC2Client client, @Region Supplier<Set<String>> regions) {
      this.client = client.getAvailabilityZoneAndRegionServices();
      this.regions = regions;
   }

   @Override
   public Map<String, Supplier<Set<String>>> get() {
      Builder<String, Set<String>> map = ImmutableMap.builder();
      HttpResponseException exception = null;
      // TODO: this should be parallel
      for (String region : regions.get()) {
         try {
            ImmutableSet<String> zones = ImmutableSet.copyOf(Iterables.transform(client
                     .describeAvailabilityZonesInRegion(region), new Function<AvailabilityZoneInfo, String>() {

               @Override
               public String apply(AvailabilityZoneInfo arg0) {
                  return arg0.getZone();
               }

            }));
            if (zones.size() > 0)
               map.put(region, zones);
         } catch (HttpResponseException e) {
            // TODO: this should be in retry handler, not here.
            if (e.getMessage().contains("Unable to tunnel through proxy")) {
               exception = e;
               logger.error(e, "Could not describe availability zones in Region: %s", region);
            } else {
               throw e;
            }
         }
      }
      ImmutableMap<String, Set<String>> result = map.build();
      if (result.isEmpty() && exception != null) {
         throw exception;
      }
      return Maps.transformValues(result, Suppliers2.<Set<String>> ofInstanceFunction());
   }

}