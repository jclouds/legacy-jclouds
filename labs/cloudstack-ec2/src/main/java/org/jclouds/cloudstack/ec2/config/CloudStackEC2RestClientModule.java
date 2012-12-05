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
package org.jclouds.cloudstack.ec2.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import org.jclouds.cloudstack.ec2.CloudStackEC2AsyncClient;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.cloudstack.ec2.services.CloudStackAMIAsyncClient;
import org.jclouds.cloudstack.ec2.services.CloudStackAMIClient;
import org.jclouds.cloudstack.ec2.services.CloudStackEC2InstanceAsyncClient;
import org.jclouds.cloudstack.ec2.services.CloudStackEC2InstanceClient;
import org.jclouds.cloudstack.ec2.suppliers.CloudStackEC2DescribeRegionsForRegionURIs;
import org.jclouds.cloudstack.ec2.xml.*;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.WindowsAsyncApi;
import org.jclouds.ec2.services.*;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.xml.*;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.*;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdToURIFromJoinOnRegionIdToURI;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.rest.ConfiguresRestClient;

import javax.inject.Singleton;
import java.util.Map;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudStackEC2RestClientModule extends EC2RestClientModule<CloudStackEC2Client, CloudStackEC2AsyncClient> {


   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(CloudStackAMIClient.class, CloudStackAMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(CloudStackEC2InstanceClient.class, CloudStackEC2InstanceAsyncClient.class)//
         .put(KeyPairClient.class, KeyPairAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(WindowsClient.class, WindowsAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .put(WindowsApi.class, WindowsAsyncApi.class)//
         .build();

   public CloudStackEC2RestClientModule() {
      super(TypeToken.of(CloudStackEC2Client.class), TypeToken.of(CloudStackEC2AsyncClient.class), DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(DescribeImagesResponseHandler.class).to(CloudStackEC2DescribeImagesResponseHandler.class);
      bind(RunInstancesResponseHandler.class).to(CloudStackEC2RunInstancesResponseHandler.class);
      bind(DescribeInstancesResponseHandler.class).to(CloudStackEC2DescribeInstancesResponseHandler.class);
      bind(DescribeVolumesResponseHandler.class).to(CloudStackEC2DescribeVolumesResponseHandler.class);
      bind(CreateVolumeResponseHandler.class).to(CloudStackEC2CreateVolumeResponseHandler.class);
   }

    @Override
    protected void installLocations() {
        install(new LocationModule());
        bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
        bind(RegionIdToURISupplier.class).to(CloudStackEC2DescribeRegionsForRegionURIs.class).in(Scopes.SINGLETON);
        bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
        bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class).in(Scopes.SINGLETON);
        bind(ZoneIdToURISupplier.class).to(ZoneIdToURIFromJoinOnRegionIdToURI.class).in(Scopes.SINGLETON);
    }

    @Override
    protected void bindRetryHandlers() {
        bind(HttpRetryHandler.class).annotatedWith(ClientError.class).toInstance(HttpRetryHandler.NEVER_RETRY);
        bind(IOExceptionRetryHandler.class).toInstance(IOExceptionRetryHandler.NEVER_RETRY);
    }

   @Singleton
   @Provides
   EC2Client provide(CloudStackEC2Client in) {
      return in;
   }

   @Singleton
   @Provides
   EC2AsyncClient provide(CloudStackEC2AsyncClient in) {
      return in;
   }

}
