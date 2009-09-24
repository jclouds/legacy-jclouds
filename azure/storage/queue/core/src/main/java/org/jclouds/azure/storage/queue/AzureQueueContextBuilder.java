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
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_MAX_RETRIES;
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_SECURE;
import static org.jclouds.http.HttpConstants.PROPERTY_SAX_DEBUG;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_IO_WORKER_THREADS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTIONS;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_CONNECTION_REUSE;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_MAX_SESSION_FAILURES;
import static org.jclouds.http.pool.PoolConstants.PROPERTY_POOL_REQUEST_INVOKER_THREADS;

import java.util.List;
import java.util.Properties;

import org.jclouds.azure.storage.queue.config.AzureQueueContextModule;
import org.jclouds.azure.storage.queue.config.RestAzureQueueConnectionModule;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.azure.storage.xml.config.AzureStorageParserModule;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.inject.Injector;
import com.google.inject.Module;

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
public class AzureQueueContextBuilder extends
         CloudContextBuilder<AzureQueueContext> {

   public AzureQueueContextBuilder(Properties props) {
      super(props);
   }

   public static AzureQueueContextBuilder newBuilder(String id, String secret) {
      Properties properties = new Properties();

      properties.setProperty(PROPERTY_HTTP_ADDRESS, id + ".queue.core.windows.net");
      properties.setProperty(PROPERTY_HTTP_SECURE, "true");
      properties.setProperty(PROPERTY_SAX_DEBUG, "false");
      properties.setProperty(PROPERTY_HTTP_MAX_RETRIES, "5");
      properties.setProperty(PROPERTY_HTTP_MAX_REDIRECTS, "5");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTION_REUSE, "75");
      properties.setProperty(PROPERTY_POOL_MAX_SESSION_FAILURES, "2");
      properties.setProperty(PROPERTY_POOL_REQUEST_INVOKER_THREADS, "1");
      properties.setProperty(PROPERTY_POOL_IO_WORKER_THREADS, "2");
      properties.setProperty(PROPERTY_POOL_MAX_CONNECTIONS, "12");

      AzureQueueContextBuilder builder = new AzureQueueContextBuilder(properties);
      builder.authenticate(id, secret);
      return builder;
   }

   public void authenticate(String id, String secret) {
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT, checkNotNull(id,
               "azureStorageAccount"));
      properties.setProperty(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY, checkNotNull(secret,
               "azureStorageKey"));
   }

   public AzureQueueContext buildContext() {
      return buildInjector().getInstance(AzureQueueContext.class);
   }

   protected void addParserModule(List<Module> modules) {
      modules.add(new AzureStorageParserModule());
   }

   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureQueueContextModule());
   }

   protected void addConnectionModule(List<Module> modules) {
      modules.add(new RestAzureQueueConnectionModule());
   }

}
