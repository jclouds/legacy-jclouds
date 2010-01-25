/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.blobstore.strategy.internal;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Retrieves all blobs in the blobstore under the current path, by the most efficient means
 * possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllBlobsInListAndRetryOnFailure implements GetBlobsInListStrategy {

   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   protected final AsyncBlobStore connection;
   protected final BackoffLimitedRetryHandler retryHandler;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_HTTP_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   GetAllBlobsInListAndRetryOnFailure(AsyncBlobStore connection,
            ListBlobMetadataStrategy getAllBlobMetadata, BackoffLimitedRetryHandler retryHandler) {
      this.connection = connection;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.retryHandler = retryHandler;
   }

   public Set<? extends Blob> execute(String container, ListContainerOptions options) {
      Set<Blob> objects = Sets.newHashSet();
      Map<String, ListenableFuture<? extends Blob>> futureObjects = Maps.newHashMap();
      for (BlobMetadata md : getAllBlobMetadata.execute(container, options)) {
         futureObjects.put(md.getName(), connection.getBlob(container, md.getName()));
      }
      for (Entry<String, ListenableFuture<? extends Blob>> futureObjectEntry : futureObjects
               .entrySet()) {
         try {
            ifNotFoundRetryOtherwiseAddToSet(container, futureObjectEntry.getKey(),
                     futureObjectEntry.getValue(), objects);
         } catch (Exception e) {
            Throwables.propagateIfPossible(e, BlobRuntimeException.class);
            throw new BlobRuntimeException(String.format("Error getting value from blob %1$s",
                     container), e);
         }

      }
      return objects;
   }

   @VisibleForTesting
   public void ifNotFoundRetryOtherwiseAddToSet(String container, String key,
            ListenableFuture<? extends Blob> value, Set<Blob> objects) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (int i = 0; i < 3; i++) {
         Blob object = (maxTime != null) ? value.get(maxTime, TimeUnit.MILLISECONDS) : value.get();
         if (object == null) {
            retryHandler.imposeBackoffExponentialDelay(i + 1, String.format("blob %s/%s not found",
                     container, key));
            value = connection.getBlob(container, key);
            continue;
         }
         object.getMetadata().setName(key);
         objects.add(object);
         return;
      }
   }
}