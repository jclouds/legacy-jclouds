/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.config;

import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT;
import static org.jclouds.atmosonline.saas.reference.AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorage;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.handlers.AtmosStorageClientErrorRetryHandler;
import org.jclouds.atmosonline.saas.handlers.ParseAtmosStorageErrorFromXmlContent;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the EMC Atmos Online Storage authentication service connection, including logging and
 * http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class AtmosStorageRestClientModule extends AbstractModule {
   @Provides
   @Singleton
   @AtmosStorage
   protected URI provideAuthenticationURI(@Named(PROPERTY_EMCSAAS_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   protected AtmosStorageAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(AtmosStorageAsyncClient.class);
   }

   @Provides
   @Singleton
   public AtmosStorageClient provideClient(AtmosStorageAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(AtmosStorageClient.class, client);
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
      return cache.get();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   Supplier<String> provideTimeStampCache(@Named(PROPERTY_EMCSAAS_SESSIONINTERVAL) long seconds,
            final DateService dateService) {
      return new ExpirableSupplier<String>(new Supplier<String>() {
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAtmosStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAtmosStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAtmosStorageErrorFromXmlContent.class);
   }

   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
               AtmosStorageClientErrorRetryHandler.class);
   }

}
