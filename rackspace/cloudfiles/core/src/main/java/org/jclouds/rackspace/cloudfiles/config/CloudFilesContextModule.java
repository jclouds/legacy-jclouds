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
package org.jclouds.rackspace.cloudfiles.config;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseBlobFromHeadersAndHttpContent.BlobFactory;
import org.jclouds.blobstore.functions.ParseBlobMetadataFromHeaders.BlobMetadataFactory;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.cloudfiles.CloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;
import org.jclouds.rackspace.cloudfiles.internal.GuiceCloudFilesContext;
import org.jclouds.rackspace.cloudfiles.internal.LiveCloudFilesInputStreamMap;
import org.jclouds.rackspace.cloudfiles.internal.LiveCloudFilesObjectMap;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Configures the Cloud Files connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class CloudFilesContextModule extends AbstractModule {
   protected final TypeLiteral<BlobMetadataFactory<BlobMetadata>> objectMetadataFactoryLiteral = new TypeLiteral<BlobMetadataFactory<BlobMetadata>>() {
   };
   protected final TypeLiteral<BlobFactory<BlobMetadata, Blob<BlobMetadata>>> objectFactoryLiteral = new TypeLiteral<BlobFactory<BlobMetadata, Blob<BlobMetadata>>>() {
   };

   @Override
   protected void configure() {
      this.requireBinding(CloudFilesBlobStore.class);
      bind(GuiceCloudFilesContext.CloudFilesObjectMapFactory.class).toProvider(
               FactoryProvider.newFactory(GuiceCloudFilesContext.CloudFilesObjectMapFactory.class,
                        LiveCloudFilesObjectMap.class));
      bind(GuiceCloudFilesContext.CloudFilesInputStreamMapFactory.class).toProvider(
               FactoryProvider.newFactory(
                        GuiceCloudFilesContext.CloudFilesInputStreamMapFactory.class,
                        LiveCloudFilesInputStreamMap.class));
      bind(objectMetadataFactoryLiteral).toProvider(
               FactoryProvider.newFactory(objectMetadataFactoryLiteral,
                        new TypeLiteral<BlobMetadata>() {
                        }));
      bind(objectFactoryLiteral).toProvider(
               FactoryProvider.newFactory(objectFactoryLiteral,
                        new TypeLiteral<Blob<BlobMetadata>>() {
                        }));
      bind(CloudFilesContext.class).to(GuiceCloudFilesContext.class);
   }
}