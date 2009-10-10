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
package org.jclouds.azure.storage.blob.integration;

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.blob.AzureBlobContextBuilder;
import org.jclouds.azure.storage.blob.AzureBlobContextFactory;
import org.jclouds.azure.storage.blob.config.StubAzureBlobStoreModule;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.integration.internal.BaseTestInitializer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class AzureBlobTestInitializer extends
         BaseTestInitializer<AzureBlobConnection, ContainerMetadata, BlobMetadata, Blob> {

   @Override
   protected BlobStoreContext<AzureBlobConnection, ContainerMetadata, BlobMetadata, Blob> createLiveContext(
            Module configurationModule, String url, String app, String account, String key) {
      return new AzureBlobContextBuilder(account, key).relaxSSLHostname().withModules(
               configurationModule, new Log4JLoggingModule()).buildContext();
   }

   @Override
   protected BlobStoreContext<AzureBlobConnection, ContainerMetadata, BlobMetadata, Blob> createStubContext() {
      return AzureBlobContextFactory.createContext("user", "pass", new StubAzureBlobStoreModule());
   }
}