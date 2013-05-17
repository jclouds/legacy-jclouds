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
package org.jclouds.openstack.cinder.v1.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.CinderAsyncApi;
import org.jclouds.openstack.cinder.v1.features.SnapshotApi;
import org.jclouds.openstack.cinder.v1.features.SnapshotAsyncApi;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.cinder.v1.features.VolumeAsyncApi;
import org.jclouds.openstack.cinder.v1.features.VolumeTypeApi;
import org.jclouds.openstack.cinder.v1.features.VolumeTypeAsyncApi;
import org.jclouds.openstack.cinder.v1.handlers.CinderErrorHandler;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.openstack.v2_0.features.ExtensionAsyncApi;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * Configures the Cinder connection.
 * 
 * @author Everett Toews
 */
@ConfiguresRestClient
public class CinderRestClientModule<S extends CinderApi, A extends CinderAsyncApi> extends RestClientModule<S, A> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(ExtensionApi.class, ExtensionAsyncApi.class)
         .put(VolumeApi.class, VolumeAsyncApi.class)
         .put(VolumeTypeApi.class, VolumeTypeAsyncApi.class)
         .put(SnapshotApi.class, SnapshotAsyncApi.class)
         .build();

   @SuppressWarnings("unchecked")
   public CinderRestClientModule() {
      super(TypeToken.class.cast(typeToken(CinderApi.class)), TypeToken.class.cast(typeToken(CinderAsyncApi.class)), DELEGATE_MAP);
   }

   protected CinderRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType, Map<Class<?>, Class<?>> sync2Async) {
      super(syncClientType, asyncClientType, sync2Async);
   }
   
   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }
   
   @Provides
   @Singleton
   public Multimap<URI, URI> aliases() {
      return ImmutableMultimap.<URI, URI>builder().build();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CinderErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CinderErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CinderErrorHandler.class);
   }
}
