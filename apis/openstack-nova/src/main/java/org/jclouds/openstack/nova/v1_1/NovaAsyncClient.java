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

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.HostAdministrationAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.SimpleTenantUsageAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.VolumeAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.ExtensionAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.FlavorAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.ImageAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.ServerAsyncClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to Nova via their REST API.
 * <p/>
 * 
 * @see NovaClient
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/"
 *      />
 * @author Adrian Cole
 */
public interface NovaAsyncClient {

   /**
    * 
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides asynchronous access to Server features.
    */
   @Delegate
   ServerAsyncClient getServerClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Flavor features.
    */
   @Delegate
   FlavorAsyncClient getFlavorClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Extension features.
    */
   @Delegate
   ExtensionAsyncClient getExtensionClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Image features.
    */
   @Delegate
   ImageAsyncClient getImageClientForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Floating IP features.
    */
   @Delegate
   Optional<FloatingIPAsyncClient> getFloatingIPExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Security Group features.
    */
   @Delegate
   Optional<SecurityGroupAsyncClient> getSecurityGroupExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Key Pair features.
    */
   @Delegate
   Optional<KeyPairAsyncClient> getKeyPairExtensionForZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Host Administration features.
    */
   @Delegate
   Optional<HostAdministrationAsyncClient> getHostAdministrationExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Simple Tenant Usage features.
    */
   @Delegate
   Optional<SimpleTenantUsageAsyncClient> getSimpleTenantUsageExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Volume features.
    */
   @Delegate
   Optional<VolumeAsyncClient> getVolumeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
