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
package org.jclouds.blobstore.strategy.internal;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.strategy.ContainerCountStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;

/**
 * counts all blobs in the blobstore by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
public class KeyCountStrategy<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements ContainerCountStrategy<C, M, B> {
   protected final GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata;

   @Inject
   KeyCountStrategy(GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata) {
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public long execute(BlobStore<C, M, B> connection, String container) {
      return getAllBlobMetadata.execute(connection, container).size();
   }

}