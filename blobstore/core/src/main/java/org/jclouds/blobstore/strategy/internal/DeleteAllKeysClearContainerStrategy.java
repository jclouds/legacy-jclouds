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

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.util.Utils;

import com.google.common.collect.Sets;

/**
 * Deletes all keys in the container
 * 
 * @author Adrian Cole
 */
public class DeleteAllKeysClearContainerStrategy<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements ClearContainerStrategy<C, M, B> {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata;

   @Inject
   DeleteAllKeysClearContainerStrategy(GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata) {
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public void execute(BlobStore<C, M, B> connection, final String containerName) {
      Set<Future<Void>> deletes = Sets.newHashSet();
      for (M md : getAllBlobMetadata.execute(connection, containerName)) {
         deletes.add(connection.removeBlob(containerName, md.getName()));
      }
      for (Future<Void> isdeleted : deletes) {
         try {
            isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException("Error deleting blob in container: " + containerName, e);
         }
      }
   }

}