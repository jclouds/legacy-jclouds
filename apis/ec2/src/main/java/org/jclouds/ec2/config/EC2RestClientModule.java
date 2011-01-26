/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.ec2.config;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.config.WithZonesFormSigningRestClientModule;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.services.AMIAsyncClient;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.jclouds.ec2.services.InstanceAsyncClient;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.ec2.services.KeyPairAsyncClient;
import org.jclouds.ec2.services.KeyPairClient;
import org.jclouds.ec2.services.MonitoringAsyncClient;
import org.jclouds.ec2.services.MonitoringClient;
import org.jclouds.ec2.services.PlacementGroupAsyncClient;
import org.jclouds.ec2.services.PlacementGroupClient;
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class EC2RestClientModule extends WithZonesFormSigningRestClientModule<EC2Client, EC2AsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(AMIClient.class, AMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(InstanceClient.class, InstanceAsyncClient.class)//
         .put(KeyPairClient.class, KeyPairAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(PlacementGroupClient.class, PlacementGroupAsyncClient.class)//
         .put(MonitoringClient.class, MonitoringAsyncClient.class)//
         .put(WindowsClient.class, WindowsAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .build();

   public EC2RestClientModule() {
      super(EC2Client.class, EC2AsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void bindRegionsToProvider() {
      bindRegionsToProvider(RegionIdsToURI.class);
   }

   @Override
   protected void bindZonesToProvider() {
      bindZonesToProvider(RegionIdToZoneId.class);
   }

   @Singleton
   public static class RegionIdsToURI implements javax.inject.Provider<Map<String, URI>> {
      private final AvailabilityZoneAndRegionClient client;

      @Inject
      public RegionIdsToURI(EC2Client client) {
         this.client = client.getAvailabilityZoneAndRegionServices();
      }

      @Singleton
      @Region
      @Override
      public Map<String, URI> get() {
         return client.describeRegions();
      }

   }
   @Singleton
   public static class RegionIdToZoneId implements javax.inject.Provider<Map<String, String>> {
      private final AvailabilityZoneAndRegionClient client;
      private final Map<String, URI> regions;

      @Inject
      public RegionIdToZoneId(EC2Client client, @Region Map<String, URI> regions) {
         this.client = client.getAvailabilityZoneAndRegionServices();
         this.regions = regions;
      }

      @Singleton
      @Zone
      @Override
      public Map<String, String> get() {
         Builder<String, String> map = ImmutableMap.<String, String> builder();
         for (Entry<String, URI> region : regions.entrySet()) {
            for (AvailabilityZoneInfo zoneInfo : client.describeAvailabilityZonesInRegion(region.getKey())) {
               map.put(zoneInfo.getZone(), region.getKey());
            }
         }
         return map.build();
      }

   }

}
