/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.ec2.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.config.BaseEC2HttpApiModule;
import org.jclouds.ec2.features.SubnetApi;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.jclouds.ec2.features.ElasticBlockStoreApi;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.ec2.features.InstanceApi;
import org.jclouds.ec2.features.SecurityGroupApi;
import org.jclouds.ec2.suppliers.DescribeAvailabilityZonesInRegion;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.location.config.LocationModule;
import org.jclouds.location.suppliers.RegionIdToZoneIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.derived.ZoneIdsFromRegionIdToZoneIdsValues;
import org.jclouds.openstack.nova.ec2.NovaEC2Api;
import org.jclouds.openstack.nova.ec2.features.NovaEC2KeyPairApi;
import org.jclouds.openstack.nova.ec2.xml.NovaCreateVolumeResponseHandler;
import org.jclouds.openstack.nova.ec2.xml.NovaDescribeImagesResponseHandler;
import org.jclouds.rest.ConfiguresHttpApi;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 */
@ConfiguresHttpApi
public class NovaEC2HttpApiModule extends BaseEC2HttpApiModule<NovaEC2Api> {

   public NovaEC2HttpApiModule() {
      super(NovaEC2Api.class);
   }
   
   @Override
   protected void configure() {
      bind(EC2Api.class).to(NovaEC2Api.class);
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

}
