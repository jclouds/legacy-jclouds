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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.domain.ContainerMetadata;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.blobstore.BlobStoreContextImpl;
import org.jclouds.blobstore.BlobMap.Factory;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configures the {@link AzureBlobContext}; requires {@link AzureBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AzureBlobContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(AzureBlobContext.class).to(AzureBlobContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class AzureBlobContextImpl extends
            BlobStoreContextImpl<AzureBlobStore, ContainerMetadata, BlobMetadata, Blob> implements
            AzureBlobContext {
      @Inject
      AzureBlobContextImpl(Factory<BlobMetadata, Blob> blobMapFactory,
               org.jclouds.blobstore.InputStreamMap.Factory<BlobMetadata> inputStreamMapFactory,
               Closer closer, Provider<Blob> blobProvider, AzureBlobStore defaultApi,
               @AzureBlob URI endPoint,
               @Named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT) String account) {
         super(blobMapFactory, inputStreamMapFactory, closer, blobProvider, defaultApi, endPoint,
                  account);
      }

   }
}
