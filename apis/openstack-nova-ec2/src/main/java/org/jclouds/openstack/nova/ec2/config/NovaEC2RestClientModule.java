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
package org.jclouds.openstack.nova.ec2.config;

import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.openstack.nova.ec2.xml.NovaCreateVolumeResponseHandler;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class NovaEC2RestClientModule extends EC2RestClientModule<EC2Client, EC2AsyncClient> {

   public NovaEC2RestClientModule() {
      super(EC2Client.class, EC2AsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(CreateVolumeResponseHandler.class).to(NovaCreateVolumeResponseHandler.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void installLocations() {
      install(new LocationModule());
      bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
      // there is only one region, and its endpoint is the same as the provider
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
   }
}
