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
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.jclouds.Constants;
import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.DeleteAllKeysInList;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;

import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

/**
 * Use the multi-object delete API call to delete objects in batches of up to 1000 keys
 *
 * @author Andrei Savu
 */
public class DeleteObjectsInBatches extends DeleteAllKeysInList {

   public static final int MAX_ERROR_COUNT = 3;
   private final AWSS3AsyncClient asyncS3Client;

   @Inject
   DeleteObjectsInBatches(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
                          AsyncBlobStore blobstore,
                          AWSS3AsyncClient asyncS3Client,
                          BackoffLimitedRetryHandler retryHandler) {
      super(userExecutor, blobstore, retryHandler);
      this.asyncS3Client = checkNotNull(asyncS3Client, "asyncS3Client is null");
   }

   @Override
   public void execute(String containerName, ListContainerOptions options) {
      String message = options.getDir() != null ?
          String.format("clearing path %s/%s", containerName, options.getDir())
          : String.format("clearing container %s", containerName);

      options = options.clone();
      if (options.isRecursive()) {
         message += " recursively";
      }

      final Map<Integer, Future<?>> responses = Maps.newHashMap();
      int errorCount = 0;

      for (int index = 0; errorCount < MAX_ERROR_COUNT; index++) {
         // Fetch partial directory listing
         PageSet<? extends StorageMetadata> listing;
         try {
            listing = connection.list(containerName, options).get();
            if (listing.size() == 0) {
               break;   /* nothing to do - the result is empty */
            }

         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            break;

         } catch (ExecutionException ee) {
            errorCount++;
            if (errorCount == MAX_ERROR_COUNT) {
               throw Throwables.propagate(ee.getCause());
            }
            retryHandler.imposeBackoffExponentialDelay(errorCount, message);
            continue;
         }

         // A PageSet always has less than 1000 elements and can be removed in a single call
         Set<String> keys = convertPageSetToListOfKeys(listing);
         responses.put(index, asyncS3Client.deleteMultipleObjects(containerName, keys));

         String marker = listing.getNextMarker();
         if (marker == null) {
            break;
         }
         options = options.afterMarker(marker);

         // Reset errorCount if we execute a successful iteration.  This ensures
         // that we only try an unsuccessful operation MAX_ERROR_COUNT times but
         // allow progress with directories containing many blobs in the face
         // of some failures.

         errorCount = 0;
      }

      Map<Integer, Exception> exceptions = awaitCompletion(responses, userExecutor,
          maxTime, logger, message);
      if (!exceptions.isEmpty()) {
         throw new BlobRuntimeException(String.format("error %s: %s", message, exceptions));
      }
   }

   private <T extends StorageMetadata> Set<String> convertPageSetToListOfKeys(PageSet<T> listing) {
      return ImmutableSet.copyOf(Iterables.transform(listing, new Function<T, String>() {
         @Override
         public String apply(T meta) {
            return meta.getName();
         }
      }));
   }
}
