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
package org.jclouds.azure.storage.blob.internal;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * Uses a Guice Injector to configure the objects served by AzureBlobContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceAzureBlobContext implements AzureBlobContext {
   public interface AzureBlobObjectMapFactory {
      BlobMap<BlobMetadata, Blob> createMapView(String container);
   }

   public interface AzureBlobInputStreamMapFactory {
      InputStreamMap<BlobMetadata> createMapView(String container);
   }

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;
   private final AzureBlobInputStreamMapFactory azureInputStreamMapFactory;
   private final AzureBlobObjectMapFactory azureObjectMapFactory;
   private final URI endPoint;
   private final String account;

   @Inject
   private GuiceAzureBlobContext(Injector injector, Closer closer,
            AzureBlobObjectMapFactory azureObjectMapFactory,
            AzureBlobInputStreamMapFactory azureInputStreamMapFactory,
            @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account, URI endPoint) {
      this.injector = injector;
      this.closer = closer;
      this.azureInputStreamMapFactory = azureInputStreamMapFactory;
      this.azureObjectMapFactory = azureObjectMapFactory;
      this.endPoint = endPoint;
      this.account = account;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   public String getAccount() {
      return account;
   }

   public AzureBlobStore getApi() {
      return injector.getInstance(AzureBlobStore.class);
   }

   public URI getEndPoint() {
      return endPoint;
   }

   public BlobMap<BlobMetadata, Blob> createBlobMap(String container) {
      getApi().createContainer(container);
      return azureObjectMapFactory.createMapView(container);
   }

   public InputStreamMap<BlobMetadata> createInputStreamMap(String container) {
      getApi().createContainer(container);
      return azureInputStreamMapFactory.createMapView(container);
   }
}
