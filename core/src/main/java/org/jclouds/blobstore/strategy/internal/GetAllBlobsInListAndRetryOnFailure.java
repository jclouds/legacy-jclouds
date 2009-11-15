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
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Retrieves all blobs in the blobstore under the current path, by the most efficient means
 * possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllBlobsInListAndRetryOnFailure implements GetBlobsInListStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   public long requestTimeoutMilliseconds = 30000;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   protected final AsyncBlobStore connection;

   /**
    * time to pause before retrying a transient failure
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_RETRY)
   protected long requestRetryMilliseconds = 10;

   @Inject
   GetAllBlobsInListAndRetryOnFailure(AsyncBlobStore connection,
            ListBlobMetadataStrategy getAllBlobMetadata) {
      this.connection = connection;
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public SortedSet<? extends Blob> execute(String container, ListContainerOptions options) {
      SortedSet<Blob> objects = Sets.newTreeSet();
      Map<String, Future<? extends Blob>> futureObjects = Maps.newHashMap();
      for (BlobMetadata md : getAllBlobMetadata.execute(container, options)) {
         futureObjects.put(md.getName(), connection.getBlob(container, md.getName()));
      }
      for (Entry<String, Future<? extends Blob>> futureObjectEntry : futureObjects.entrySet()) {
         try {
            ifNotFoundRetryOtherwiseAddToSet(container, futureObjectEntry.getKey(),
                     futureObjectEntry.getValue(), objects);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException(String.format("Error getting value from blob %1$s",
                     container), e);
         }

      }
      return objects;
   }

   @VisibleForTesting
   public void ifNotFoundRetryOtherwiseAddToSet(String container, String key,
            Future<? extends Blob> value, Set<Blob> objects) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (int i = 0; i < 3; i++) {
         try {
            Blob object = value.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
            object.getMetadata().setName(key);
            objects.add(object);
            return;
         } catch (KeyNotFoundException e) {
            Thread.sleep(requestRetryMilliseconds);
            value = connection.getBlob(container, key);
         }
      }
   }

}