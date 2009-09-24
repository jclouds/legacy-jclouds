/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.azure.storage.blob.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azure.storage.blob.AzureBlob;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.config.RestAzureStorageConnectionModule;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.Provides;

/**
 * Configures the Azure Blob Service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestAzureBlobStoreModule extends RestAzureStorageConnectionModule {

   @Override
   protected void configure() {
      super.configure();
   }

   @Provides
   @Singleton
   @AzureBlob
   protected URI provideAuthenticationURI(
            @Named(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected AzureBlobStore provideAzureBlobStore(@AzureBlob URI uri, RestClientFactory factory) {
      return factory.create(uri, AzureBlobStore.class);
   }

}