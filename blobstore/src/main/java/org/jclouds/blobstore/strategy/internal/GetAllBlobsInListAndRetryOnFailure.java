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
package org.jclouds.blobstore.strategy.internal;

import static org.jclouds.concurrent.FutureIterables.transformParallel;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobsInContainer;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Retrieves all blobs in the blobstore under the current path, by the most efficient means
 * possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GetAllBlobsInListAndRetryOnFailure implements GetBlobsInListStrategy {

   protected final ListBlobsInContainer getAllBlobMetadata;
   protected final BackoffLimitedRetryHandler retryHandler;
   protected final AsyncBlobStore ablobstore;
   protected final ListeningExecutorService userExecutor;
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   GetAllBlobsInListAndRetryOnFailure(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            ListBlobsInContainer getAllBlobMetadata, AsyncBlobStore ablobstore, BackoffLimitedRetryHandler retryHandler) {
      this.userExecutor = userExecutor;
      this.ablobstore = ablobstore;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.retryHandler = retryHandler;
   }

   public Iterable<Blob> execute(final String container, ListContainerOptions options) {
      Iterable<? extends BlobMetadata> list = getAllBlobMetadata.execute(container, options);
      return transformParallel(list, new Function<BlobMetadata, ListenableFuture<? extends Blob>>() {

         @Override
         public ListenableFuture<Blob> apply(BlobMetadata from) {
            return ablobstore.getBlob(container, from.getName());
         }

      }, userExecutor, maxTime, logger, String.format("getting from containerName: %s", container), retryHandler, 3);

   }
}
