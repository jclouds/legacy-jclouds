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
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.azure.storage.blob.config.AzureBlobContextModule;
import org.jclouds.azure.storage.blob.config.RestAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

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
 * @author Adrian Cole, Andrew Newdigate
 * @see AzureBlobContext
 */
public class AzureBlobContextBuilder extends
         BlobStoreContextBuilder<AzureBlobConnection, ContainerMetadata, BlobMetadata, Blob> {

   @Override
   public AzureBlobContext buildContext() {
      return this.buildInjector().getInstance(AzureBlobContext.class);
   }

   public AzureBlobContextBuilder(Properties props) {
      super(new TypeLiteral<AzureBlobConnection>() {
      }, new TypeLiteral<ContainerMetadata>() {
      }, new TypeLiteral<BlobMetadata>() {
      }, new TypeLiteral<Blob>() {
      }, props);
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-ms-meta-");
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT,
               "https://{account}.blob.core.windows.net");
      if (!properties.containsKey(PROPERTY_AZURESTORAGE_SESSIONINTERVAL))
         this.withTimeStampExpiration(60);
   }

   public AzureBlobContextBuilder(String id, String secret) {
      this(new Properties());
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
      String endpoint = properties.getProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT);
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, endpoint.replaceAll(
               "\\{account\\}", id));
   }

   @Override
   public AzureBlobContextBuilder relaxSSLHostname() {
      return (AzureBlobContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public AzureBlobContextBuilder withExecutorService(ExecutorService service) {
      return (AzureBlobContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AzureBlobContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (AzureBlobContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public AzureBlobContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (AzureBlobContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public AzureBlobContextBuilder withModule(Module module) {
      return (AzureBlobContextBuilder) super.withModule(module);
   }

   @Override
   public AzureBlobContextBuilder withModules(Module... modules) {
      return (AzureBlobContextBuilder) super.withModules(modules);
   }

   @Override
   public AzureBlobContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (AzureBlobContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public AzureBlobContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (AzureBlobContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public AzureBlobContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (AzureBlobContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public AzureBlobContextBuilder withRequestTimeout(long milliseconds) {
      return (AzureBlobContextBuilder) super.withRequestTimeout(milliseconds);
   }

   @Override
   public AzureBlobContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (AzureBlobContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public AzureBlobContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (AzureBlobContextBuilder) super
               .withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }

   @Override
   public AzureBlobContextBuilder withEndpoint(URI endpoint) {
      properties.setProperty(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT, checkNotNull(endpoint,
               "endpoint").toString());
      return (AzureBlobContextBuilder) this;
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureBlobContextModule());
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestAzureBlobStoreModule());
   }

   public AzureBlobContextBuilder withTimeStampExpiration(long seconds) {
      getProperties().setProperty(PROPERTY_AZURESTORAGE_SESSIONINTERVAL, seconds + "");
      return this;
   }
}
