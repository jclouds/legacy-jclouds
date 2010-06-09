/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.http.functions.config.ParserModule.LongDateAdapter;
import org.jclouds.ibmdev.IBMDeveloperCloud;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.handlers.IBMDeveloperCloudErrorHandler;
import org.jclouds.ibmdev.predicates.InstanceActive;
import org.jclouds.ibmdev.predicates.InstanceRemovedOrNotFound;
import org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Predicate;
import com.google.inject.Provides;

/**
 * Configures the IBMDeveloperCloud connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class IBMDeveloperCloudRestClientModule extends
         RestClientModule<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient> {

   public IBMDeveloperCloudRestClientModule() {
      super(IBMDeveloperCloudClient.class, IBMDeveloperCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_USER) String user,
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_PASSWORD) String password,
            EncryptionService encryptionService) throws UnsupportedEncodingException {
      return new BasicAuthentication(user, password, encryptionService);
   }

   @Provides
   @Singleton
   @IBMDeveloperCloud
   protected URI provideURI(
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               IBMDeveloperCloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               IBMDeveloperCloudErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               IBMDeveloperCloudErrorHandler.class);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(LongDateAdapter.class);
      super.configure();
   }

   @Provides
   @Singleton
   @Named("ACTIVE")
   protected Predicate<Instance> instanceActive(InstanceActive instanceActive) {
      return new RetryablePredicate<Instance>(instanceActive, 1200, 3, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Named("REMOVED")
   Predicate<Instance> instanceRemoved(InstanceRemovedOrNotFound instanceRemoved) {
      return new RetryablePredicate<Instance>(instanceRemoved, 5000, 500, TimeUnit.MILLISECONDS);
   }
}