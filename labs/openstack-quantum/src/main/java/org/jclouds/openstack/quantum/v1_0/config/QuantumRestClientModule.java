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
package org.jclouds.openstack.quantum.v1_0.config;

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
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.quantum.v1_0.QuantumApi;
import org.jclouds.openstack.quantum.v1_0.QuantumAsyncApi;
import org.jclouds.openstack.quantum.v1_0.features.NetworkApi;
import org.jclouds.openstack.quantum.v1_0.features.NetworkAsyncApi;
import org.jclouds.openstack.quantum.v1_0.features.PortApi;
import org.jclouds.openstack.quantum.v1_0.features.PortAsyncApi;
import org.jclouds.openstack.quantum.v1_0.handlers.QuantumErrorHandler;
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
 * Configures the Quantum connection.
 * 
 * @author Adam Lowe
 */
@ConfiguresRestClient
public class QuantumRestClientModule<S extends QuantumApi, A extends QuantumAsyncApi> extends RestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(ExtensionApi.class, ExtensionAsyncApi.class)
         .put(NetworkApi.class, NetworkAsyncApi.class)
         .put(PortApi.class, PortAsyncApi.class)
         .build();

   @SuppressWarnings("unchecked")
   public QuantumRestClientModule() {
      super(TypeToken.class.cast(TypeToken.of(QuantumApi.class)), TypeToken.class.cast(TypeToken.of(QuantumAsyncApi.class)), DELEGATE_MAP);
   }

   protected QuantumRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType, Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   
   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bind(ImplicitOptionalConverter.class).to(PresentWhenExtensionAnnotationNamespaceEqualsAnyNamespaceInExtensionsSet.class);
      super.configure();
   }
   
   @Provides
   @Singleton
   public Multimap<URI, URI> aliases() {
       return ImmutableMultimap.<URI, URI>builder()
          .build();
   }

   @Provides
   @Singleton
   public LoadingCache<String, Set<? extends Extension>> provideExtensionsByZone(final Provider<QuantumApi> quantumApi) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS)
            .build(new CacheLoader<String, Set<? extends Extension>>() {
               @Override
               public Set<? extends Extension> load(String key) throws Exception {
                  return quantumApi.get().getExtensionApiForZone(key).list();
               }
            });
   }
   
   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(QuantumErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(QuantumErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(QuantumErrorHandler.class);
   }
}
