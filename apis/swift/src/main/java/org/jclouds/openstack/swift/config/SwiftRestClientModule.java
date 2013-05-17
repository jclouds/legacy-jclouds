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
package org.jclouds.openstack.swift.config;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.functions.URIFromAuthenticationResponseForService;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.openstack.swift.SwiftClient;
import org.jclouds.openstack.swift.handlers.ParseSwiftErrorFromHttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 *
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class SwiftRestClientModule<S extends CommonSwiftClient, A extends CommonSwiftAsyncClient> extends
      RestClientModule<S, A> {

   @SuppressWarnings("unchecked")
   public SwiftRestClientModule() {
      this(TypeToken.class.cast(typeToken(SwiftClient.class)), TypeToken.class.cast(typeToken(SwiftAsyncClient.class)),
            ImmutableMap.<Class<?>, Class<?>> of());
   }

   protected SwiftRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType,
         Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }

   public static class StorageEndpointModule extends OpenStackAuthenticationModule {
      @Provides
      @Singleton
      @Storage
      protected Supplier<URI> provideStorageUrl(URIFromAuthenticationResponseForService.Factory factory) {
         return factory.create(AuthHeaders.STORAGE_URL);
      }
   }

   public static class KeystoneStorageEndpointModule extends KeystoneAuthenticationModule {

      @Provides
      @Singleton
      @Storage
      protected Supplier<URI> provideStorageUrl(RegionIdToURISupplier.Factory factory, @ApiVersion String apiVersion) {
         return getLastValueInMap(factory.createForApiTypeAndVersion(ServiceType.OBJECT_STORE, apiVersion));
      }

   }

   @Override
   protected void configure() {
      install(new SwiftObjectModule());
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
      bindResolvedClientsToCommonSwift();
   }

   protected void bindResolvedClientsToCommonSwift() {
      bind(CommonSwiftClient.class).to(SwiftClient.class).in(Scopes.SINGLETON);
      bind(CommonSwiftAsyncClient.class).to(SwiftAsyncClient.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseSwiftErrorFromHttpResponse.class);
   }

}
