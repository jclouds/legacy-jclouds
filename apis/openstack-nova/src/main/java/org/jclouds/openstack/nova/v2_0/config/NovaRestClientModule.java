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
package org.jclouds.openstack.nova.v2_0.config;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsApi;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateApi;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassApi;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerAdminApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerAdminAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsApi;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceApi;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAttachmentAsyncApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeApi;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ImageAsyncApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.features.ServerAsyncApi;
import org.jclouds.openstack.nova.v2_0.handlers.NovaErrorHandler;
import org.jclouds.openstack.v2_0.domain.Extension;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.openstack.v2_0.functions.PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * Configures the Nova connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class NovaRestClientModule<S extends NovaApi, A extends NovaAsyncApi> extends RestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(ServerApi.class, ServerAsyncApi.class)
         .put(FlavorApi.class, FlavorAsyncApi.class)
         .put(ImageApi.class, ImageAsyncApi.class)
         .put(ExtensionApi.class, ExtensionAsyncApi.class)
         .put(FloatingIPApi.class, FloatingIPAsyncApi.class)
         .put(SecurityGroupApi.class, SecurityGroupAsyncApi.class)
         .put(KeyPairApi.class, KeyPairAsyncApi.class)
         .put(HostAdministrationApi.class, HostAdministrationAsyncApi.class)
         .put(SimpleTenantUsageApi.class, SimpleTenantUsageAsyncApi.class)
         .put(VirtualInterfaceApi.class, VirtualInterfaceAsyncApi.class)
         .put(ServerWithSecurityGroupsApi.class, ServerWithSecurityGroupsAsyncApi.class)
         .put(ServerAdminApi.class, ServerAdminAsyncApi.class)
         .put(HostAggregateApi.class, HostAggregateAsyncApi.class)
         .put(FlavorExtraSpecsApi.class, FlavorExtraSpecsAsyncApi.class)
         .put(QuotaApi.class, QuotaAsyncApi.class)
         .put(QuotaClassApi.class, QuotaClassAsyncApi.class)
         .put(VolumeApi.class, VolumeAsyncApi.class)
         .put(VolumeAttachmentApi.class, VolumeAttachmentAsyncApi.class)
         .put(VolumeTypeApi.class, VolumeTypeAsyncApi.class)
         .build();
   
   @SuppressWarnings("unchecked")
   public NovaRestClientModule() {
      super(TypeToken.class.cast(typeToken(NovaApi.class)), TypeToken.class.cast(typeToken(NovaAsyncApi.class)), DELEGATE_MAP);
   }

   protected NovaRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
            Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }

   @Override
   protected void configure() {
      bind(ImplicitOptionalConverter.class).to(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      super.configure();
   }
   
   @Provides
   @Singleton
   public Multimap<URI, URI> aliases() {
       return ImmutableMultimap.<URI, URI>builder()
          .put(URI.create(ExtensionNamespaces.SECURITY_GROUPS),
               URI.create("http://docs.openstack.org/compute/ext/securitygroups/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.FLOATING_IPS),
               URI.create("http://docs.openstack.org/compute/ext/floating_ips/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.KEYPAIRS),
               URI.create("http://docs.openstack.org/compute/ext/keypairs/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.SIMPLE_TENANT_USAGE),
               URI.create("http://docs.openstack.org/compute/ext/os-simple-tenant-usage/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.HOSTS),
               URI.create("http://docs.openstack.org/compute/ext/hosts/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.VOLUMES),
               URI.create("http://docs.openstack.org/compute/ext/volumes/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.VIRTUAL_INTERFACES),
               URI.create("http://docs.openstack.org/compute/ext/virtual_interfaces/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.CREATESERVEREXT),
               URI.create("http://docs.openstack.org/compute/ext/createserverext/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.ADMIN_ACTIONS),
               URI.create("http://docs.openstack.org/compute/ext/admin-actions/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.AGGREGATES),
               URI.create("http://docs.openstack.org/compute/ext/aggregates/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.FLAVOR_EXTRA_SPECS),
               URI.create("http://docs.openstack.org/compute/ext/flavor_extra_specs/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.QUOTAS),
               URI.create("http://docs.openstack.org/compute/ext/quotas-sets/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.QUOTA_CLASSES),
               URI.create("http://docs.openstack.org/compute/ext/quota-classes-sets/api/v1.1"))
          .put(URI.create(ExtensionNamespaces.VOLUME_TYPES),
               URI.create("http://docs.openstack.org/compute/ext/volume_types/api/v1.1"))
          .build();
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<? extends Extension>> provideExtensionsByZone(final Provider<NovaApi> novaApi) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(new CacheLoader<String, Set<? extends Extension>>() {
               @Override
               public Set<? extends Extension> load(String key) throws Exception {
                  return novaApi.get().getExtensionApiForZone(key).list();
               }
            });
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(NovaErrorHandler.class);
   }
}
