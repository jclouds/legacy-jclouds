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
package org.jclouds.openstack.nova.v1_1;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Region;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v1_1.features.ExtensionClient;
import org.jclouds.openstack.nova.v1_1.features.FlavorClient;
import org.jclouds.openstack.nova.v1_1.features.ImageClient;
import org.jclouds.openstack.nova.v1_1.features.ServerClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides synchronous access to Nova.
 * <p/>
 * 
 * @see NovaAsyncClient
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface NovaClient {
   /**
    * 
    * @return the region codes configured
    */
   @Provides
   @Region
   Set<String> getConfiguredRegions();
   
   /**
    * Provides synchronous access to Server features.
    */
   @Delegate
   ServerClient getServerClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Flavor features.
    */
   @Delegate
   FlavorClient getFlavorClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Extension features.
    */
   @Delegate
   ExtensionClient getExtensionClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);
   
   /**
    * Provides synchronous access to Image features.
    */
   @Delegate
   ImageClient getImageClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Floating IP features.
    */
   @Delegate
   Optional<FloatingIPClient> getFloatingIPExtensionForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Security Group features.
    */
   @Delegate
   SecurityGroupClient getSecurityGroupClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * Provides synchronous access to Key Pair features.
    */
   @Delegate
   KeyPairClient getKeyPairClientForRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

}
