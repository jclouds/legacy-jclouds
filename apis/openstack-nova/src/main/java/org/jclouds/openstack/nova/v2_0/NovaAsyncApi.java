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
package org.jclouds.openstack.nova.v2_0;

import java.io.Closeable;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerAdminAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to Nova via their REST API.
 * <p/>
 * 
 * @see NovaApi
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/"
 *      />
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(NovaApi.class)} as
 *             {@link NovaAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface NovaAsyncApi extends Closeable {

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
   ServerAsyncApi getServerApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Flavor features.
    */
   @Delegate
   FlavorAsyncApi getFlavorApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Extension features.
    */
   @Delegate
   ExtensionAsyncApi getExtensionApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Image features.
    */
   @Delegate
   ImageAsyncApi getImageApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Floating IP features.
    */
   @Delegate
   Optional<? extends FloatingIPAsyncApi> getFloatingIPExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Security Group features.
    */
   @Delegate
   Optional<? extends SecurityGroupAsyncApi> getSecurityGroupExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Key Pair features.
    */
   @Delegate
   Optional<? extends KeyPairAsyncApi> getKeyPairExtensionForZone(
            @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Host Administration features.
    */
   @Delegate
   Optional<? extends HostAdministrationAsyncApi> getHostAdministrationExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Simple Tenant Usage features.
    */
   @Delegate
   Optional<? extends SimpleTenantUsageAsyncApi> getSimpleTenantUsageExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Virtual Interface features.
    */
   @Delegate
   Optional<? extends VirtualInterfaceAsyncApi> getVirtualInterfaceExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);


   /**
    * Provides asynchronous access to Server Extra Data features.
    */
   @Delegate
   Optional<? extends ServerWithSecurityGroupsAsyncApi> getServerWithSecurityGroupsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Server Admin Actions features.
    */
   @Delegate
   Optional<? extends ServerAdminAsyncApi> getServerAdminExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to HostAggregate features.
    */
   @Delegate
   Optional<? extends HostAggregateAsyncApi> getHostAggregateExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Flavor extra specs features.
    */
   @Delegate
   Optional<? extends FlavorExtraSpecsAsyncApi> getFlavorExtraSpecsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Quota features.
    */
   @Delegate
   Optional<? extends QuotaAsyncApi> getQuotaExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Quota Classes features.
    */
   @Delegate
   Optional<? extends QuotaClassAsyncApi> getQuotaClassExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Volume features.
    */
   @Delegate
   Optional<? extends VolumeAsyncApi> getVolumeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Volume features.
    */
   @Delegate
   Optional<? extends VolumeAttachmentAsyncApi> getVolumeAttachmentExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides asynchronous access to Volume Type features.
    */
   @Delegate
   Optional<? extends VolumeTypeAsyncApi> getVolumeTypeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

}
