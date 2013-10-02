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
package org.jclouds.azure.storage.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.azure.storage.handlers.AzureStorageClientErrorRetryHandler;
import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;

/**
 * Configures the AzureStorage connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class AzureStorageRestClientModule<S, A> extends RestClientModule<S, A> {
   protected AzureStorageRestClientModule() {

   }

   public AzureStorageRestClientModule(Map<Class<?>, Class<?>> delegate) {
	      super(delegate);
	   }
   
   public AzureStorageRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType) {
      super(syncClientType, asyncClientType);
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
   protected Supplier<String> provideTimeStampCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final DateService dateService) {
      return Suppliers.memoizeWithExpiration(new Supplier<String>() {
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAzureStorageErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAzureStorageErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AzureStorageClientErrorRetryHandler.class);
   }

   @Override
   protected void configure() {
      install(new AzureStorageParserModule());
      super.configure();
   }

}
