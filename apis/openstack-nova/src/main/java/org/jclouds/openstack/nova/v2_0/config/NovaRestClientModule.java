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
package org.jclouds.openstack.nova.v2_0.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.nova.v2_0.NovaAsyncClient;
import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.jclouds.openstack.nova.v2_0.extensions.AdminActionsAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.AdminActionsClient;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsClient;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAdministrationClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.HostAggregateClient;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClassClient;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaClient;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.ServerWithSecurityGroupsClient;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageClient;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.VirtualInterfaceClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeAsyncClient;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeClient;
import org.jclouds.openstack.nova.v2_0.features.ExtensionAsyncClient;
import org.jclouds.openstack.nova.v2_0.features.ExtensionClient;
import org.jclouds.openstack.nova.v2_0.features.FlavorAsyncClient;
import org.jclouds.openstack.nova.v2_0.features.FlavorClient;
import org.jclouds.openstack.nova.v2_0.features.ImageAsyncClient;
import org.jclouds.openstack.nova.v2_0.features.ImageClient;
import org.jclouds.openstack.nova.v2_0.features.ServerAsyncClient;
import org.jclouds.openstack.nova.v2_0.features.ServerClient;
import org.jclouds.openstack.nova.v2_0.functions.PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet;
import org.jclouds.openstack.nova.v2_0.handlers.NovaErrorHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

/**
 * Configures the Nova connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class NovaRestClientModule extends RestClientModule<NovaClient, NovaAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(ServerClient.class, ServerAsyncClient.class).put(FlavorClient.class, FlavorAsyncClient.class)
         .put(ImageClient.class, ImageAsyncClient.class).put(ExtensionClient.class, ExtensionAsyncClient.class)
         .put(FloatingIPClient.class, FloatingIPAsyncClient.class)
         .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)
         .put(KeyPairClient.class, KeyPairAsyncClient.class)
         .put(HostAdministrationClient.class, HostAdministrationAsyncClient.class)
         .put(SimpleTenantUsageClient.class, SimpleTenantUsageAsyncClient.class)
         .put(VolumeClient.class, VolumeAsyncClient.class)
         .put(VirtualInterfaceClient.class, VirtualInterfaceAsyncClient.class)
         .put(ServerWithSecurityGroupsClient.class, ServerWithSecurityGroupsAsyncClient.class)
         .put(AdminActionsClient.class, AdminActionsAsyncClient.class)
         .put(HostAggregateClient.class, HostAggregateAsyncClient.class)
         .put(FlavorExtraSpecsClient.class, FlavorExtraSpecsAsyncClient.class)
         .put(QuotaClient.class, QuotaAsyncClient.class)
         .put(QuotaClassClient.class, QuotaClassAsyncClient.class)
         .put(VolumeTypeClient.class, VolumeTypeAsyncClient.class)
         .build();

   public NovaRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      install(new NovaParserModule());
      bind(ImplicitOptionalConverter.class).to(
            PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      super.configure();
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<Extension>> provideExtensionsByZone(final Provider<NovaClient> novaClient) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(new CacheLoader<String, Set<Extension>>() {

               @Override
               public Set<Extension> load(String key) throws Exception {
                  return novaClient.get().getExtensionClientForZone(key).listExtensions();
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
