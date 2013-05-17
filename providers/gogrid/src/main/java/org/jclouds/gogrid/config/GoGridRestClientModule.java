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
package org.jclouds.gogrid.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.date.TimeStamp;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.handlers.GoGridErrorHandler;
import org.jclouds.gogrid.location.GoGridDefaultLocationSupplier;
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
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the GoGrid connection.
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@ConfiguresRestClient
public class GoGridRestClientModule extends RestClientModule<GoGridClient, GoGridAsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(GridServerClient.class, GridServerAsyncClient.class)//
         .put(GridJobClient.class, GridJobAsyncClient.class)//
         .put(GridIpClient.class, GridIpAsyncClient.class)//
         .put(GridLoadBalancerClient.class, GridLoadBalancerAsyncClient.class)//
         .put(GridImageClient.class, GridImageAsyncClient.class)//
         .build();

   public GoGridRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Provides
   @TimeStamp
   protected Long provideTimeStamp(@TimeStamp Supplier<Long> cache) {
      return cache.get();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   Supplier<Long> provideTimeStampCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return Suppliers.memoizeWithExpiration(new Supplier<Long>() {
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
      install(new GoGridParserModule());
      super.configure();
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(GoGridDefaultLocationSupplier.class).in(Scopes.SINGLETON);
   }
}
