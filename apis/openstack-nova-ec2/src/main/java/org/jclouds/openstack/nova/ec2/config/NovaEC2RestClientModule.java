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

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.config.EC2RestClientModule;
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
import org.jclouds.ec2.services.SecurityGroupAsyncClient;
import org.jclouds.ec2.services.SecurityGroupClient;
import org.jclouds.ec2.services.WindowsAsyncClient;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.openstack.nova.ec2.NovaEC2AsyncClient;
import org.jclouds.openstack.nova.ec2.NovaEC2Client;
import org.jclouds.openstack.nova.ec2.services.NovaEC2KeyPairAsyncClient;
import org.jclouds.openstack.nova.ec2.services.NovaEC2KeyPairClient;
import org.jclouds.openstack.nova.ec2.xml.NovaCreateVolumeResponseHandler;
import org.jclouds.openstack.nova.ec2.xml.NovaDescribeImagesResponseHandler;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 */
@ConfiguresRestClient
public class NovaEC2RestClientModule extends EC2RestClientModule<NovaEC2Client, NovaEC2AsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(AMIClient.class, AMIAsyncClient.class)//
         .put(ElasticIPAddressClient.class, ElasticIPAddressAsyncClient.class)//
         .put(InstanceClient.class, InstanceAsyncClient.class)//
         .put(NovaEC2KeyPairClient.class, NovaEC2KeyPairAsyncClient.class)//
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
         .put(WindowsClient.class, WindowsAsyncClient.class)//
         .put(AvailabilityZoneAndRegionClient.class, AvailabilityZoneAndRegionAsyncClient.class)//
         .put(ElasticBlockStoreClient.class, ElasticBlockStoreAsyncClient.class)//
         .build();

   public NovaEC2RestClientModule() {
      super(typeToken(NovaEC2Client.class), typeToken(NovaEC2AsyncClient.class), DELEGATE_MAP);
   }
   
   @Override
   protected void configure() {
      super.configure();
      bind(CreateVolumeResponseHandler.class).to(NovaCreateVolumeResponseHandler.class).in(Scopes.SINGLETON);
      bind(DescribeImagesResponseHandler.class).to(NovaDescribeImagesResponseHandler.class);
   }

   @Override
   protected void installLocations() {
      install(new LocationModule());
      bind(RegionIdToZoneIdsSupplier.class).to(DescribeAvailabilityZonesInRegion.class).in(Scopes.SINGLETON);
      // there is only one region, and its endpoint is the same as the provider
      bind(ZoneIdsSupplier.class).to(ZoneIdsFromRegionIdToZoneIdsValues.class).in(Scopes.SINGLETON);
   }

   @Singleton
   @Provides
   EC2Client provide(NovaEC2Client in) {
      return in;
   }

   @Singleton
   @Provides
   EC2AsyncClient provide(NovaEC2AsyncClient in) {
      return in;
   }
}
