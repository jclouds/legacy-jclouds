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

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Map;
import java.util.Set;
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
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class EC2RestClientModule<S extends EC2Client, A extends EC2AsyncClient> extends
         WithZonesFormSigningRestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(AMIClient.class, AMIAsyncClient.class)//
            .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
            .put(InstanceClient.class, InstanceAsyncClient.class)//
            .put(KeyPairClient.class, KeyPairAsyncClient.class)//
            .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
            .put(WindowsClient.class, WindowsAsyncClient.class)//
            .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
            .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
            .build();

   public static EC2RestClientModule<EC2Client, EC2AsyncClient> create() {
      return new EC2RestClientModule<EC2Client, EC2AsyncClient>(EC2Client.class, EC2AsyncClient.class, DELEGATE_MAP);
   }

   public EC2RestClientModule(Class<S> sync, Class<A> async, Map<Class<?>, Class<?>> delegateMap) {
      super(sync, async, delegateMap);
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
      private final Injector injector;

      @Inject
      public RegionIdsToURI(EC2Client client, Injector injector) {
         this.client = client.getAvailabilityZoneAndRegionServices();
         this.injector = injector;
      }

      @Singleton
      @Region
      @Override
      public Map<String, URI> get() {
         try {
            String regionString = injector.getInstance(Key.get(String.class, Names.named(PROPERTY_REGIONS)));
            Set<String> regions = ImmutableSet.copyOf(Splitter.on(',').split(regionString));
            if (regions.size() > 0)
               return Maps.filterKeys(client.describeRegions(), Predicates.in(regions));
         } catch (ConfigurationException e) {
            // this happens if regions property isn't set
            // services not run by AWS may not have regions, so this is ok.
         }
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
