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

package org.jclouds.aws.config;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.aws.ec2.xml.DescribeAvailabilityZonesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeRegionsResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
public class ProvidersViaAPI {

   @Singleton
   public static class RegionIdsToURI implements javax.inject.Provider<Map<String, URI>> {
      private final ZoneAndRegionClient client;
      private URI endpoint;

      @Inject
      public RegionIdsToURI(@Zone URI endpoint, ZoneAndRegionClient client) {
         this.client = client;
         this.endpoint = endpoint;
      }

      @Singleton
      @Region
      @Override
      public Map<String, URI> get() {
         return client.describeRegions(endpoint);
      }

   }

   @Singleton
   public static class RegionIdToZoneId implements javax.inject.Provider<Map<String, String>> {
      private final ZoneAndRegionClient client;
      private final Set<String> regions;
      private final RegionIdsToURI regionIdsToURI;

      @Inject
      public RegionIdToZoneId(RegionIdsToURI regionIdsToURI, ZoneAndRegionClient client, @Region Set<String> regions) {
         this.client = client;
         this.regions = regions;
         this.regionIdsToURI = regionIdsToURI;
      }

      @Singleton
      @Zone
      @Override
      public Map<String, String> get() {

         Builder<String, String> map = ImmutableMap.<String, String> builder();
         for (Entry<String, URI> region : regionIdsToURI.get().entrySet()) {
            if (regions.contains(region.getKey()))
               for (AvailabilityZoneInfo zoneInfo : client.describeAvailabilityZonesInRegion(region.getValue())) {
                  map.put(zoneInfo.getZone(), region.getKey());
               }
         }
         return map.build();
      }

   }

   static class ProvidesZoneAndRegionClientModule extends AbstractModule {
      @Provides
      @Singleton
      @Zone
      URI provideURI(@Named(AWSConstants.PROPERTY_ZONECLIENT_ENDPOINT) String endpoint) {
         return URI.create(endpoint);
      }

      @Provides
      @Singleton
      ZoneAndRegionClient provideZoneAndRegionClient(AsyncClientFactory factory) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
         return SyncProxy.proxy(ZoneAndRegionClient.class,
               new SyncProxy(ZoneAndRegionClient.class, factory.create(ZoneAndRegionAsyncClient.class),
                     new ConcurrentHashMap<ClassMethodArgs, Object>(), ImmutableMap.<Class<?>, Class<?>> of()));
      }

      @Override
      protected void configure() {

      }

   }

   @RequestFilters(FormSigner.class)
   @FormParams(keys = "Version", values = "2010-06-15")
   @VirtualHost
   static interface ZoneAndRegionAsyncClient {

      @POST
      @Path("/")
      @FormParams(keys = "Action", values = "DescribeAvailabilityZones")
      @XMLResponseParser(DescribeAvailabilityZonesResponseHandler.class)
      @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
      ListenableFuture<? extends Set<AvailabilityZoneInfo>> describeAvailabilityZonesInRegion(@EndpointParam URI region);

      @POST
      @Path("/")
      @FormParams(keys = "Action", values = "DescribeRegions")
      @XMLResponseParser(DescribeRegionsResponseHandler.class)
      ListenableFuture<? extends Map<String, URI>> describeRegions(@EndpointParam URI endpoint);

   }

   @Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
   static interface ZoneAndRegionClient {

      Set<AvailabilityZoneInfo> describeAvailabilityZonesInRegion(URI region);

      Map<String, URI> describeRegions(URI endpoint);
   }

}