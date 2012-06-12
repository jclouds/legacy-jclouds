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
package org.jclouds.openstack.nova.v2_0;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.nova.v2_0.extensions.AdminActionsClient;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsClient;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateClient;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClient;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsClient;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageClient;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeClient;
import org.jclouds.openstack.nova.v2_0.features.ExtensionClient;
import org.jclouds.openstack.nova.v2_0.features.FlavorClient;
import org.jclouds.openstack.nova.v2_0.features.ImageClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides synchronous access to Nova.
 * <p/>
 * 
 * @see NovaAsyncClient
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface NovaClient {
   /**
    * 
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides synchronous access to Server features.
    */
   @Delegate
   ServerClient getServerClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Flavor features.
    */
   @Delegate
   FlavorClient getFlavorClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Extension features.
    */
   @Delegate
   ExtensionClient getExtensionClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Image features.
    */
   @Delegate
   ImageClient getImageClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Floating IP features.
    */
   @Delegate
   Optional<FloatingIPClient> getFloatingIPExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Security Group features.
    */
   @Delegate
   Optional<SecurityGroupClient> getSecurityGroupExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Key Pair features.
    */
   @Delegate
   Optional<KeyPairClient> getKeyPairExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Host Administration features.
    */
   @Delegate
   Optional<HostAdministrationClient> getHostAdministrationExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Simple Tenant Usage features.
    */
   @Delegate
   Optional<SimpleTenantUsageClient> getSimpleTenantUsageExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Volume features.
    */
   @Delegate
   Optional<VolumeClient> getVolumeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Virtual Interface features.
    */
   @Delegate
   Optional<VirtualInterfaceClient> getVirtualInterfaceExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Server Extra Data features.
    */
   @Delegate
   Optional<ServerWithSecurityGroupsClient> getServerWithSecurityGroupsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Server Admin Actions features.
    */
   @Delegate
   Optional<AdminActionsClient> getAdminActionsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
 
   /**
    * Provides synchronous access to Aggregate features.
    */
   @Delegate
   Optional<HostAggregateClient> getHostAggregateExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Flavor extra specs features.
    */
   @Delegate
   Optional<FlavorExtraSpecsClient> getFlavorExtraSpecsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Quota features.
    */
   @Delegate
   Optional<QuotaClient> getQuotaExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Quota Classes features.
    */
   @Delegate
   Optional<QuotaClassClient> getQuotaClassExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Volume Type features.
    */
   @Delegate
   Optional<VolumeTypeClient> getVolumeTypeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

}
