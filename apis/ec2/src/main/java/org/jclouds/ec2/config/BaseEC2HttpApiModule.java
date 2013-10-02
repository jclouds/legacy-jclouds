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
package org.jclouds.ec2.config;

import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.config.FormSigningHttpApiModule;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.features.SubnetApi;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.features.WindowsApi;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
import org.jclouds.ec2.features.ElasticBlockStoreApi;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.ec2.features.InstanceApi;
import org.jclouds.ec2.features.KeyPairApi;
import org.jclouds.ec2.features.SecurityGroupApi;
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
import org.jclouds.rest.ConfiguresHttpApi;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the EC2 connection.
 * 
 * @author Adrian Cole (EDIT: Nick Terry nterry@familysearch.org)
 */
@ConfiguresHttpApi
public abstract class BaseEC2HttpApiModule<A extends EC2Api> extends
         FormSigningHttpApiModule<A> {

   protected BaseEC2HttpApiModule(Class<A> api) {
      super(api);
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
