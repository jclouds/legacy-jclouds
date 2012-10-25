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
package org.jclouds.ec2.config;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.config.WithZonesFormSigningRestClientModule;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.EC2AsyncApi;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.features.TagAsyncApi;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
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
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.suppliers.DescribeRegionsForRegionURIs;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole (EDIT: Nick Terry nterry@familysearch.org)
 */
@ConfiguresRestClient
// EC2Api not EC2Client so that this can be used for new apps that only depend on EC2Api
public class EC2RestClientModule<S extends EC2Api, A extends EC2AsyncApi> extends
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
                        .put(WindowsApi.class, WindowsAsyncApi.class)//
                        .put(TagApi.class, TagAsyncApi.class)//
                        .build();
   
   @SuppressWarnings("unchecked")
   public EC2RestClientModule() {
      // retaining top-level type of EC2Client vs EC2Api until we migrate all functionality up
      super(TypeToken.class.cast(TypeToken.of(EC2Client.class)), TypeToken.class.cast(TypeToken.of(EC2AsyncClient.class)), DELEGATE_MAP);
   }

   protected EC2RestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   

   /**
    * so that we can make bindings to {@link EC2Api directly} until we switch
    * off {@link @EC2Client}
    */
   @Singleton
   @Provides
   EC2Api provideEC2Api(EC2Client in) {
      return in;
   }
   
   /**
    * so that we can make bindings to {@link EC2AsyncApi directly} until we switch
    * off {@link @EC2AsyncClient}
    */
   @Singleton
   @Provides
   EC2AsyncApi provideEC2Api(EC2AsyncClient in) {
      return in;
   }
   
   @Override
   protected void installLocations() {
      install(new LocationModule());
      bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
      bind(RegionIdToURISupplier.class).to(DescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
      bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
      bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
   }
}
