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

import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.azure.storage.blob.AzureBlobStore;
import org.jclouds.azure.storage.blob.domain.Blob;
import org.jclouds.azure.storage.blob.domain.BlobMetadata;
import org.jclouds.azure.storage.blob.internal.GuiceAzureBlobContext;
import org.jclouds.azure.storage.blob.internal.LiveAzureBlobInputStreamMap;
import org.jclouds.azure.storage.blob.internal.LiveAzureBlobObjectMap;
import org.jclouds.blobstore.functions.ParseBlobFromHeadersAndHttpContent.BlobFactory;
import org.jclouds.blobstore.functions.ParseContentTypeFromHeaders.BlobMetadataFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Configures the {@link AzureBlobContext}; requires {@link AzureBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class AzureBlobContextModule extends AbstractModule {
   protected final TypeLiteral<BlobMetadataFactory<BlobMetadata>> objectMetadataFactoryLiteral = new TypeLiteral<BlobMetadataFactory<BlobMetadata>>() {
   };
   protected final TypeLiteral<BlobFactory<BlobMetadata, Blob>> objectFactoryLiteral = new TypeLiteral<BlobFactory<BlobMetadata, Blob>>() {
   };

   @Override
   protected void configure() {
      this.requireBinding(AzureBlobStore.class);
      bind(GuiceAzureBlobContext.AzureBlobObjectMapFactory.class).toProvider(
               FactoryProvider.newFactory(GuiceAzureBlobContext.AzureBlobObjectMapFactory.class,
                        LiveAzureBlobObjectMap.class));
      bind(GuiceAzureBlobContext.AzureBlobInputStreamMapFactory.class).toProvider(
               FactoryProvider.newFactory(
                        GuiceAzureBlobContext.AzureBlobInputStreamMapFactory.class,
                        LiveAzureBlobInputStreamMap.class));
      bind(AzureBlobContext.class).to(GuiceAzureBlobContext.class);
      bind(objectMetadataFactoryLiteral).toProvider(
               FactoryProvider.newFactory(objectMetadataFactoryLiteral,
                        new TypeLiteral<BlobMetadata>() {
                        }));
      bind(objectFactoryLiteral).toProvider(
               FactoryProvider.newFactory(objectFactoryLiteral, new TypeLiteral<Blob>() {
               }));
   }

}
