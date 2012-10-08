/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */

package org.jclouds.aws.s3.blobstore.strategy.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.jclouds.Constants;
import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.DeleteAllKeysInList;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.s3.S3;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.ListBucketOptions;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

/**
 * Use the multi-object delete API call to delete objects in batches of up to 1000 keys
 *
 * @author Andrei Savu
 */
public class DeleteObjectsInBatches extends DeleteAllKeysInList {

   public static final int MAX_ERROR_COUNT = 3;

   /**
    * The maximum number of items that can be deleted in a single call
    */
   public static final int MAX_PAGE_SIZE = 1000;
   /**
    * Wait for async operations to complete when this limit is exceeded
    */
   public static final int MAX_NUMBER_OF_OUTSTANDING_CALLS = 500;

   private final AWSS3Client s3Client;
   private final AWSS3AsyncClient asyncS3Client;

   private final Function<Iterable<ObjectMetadata>, Set<String>> objectMetadataPageToSetOfKeys =
      new Function<Iterable<ObjectMetadata>, Set<String>>() {
         @Override
         public Set<String> apply(Iterable<ObjectMetadata> objects) {
            return Sets.newHashSet(Iterables.transform(objects, new Function<ObjectMetadata, String>() {
               @Override
               public String apply(ObjectMetadata obj) {
                  return obj.getKey();
               }
            }));
         }
      };

   @Inject
   DeleteObjectsInBatches(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
                          AsyncBlobStore blobstore, AWSS3Client s3Client,
                          AWSS3AsyncClient asyncS3Client, BackoffLimitedRetryHandler retryHandler) {
      super(userExecutor, blobstore, retryHandler);

      this.s3Client = checkNotNull(s3Client, "s3Client is null");
      this.asyncS3Client = checkNotNull(asyncS3Client, "asyncS3Client is null");
   }

   @Override
   public void execute(String containerName, ListContainerOptions options) {
      String message = String.format("clearing container %s", containerName);

      final ListBucketOptions bucketOptions = new ListBucketOptions().maxResults(MAX_PAGE_SIZE);

      Map<Integer, Future<?>> responses = Maps.newHashMap();
      int index = 0;

      for (IterableWithMarker<ObjectMetadata> objects : S3.listBucket(s3Client, containerName, bucketOptions)) {
         Set<String> keys = objectMetadataPageToSetOfKeys.apply(objects);
         if (keys != null && keys.size() == 0) break;

         responses.put(index, asyncS3Client.deleteObjects(containerName, keys));

         index += 1;
         if (index % MAX_NUMBER_OF_OUTSTANDING_CALLS == 0) {
            awaitCompletionOfAsyncDeleteRequests(message, responses);
            responses.clear();
         }
      }

      awaitCompletionOfAsyncDeleteRequests(message, responses);
   }

   private void awaitCompletionOfAsyncDeleteRequests(String message, Map<Integer, Future<?>> responses) {
      int errorCount = 0;
      while (true) {
         Map<Integer, Exception> exceptions;
         try {
            exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, message);
         } catch (TimeoutException te) {
            ++errorCount;
            if (errorCount == MAX_ERROR_COUNT) {
               throw propagate(te);
            }
            retryHandler.imposeBackoffExponentialDelay(errorCount, message);
            continue;   /* try one more time */
         }

         if (exceptions != null && !exceptions.isEmpty()) {
            throw new BlobRuntimeException(String.format("error %s: %s", message, exceptions));
         }
         break;   /* the operation completed successfully */
      }
   }
}
