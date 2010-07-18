/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.config;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.Region;
import org.jclouds.aws.config.AWSFormSigningRestClientModule;
import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.aws.ec2.services.AMIAsyncClient;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.aws.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreAsyncClient;
import org.jclouds.aws.ec2.services.ElasticBlockStoreClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressAsyncClient;
import org.jclouds.aws.ec2.services.ElasticIPAddressClient;
import org.jclouds.aws.ec2.services.InstanceAsyncClient;
import org.jclouds.aws.ec2.services.InstanceClient;
import org.jclouds.aws.ec2.services.KeyPairAsyncClient;
import org.jclouds.aws.ec2.services.KeyPairClient;
import org.jclouds.aws.ec2.services.MonitoringAsyncClient;
import org.jclouds.aws.ec2.services.MonitoringClient;
import org.jclouds.aws.ec2.services.PlacementGroupAsyncClient;
import org.jclouds.aws.ec2.services.PlacementGroupClient;
import org.jclouds.aws.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.aws.ec2.services.SecurityGroupClient;
import org.jclouds.aws.ec2.services.WindowsAsyncClient;
import org.jclouds.aws.ec2.services.WindowsClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class EC2RestClientModule extends AWSFormSigningRestClientModule<EC2Client, EC2AsyncClient> {

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

   private RuntimeException regionException = null;

   @Override
   protected Map<String, URI> provideRegions(Injector injector) {
      // http://code.google.com/p/google-guice/issues/detail?id=483
      // guice doesn't remember when singleton providers throw exceptions.
      // in this case, if describeRegions fails, it is called again for
      // each provider method that depends on it. To short-circuit this,
      // we remember the last exception trusting that guice is single-threaded
      if (regionException != null)
         throw regionException;
      EC2Client client = injector.getInstance(EC2Client.class);
      try {
         return client.getAvailabilityZoneAndRegionServices().describeRegions();
      } catch (RuntimeException e) {
         this.regionException = e;
         throw e;
      }
   }

   @Provides
   @Singleton
   protected Map<String, String> provideAvailabilityZoneToRegions(EC2Client client, @Region Map<String, URI> regions) {
      Map<String, String> map = Maps.newHashMap();
      for (String region : regions.keySet()) {
         for (AvailabilityZoneInfo zoneInfo : client.getAvailabilityZoneAndRegionServices()
                  .describeAvailabilityZonesInRegion(region)) {
            map.put(zoneInfo.getZone(), region);
         }
      }
      return map;
   }

}