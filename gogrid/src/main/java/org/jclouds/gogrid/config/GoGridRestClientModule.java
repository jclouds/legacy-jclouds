/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.config;

import static org.jclouds.Constants.PROPERTY_GSON_ADAPTERS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.domain.IpState;
import org.jclouds.gogrid.domain.JobState;
import org.jclouds.gogrid.domain.LoadBalancerOs;
import org.jclouds.gogrid.domain.LoadBalancerPersistenceType;
import org.jclouds.gogrid.domain.LoadBalancerState;
import org.jclouds.gogrid.domain.LoadBalancerType;
import org.jclouds.gogrid.domain.ObjectType;
import org.jclouds.gogrid.domain.ServerImageState;
import org.jclouds.gogrid.domain.ServerImageType;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.gogrid.handlers.GoGridErrorHandler;
import org.jclouds.gogrid.services.GridImageAsyncClient;
import org.jclouds.gogrid.services.GridImageClient;
import org.jclouds.gogrid.services.GridIpAsyncClient;
import org.jclouds.gogrid.services.GridIpClient;
import org.jclouds.gogrid.services.GridJobAsyncClient;
import org.jclouds.gogrid.services.GridJobClient;
import org.jclouds.gogrid.services.GridLoadBalancerAsyncClient;
import org.jclouds.gogrid.services.GridLoadBalancerClient;
import org.jclouds.gogrid.services.GridServerAsyncClient;
import org.jclouds.gogrid.services.GridServerClient;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Provides;

/**
 * Configures the GoGrid connection.
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@RequiresHttp
@ConfiguresRestClient
public class GoGridRestClientModule extends RestClientModule<GoGridClient, GoGridAsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
            .<Class<?>, Class<?>> builder()//
            .put(GridServerClient.class, GridServerAsyncClient.class)//
            .put(GridJobClient.class, GridJobAsyncClient.class)//
            .put(GridIpClient.class, GridIpAsyncClient.class)//
            .put(GridLoadBalancerClient.class, GridLoadBalancerAsyncClient.class)//
            .put(GridImageClient.class, GridImageAsyncClient.class)//
            .build();

   public GoGridRestClientModule() {
      super(GoGridClient.class, GoGridAsyncClient.class, DELEGATE_MAP);
   }

   @Provides
   @TimeStamp
   protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
      return cache.get();
   }

   @SuppressWarnings("unchecked")
   @Provides
   @Singleton
   @com.google.inject.name.Named(PROPERTY_GSON_ADAPTERS)
   public Map<Class, Object> provideCustomAdapterBindings() {
      Map<Class, Object> bindings = Maps.newHashMap();
      bindings.put(ObjectType.class, new CustomDeserializers.ObjectTypeAdapter());
      bindings.put(LoadBalancerOs.class, new CustomDeserializers.LoadBalancerOsAdapter());
      bindings.put(LoadBalancerState.class, new CustomDeserializers.LoadBalancerStateAdapter());
      bindings.put(LoadBalancerPersistenceType.class,
               new CustomDeserializers.LoadBalancerPersistenceTypeAdapter());
      bindings.put(LoadBalancerType.class, new CustomDeserializers.LoadBalancerTypeAdapter());
      bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
      bindings.put(JobState.class, new CustomDeserializers.JobStateAdapter());
      bindings.put(ServerImageState.class, new CustomDeserializers.ServerImageStateAdapter());
      bindings.put(ServerImageType.class, new CustomDeserializers.ServerImageTypeAdapter());
      return bindings;
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   Supplier<Long> provideTimeStampCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return new ExpirableSupplier<Long>(new Supplier<Long>() {
         public Long get() {
            return System.currentTimeMillis() / 1000;
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GoGridErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GoGridErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GoGridErrorHandler.class);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(DateSecondsAdapter.class);
      super.configure();
   }

}