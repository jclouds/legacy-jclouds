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
package org.jclouds.azure.storage.queue;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.azure.storage.queue.config.AzureQueueContextModule;
import org.jclouds.azure.storage.queue.config.RestAzureQueueConnectionModule;
import org.jclouds.azure.storage.queue.reference.AzureQueueConstants;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link AzureQueueContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see AzureQueueContext
 */
public class AzureQueueContextBuilder extends CloudContextBuilder<AzureQueueConnection> {
   private static final TypeLiteral<AzureQueueConnection> connectionType = new TypeLiteral<AzureQueueConnection>() {
   };

   public AzureQueueContextBuilder(String id, String secret) {
      this(new Properties());
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
      String endpoint = properties.getProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT);
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, endpoint.replaceAll(
               "\\{account\\}", id));
   }

   public AzureQueueContextBuilder(Properties properties) {
      super(connectionType, properties);
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT,
               "https://{account}.queue.core.windows.net");
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureQueueContextModule());
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestAzureQueueConnectionModule());
   }

   @Override
   public AzureQueueContextBuilder withEndpoint(URI endpoint) {
      String account = checkNotNull(properties
               .getProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT),
               "azureStorageAccount");
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, checkNotNull(
               endpoint, "endpoint").toString());
      properties.setProperty(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT, endpoint.toString()
               .replaceAll("\\{account\\}", account));
      return this;
   }

   // below is to cast the builder to the correct type so that chained builder methods end correctly

   @Override
   public AzureQueueContext buildContext() {
      Injector injector = buildInjector();
      return injector.getInstance(AzureQueueContext.class);
   }

   @Override
   public AzureQueueContextBuilder relaxSSLHostname() {
      return (AzureQueueContextBuilder) super.relaxSSLHostname();
   }

   @Override
   public AzureQueueContextBuilder withExecutorService(ExecutorService service) {
      return (AzureQueueContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AzureQueueContextBuilder withHttpMaxRedirects(int httpMaxRedirects) {
      return (AzureQueueContextBuilder) super.withHttpMaxRedirects(httpMaxRedirects);
   }

   @Override
   public AzureQueueContextBuilder withHttpMaxRetries(int httpMaxRetries) {
      return (AzureQueueContextBuilder) super.withHttpMaxRetries(httpMaxRetries);
   }

   @Override
   public AzureQueueContextBuilder withModule(Module module) {
      return (AzureQueueContextBuilder) super.withModule(module);
   }

   @Override
   public AzureQueueContextBuilder withModules(Module... modules) {
      return (AzureQueueContextBuilder) super.withModules(modules);
   }

   @Override
   public AzureQueueContextBuilder withPoolIoWorkerThreads(int poolIoWorkerThreads) {
      return (AzureQueueContextBuilder) super.withPoolIoWorkerThreads(poolIoWorkerThreads);
   }

   @Override
   public AzureQueueContextBuilder withPoolMaxConnectionReuse(int poolMaxConnectionReuse) {
      return (AzureQueueContextBuilder) super.withPoolMaxConnectionReuse(poolMaxConnectionReuse);
   }

   @Override
   public AzureQueueContextBuilder withPoolMaxConnections(int poolMaxConnections) {
      return (AzureQueueContextBuilder) super.withPoolMaxConnections(poolMaxConnections);
   }

   @Override
   public AzureQueueContextBuilder withPoolMaxSessionFailures(int poolMaxSessionFailures) {
      return (AzureQueueContextBuilder) super.withPoolMaxSessionFailures(poolMaxSessionFailures);
   }

   @Override
   public AzureQueueContextBuilder withPoolRequestInvokerThreads(int poolRequestInvokerThreads) {
      return (AzureQueueContextBuilder) super
               .withPoolRequestInvokerThreads(poolRequestInvokerThreads);
   }
}
