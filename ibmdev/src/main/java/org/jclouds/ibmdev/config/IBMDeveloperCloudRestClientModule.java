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
package org.jclouds.ibmdev.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.encryption.EncryptionService;

import org.jclouds.ibmdev.IBMDeveloperCloud;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the IBMDeveloperCloud connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class IBMDeveloperCloudRestClientModule extends AbstractModule {

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_USER) String user,
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_PASSWORD) String password,
            EncryptionService encryptionService)
            throws UnsupportedEncodingException {
      return new BasicAuthentication(user, password, encryptionService);
   }

   @Provides
   @Singleton
   protected IBMDeveloperCloudAsyncClient provideClient(RestClientFactory factory) {
      return factory.create(IBMDeveloperCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   public IBMDeveloperCloudClient provideClient(IBMDeveloperCloudAsyncClient provider) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(IBMDeveloperCloudClient.class, provider);
   }
   
   @Provides
   @Singleton
   @IBMDeveloperCloud
   protected URI provideURI(@Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO
   }

}