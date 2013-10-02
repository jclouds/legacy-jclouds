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
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerAdminApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageApi;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.common.base.Optional;
import com.google.inject.Provides;

/**
 * Provides synchronous access to Nova.
 * <p/>
 * 
 * @see NovaAsyncApi
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/"
 *      />
 * @author Adrian Cole
 */
public interface NovaApi extends Closeable {
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
   ServerApi getServerApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Flavor features.
    */
   @Delegate
   FlavorApi getFlavorApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Image features.
    */
   @Delegate
   ImageApi getImageApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Floating IP features.
    */
   @Delegate
   Optional<? extends FloatingIPApi> getFloatingIPExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Security Group features.
    */
   @Delegate
   Optional<? extends SecurityGroupApi> getSecurityGroupExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Key Pair features.
    */
   @Delegate
   Optional<? extends KeyPairApi> getKeyPairExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Host Administration features.
    */
   @Delegate
   Optional<? extends HostAdministrationApi> getHostAdministrationExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Simple Tenant Usage features.
    */
   @Delegate
   Optional<? extends SimpleTenantUsageApi> getSimpleTenantUsageExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Virtual Interface features.
    */
   @Delegate
   Optional<? extends VirtualInterfaceApi> getVirtualInterfaceExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Server Extra Data features.
    */
   @Delegate
   Optional<? extends ServerWithSecurityGroupsApi> getServerWithSecurityGroupsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Server Admin Actions features.
    */
   @Delegate
   Optional<? extends ServerAdminApi> getServerAdminExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
 
   /**
    * Provides synchronous access to Aggregate features.
    */
   @Delegate
   Optional<? extends HostAggregateApi> getHostAggregateExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Flavor extra specs features.
    */
   @Delegate
   Optional<? extends FlavorExtraSpecsApi> getFlavorExtraSpecsExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Quota features.
    */
   @Delegate
   Optional<? extends QuotaApi> getQuotaExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Quota Classes features.
    */
   @Delegate
   Optional<? extends QuotaClassApi> getQuotaClassExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Volume features.
    */
   @Delegate
   Optional<? extends VolumeApi> getVolumeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Volume Attachment features.
    */
   @Delegate
   Optional<? extends VolumeAttachmentApi> getVolumeAttachmentExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Volume Type features.
    */
   @Delegate
   Optional<? extends VolumeTypeApi> getVolumeTypeExtensionForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

}
