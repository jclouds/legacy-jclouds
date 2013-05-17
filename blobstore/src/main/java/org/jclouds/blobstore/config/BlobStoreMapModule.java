/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.internal.BlobMapImpl;
import org.jclouds.blobstore.internal.InputStreamMapImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.blobstore.strategy.internal.ListContainerAndRecurseThroughFolders;

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
      BlobStore connection;
      @Inject
      GetBlobsInListStrategy getAllBlobs;
      @Inject
      ContainsValueInListStrategy containsValueStrategy;
      @Inject
      PutBlobsStrategy putBlobsStrategy;
      @Inject
      ListContainerAndRecurseThroughFolders listStrategy;
      @Inject
      Provider<BlobBuilder> blobBuilders;

      public BlobMap create(String containerName, ListContainerOptions options) {
         return new BlobMapImpl(connection, getAllBlobs, containsValueStrategy, putBlobsStrategy, listStrategy,
               containerName, options, blobBuilders);
      }

   }

   private static class InputStreamMapFactory implements InputStreamMap.Factory {
      @Inject
      BlobStore connection;
      @Inject
      Provider<BlobBuilder> blobBuilders;
      @Inject
      GetBlobsInListStrategy getAllBlobs;
      @Inject
      ContainsValueInListStrategy containsValueStrategy;
      @Inject
      PutBlobsStrategy putBlobsStrategy;
      @Inject
      ListContainerAndRecurseThroughFolders listStrategy;

      public InputStreamMap create(String containerName, ListContainerOptions options) {
         return new InputStreamMapImpl(connection, blobBuilders, getAllBlobs, listStrategy, containsValueStrategy,
               putBlobsStrategy, containerName, options);
      }

   }

}
