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

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.location.Region;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

@Singleton
public class DescribeRegionsForConfiguredRegions implements RegionIdToURISupplier {
   private final AvailabilityZoneAndRegionClient client;
   private final Supplier<Set<String>> regions;

   @Inject
   public DescribeRegionsForConfiguredRegions(EC2Client client, @Region Supplier<Set<String>> regions) {
      this.client = client.getAvailabilityZoneAndRegionServices();
      this.regions = regions;
   }

   @Singleton
   @Region
   @Override
   public Map<String, Supplier<URI>> get() {
      Set<String> regionWhiteList = regions.get();
      Map<String, URI> regionToUris = client.describeRegions();
      if (regionWhiteList.size() > 0)
         regionToUris = Maps.filterKeys(regionToUris, Predicates.in(regionWhiteList));
      return Maps.transformValues(regionToUris, Suppliers2.<URI> ofInstanceFunction());
   }
}