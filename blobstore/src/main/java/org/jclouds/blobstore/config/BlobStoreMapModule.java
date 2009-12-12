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
package org.jclouds.blobstore.config;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobMapImpl;
import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all Blob implementations
 * 
 * @author Adrian Cole
 */
public class BlobStoreMapModule extends AbstractModule {

   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      bind(BlobMap.Factory.class).to(BlobMapFactory.class).in(Scopes.SINGLETON);
      bind(InputStreamMap.Factory.class).to(InputStreamMapFactory.class).in(Scopes.SINGLETON);
   }

   private static class BlobMapFactory implements BlobMap.Factory {
      @Inject
      AsyncBlobStore connection;
      @Inject
      GetBlobsInListStrategy getAllBlobs;
      @Inject
      ListBlobMetadataStrategy getAllBlobMetadata;
      @Inject
      ContainsValueInListStrategy containsValueStrategy;
      @Inject
      ClearListStrategy clearContainerStrategy;
      @Inject
      CountListStrategy containerCountStrategy;

      public BlobMap create(String containerName, ListContainerOptions listOptions) {
         return new BlobMapImpl(connection, getAllBlobs, getAllBlobMetadata, containsValueStrategy,
                  clearContainerStrategy, containerCountStrategy, containerName, listOptions);
      }

   }

   private static class InputStreamMapFactory implements InputStreamMap.Factory {
      @Inject
      AsyncBlobStore connection;
      @Inject
      Blob.Factory blobFactory;
      @Inject
      GetBlobsInListStrategy getAllBlobs;
      @Inject
      ListBlobMetadataStrategy getAllBlobMetadata;
      @Inject
      ContainsValueInListStrategy containsValueStrategy;
      @Inject
      ClearListStrategy clearContainerStrategy;
      @Inject
      CountListStrategy containerCountStrategy;

      public InputStreamMap create(String containerName, ListContainerOptions listOptions) {
         return new InputStreamMapImpl(connection, blobFactory, getAllBlobs, getAllBlobMetadata,
                  containsValueStrategy, clearContainerStrategy, containerCountStrategy,
                  containerName, listOptions);
      }

   }

}