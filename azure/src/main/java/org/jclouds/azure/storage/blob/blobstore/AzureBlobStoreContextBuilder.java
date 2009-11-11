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
package org.jclouds.azure.storage.blob.blobstore;

import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_METADATA_PREFIX;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_RETRY;
import static org.jclouds.azure.storage.blob.reference.AzureBlobConstants.PROPERTY_AZUREBLOB_TIMEOUT;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.config.AzureBlobStoreContextModule;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link AzureBlobStoreContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see AzureBlobStoreContext
 */
public class AzureBlobStoreContextBuilder extends BlobStoreContextBuilder<AzureBlobClient> {

   public AzureBlobStoreContextBuilder(Properties props) {
      super(new TypeLiteral<AzureBlobClient>() {
      }, convert(props));
   }

   private static Properties convert(Properties props) {
      for (Entry<String, String> entry : ImmutableMap.of(PROPERTY_AZUREBLOB_METADATA_PREFIX,
               PROPERTY_USER_METADATA_PREFIX, PROPERTY_AZUREBLOB_RETRY, PROPERTY_BLOBSTORE_RETRY,
               PROPERTY_AZUREBLOB_TIMEOUT, PROPERTY_USER_METADATA_PREFIX).entrySet()) {
         if (props.containsKey(entry.getKey()))
            props.setProperty(entry.getValue(), props.getProperty(entry.getKey()));
      }
      return props;
   }

   @Override
   public AzureBlobStoreContextBuilder withExecutorService(ExecutorService service) {
      return (AzureBlobStoreContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AzureBlobStoreContextBuilder withModules(Module... modules) {
      return (AzureBlobStoreContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AzureBlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AzureBlobRestClientModule());
   }
}
