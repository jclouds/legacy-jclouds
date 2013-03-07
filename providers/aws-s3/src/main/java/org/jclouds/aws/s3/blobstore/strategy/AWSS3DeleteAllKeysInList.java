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
package org.jclouds.aws.s3.blobstore.strategy;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.s3.AWSS3ApiMetadata;
import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Deletes all keys in the container
 *
 * @author Shrinand Javadekar
 */
@Singleton
public class AWSS3DeleteAllKeysInList implements ClearListStrategy, ClearContainerStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final BackoffLimitedRetryHandler retryHandler;
   private final ListeningExecutorService userExecutor;

   protected final AsyncBlobStore connection;
   /** Maximum duration in milliseconds of a request. */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime = Long.MAX_VALUE;

   @Inject
   public AWSS3DeleteAllKeysInList(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
         AsyncBlobStore connection, BackoffLimitedRetryHandler retryHandler) {
      this.userExecutor = userExecutor;
      this.connection = connection;
      this.retryHandler = retryHandler;
   }

   @Override
   public void execute(String containerName) {
      execute(containerName, recursive());
   }

   @Override
   public void execute(final String containerName, ListContainerOptions options) {
      String message = options.getDir() != null ? String.format("clearing path %s/%s",
               containerName, options.getDir()) : String.format("clearing container %s",
               containerName);
      options = options.clone();
      if (options.isRecursive())
         message += " recursively";
      logger.debug(message);
      Map<Set<String>, Exception> exceptions = Maps.newHashMap();
      Map<Set<String>, ListenableFuture<?>> responses = Maps.newHashMap();

      int maxErrors = 3; // TODO parameterize
      for (int numErrors = 0; numErrors < maxErrors; ) {
         // fetch partial directory listing
         PageSet<? extends StorageMetadata> listing;
         ListenableFuture<PageSet<? extends StorageMetadata>> listFuture =
               connection.list(containerName, options);

         try {
            listing = listFuture.get(maxTime, TimeUnit.MILLISECONDS);
         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            break;
         } catch (ExecutionException ee) {
            ++numErrors;
            if (numErrors == maxErrors) {
               throw propagate(ee.getCause());
            }
            retryHandler.imposeBackoffExponentialDelay(numErrors, message);
            continue;
         } catch (TimeoutException te) {
            ++numErrors;
            if (numErrors == maxErrors) {
               throw propagate(te);
            }
            retryHandler.imposeBackoffExponentialDelay(numErrors, message);
            continue;
         } finally {
            listFuture.cancel(true);
         }

         int count = 0;
         ImmutableSet.Builder<String> builder = ImmutableSet.builder();
         Set<String> keys = null;

         // AWS S3 currently supports multi-delete of 1000 keys in one request.
         // Ref: http://docs.aws.amazon.com/AmazonS3/latest/API/multiobjectdeleteapi.html
         int maxMultiDeleteKeys = 1000;

         for (StorageMetadata md : listing) {
        	 String fullPath = null;

        	 switch (md.getType()) {
        	 	case FOLDER:
        	 	case RELATIVE_PATH:
        	 		fullPath = md.getName() + "/";
        	 		break;
        	 	case BLOB:
        	 		fullPath = parentIsFolder(options, md) ? options.getDir() + "/"
        	 				+ md.getName() : md.getName();
        	 		break;
        	 	case CONTAINER:
        	 		throw new IllegalArgumentException("Container type not supported");
        	    default:
        	    	throw new UnsupportedOperationException("Multi-delete can currently only process folders, relative paths, blobs or containers");
        	 }

     		 builder.add(fullPath);
       		 count++;

        	 if (count % maxMultiDeleteKeys == 0) {
        		 keys = builder.build();
        		 deleteKeys(containerName, keys, responses);

	       		 // Create a new builder object for the next set of keys.
	       		 builder = ImmutableSet.builder();
        	 }
         }

         // There may be keys added to the builder that haven't been deleted yet. Do that now.
         keys = builder.build();
         if (!keys.isEmpty()) {
        	 deleteKeys(containerName, keys, responses);
         }

         try {
             exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, message);
         } catch (TimeoutException te) {
             ++numErrors;
             if (numErrors == maxErrors) {
                throw propagate(te);
             }
             retryHandler.imposeBackoffExponentialDelay(numErrors, message);
             continue;
         } finally {
             for (ListenableFuture<?> future : responses.values()) {
                future.cancel(true);
             }
         }

         if (!exceptions.isEmpty()) {
             ++numErrors;
             if (numErrors == maxErrors) {
                break;
             }
             retryHandler.imposeBackoffExponentialDelay(numErrors, message);
             continue;
         }

         String marker = listing.getNextMarker();
         if (marker == null) {
            break;
         }

         logger.debug("%s with marker %s", message, marker);
         options = options.afterMarker(marker);

         // Reset numErrors if we execute a successful iteration.  This ensures
         // that we only try an unsuccessful operation maxErrors times but
         // allow progress with directories containing many blobs in the face
         // of some failures.
         numErrors = 0;
      }

      if (!exceptions.isEmpty())
         throw new BlobRuntimeException(String.format("error %s: %s", message, exceptions));
   }

   private boolean parentIsFolder(final ListContainerOptions options, final StorageMetadata md) {
      return options.getDir() != null && md.getName().indexOf('/') == -1;
   }

   private void deleteKeys(final String containerName, final Set<String> keys, Map<Set<String>, ListenableFuture<?>> responses) {
 		 AWSS3AsyncClient asyncClient = connection.getContext().unwrap(AWSS3ApiMetadata.CONTEXT_TOKEN).getAsyncApi();
 		 ListenableFuture<?> deleteFuture = asyncClient.deleteObjects(containerName, keys);
 		 responses.put(keys, deleteFuture);
   }
}
