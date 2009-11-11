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
package org.jclouds.azure.storage.config;

import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.util.DateService;
import org.jclouds.util.TimeStamp;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the AzureStorage connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class AzureStorageRestClientModule extends AbstractModule {

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
   Supplier<String> provideTimeStampCache(
            @Named(PROPERTY_AZURESTORAGE_SESSIONINTERVAL) long seconds,
            final DateService dateService) {
      return new ExpirableSupplier<String>(new Supplier<String>() {
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAzureStorageErrorFromXmlContent.class);
   }

   protected void bindRetryHandlers() {
   }

}