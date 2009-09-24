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
package org.jclouds.azure.storage.blob;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.RestAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link AzureBlobContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see AzureBlobContext
 */
public class AzureBlobContextBuilder extends CloudContextBuilder<AzureBlobContext> {

   public AzureBlobContextBuilder(Properties props) {
      super(props);
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT,
               "https://{account}.blob.core.windows.net");
   }

   public static AzureBlobContextBuilder newBuilder(String id, String secret) {
      Properties properties = new Properties();
      AzureBlobContextBuilder builder = new AzureBlobContextBuilder(properties);
      builder.authenticate(id, secret);
      return builder;
   }

   @Override
   public CloudContextBuilder<AzureBlobContext> withEndpoint(URI endpoint) {
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, checkNotNull(endpoint,
               "endpoint").toString());
      return this;
   }

   public void authenticate(String id, String secret) {
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
      String endpoint = properties.getProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT);
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, endpoint.replaceAll(
               "\\{account\\}", id));
   }

   public AzureBlobContext buildContext() {
      return buildInjector().getInstance(AzureBlobContext.class);
   }

   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureBlobContextModule());
   }

   protected void addApiModule(List<Module> modules) {
      modules.add(new RestAzureBlobStoreModule());
   }

}
