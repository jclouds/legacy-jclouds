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
package org.jclouds.blobstore.integration;

import java.net.URI;
import java.util.List;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.blobstore.BlobStoreContextImpl;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.integration.config.StubBlobStoreConnectionModule;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public class StubBlobStoreContextBuilder
         extends
         BlobStoreContextBuilder<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

   public StubBlobStoreContextBuilder() {
      super(new TypeLiteral<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
      }, new TypeLiteral<ContainerMetadata>() {
      }, new TypeLiteral<BlobMetadata>() {
      }, new TypeLiteral<Blob<BlobMetadata>>() {
      });
   }

   @Override
   public void addContextModule(List<Module> modules) {
      modules.add(new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         BlobStoreContext<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> provideContext(
                  BlobMap.Factory<BlobMetadata, Blob<BlobMetadata>> blobMapFactory,
                  InputStreamMap.Factory<BlobMetadata> inputStreamMapFactory, Closer closer,
                  Provider<Blob<BlobMetadata>> blobProvider,
                  BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> api) {
            return new BlobStoreContextImpl<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>(
                     blobMapFactory, inputStreamMapFactory, closer, blobProvider, api, URI
                              .create("http://localhost/blobstub"), "foo");
         }

      });
   }

   @Override
   public CloudContextBuilder<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>> withEndpoint(
            URI endpoint) {
      return null;
   }

   @Override
   protected void addConnectionModule(List<Module> modules) {
      modules.add(new StubBlobStoreConnectionModule());
   }

}