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

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.internal.BaseBlobMap.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobsStrategy;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Retrieves all blobs in the blobstore by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
public class RetryOnNotFoundGetAllBlobsStrategy<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements GetAllBlobsStrategy<C, M, B> {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   public long requestTimeoutMilliseconds = 30000;
   protected final GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata;

   /**
    * time to pause before retrying a transient failure
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY)
   protected long requestRetryMilliseconds = 10;

   @Inject
   RetryOnNotFoundGetAllBlobsStrategy(GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata) {
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public SortedSet<B> execute(BlobStore<C, M, B> connection, String container) {
      SortedSet<B> objects = Sets.<B> newTreeSet();
      Map<String, Future<B>> futureObjects = Maps.newHashMap();
      for (M md : getAllBlobMetadata.execute(connection, container)) {
         futureObjects.put(md.getKey(), connection.getBlob(container, md.getKey()));
      }
      for (Entry<String, Future<B>> futureObjectEntry : futureObjects.entrySet()) {
         try {
            ifNotFoundRetryOtherwiseAddToSet(futureObjectEntry.getKey(), futureObjectEntry
                     .getValue(), objects);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException(String.format("Error getting value from blob %1$s",
                     container), e);
         }

      }
      return objects;
   }

   @VisibleForTesting
   public void ifNotFoundRetryOtherwiseAddToSet(String key, Future<B> value, Set<B> objects)
            throws InterruptedException, ExecutionException, TimeoutException {
      for (int i = 0; i < 3; i++) {
         try {
            B object = value.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
            object.getMetadata().setKey(key);
            objects.add(object);
            return;
         } catch (KeyNotFoundException e) {
            Thread.sleep(requestRetryMilliseconds);
         }
      }
   }

}