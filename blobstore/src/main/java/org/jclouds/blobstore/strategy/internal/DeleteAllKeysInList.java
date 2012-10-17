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
package org.jclouds.blobstore.strategy.internal;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
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

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;

/**
 * Deletes all keys in the container
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeleteAllKeysInList implements ClearListStrategy, ClearContainerStrategy {
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final BackoffLimitedRetryHandler retryHandler;
   protected final ExecutorService userExecutor;

   protected final AsyncBlobStore connection;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   public DeleteAllKeysInList(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
                              AsyncBlobStore connection,
                              BackoffLimitedRetryHandler retryHandler) {

      this.userExecutor = userExecutor;
      this.connection = connection;
      this.retryHandler = retryHandler;
   }

   public void execute(String containerName) {
      execute(containerName, recursive());
   }

   public void execute(final String containerName, ListContainerOptions options) {
      String message = options.getDir() != null ? String.format("clearing path %s/%s",
               containerName, options.getDir()) : String.format("clearing container %s",
               containerName);
      options = options.clone();
      if (options.isRecursive())
         message = message + " recursively";
      Map<StorageMetadata, Exception> exceptions = Maps.newHashMap();
      PageSet<? extends StorageMetadata> listing;
      int maxErrors = 3; // TODO parameterize
      for (int numErrors = 0; numErrors < maxErrors; ) {
         // fetch partial directory listing
         try {
            listing = connection.list(containerName, options).get();
         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            break;
         } catch (ExecutionException ee) {
            ++numErrors;
            if (numErrors == maxErrors) {
               throw Throwables.propagate(ee.getCause());
            }
            retryHandler.imposeBackoffExponentialDelay(numErrors, message);
            continue;
         }

         // recurse on subdirectories
         if (options.isRecursive()) {
            for (StorageMetadata md : listing) {
               String fullPath = parentIsFolder(options, md) ? options.getDir() + "/"
                        + md.getName() : md.getName();
               switch (md.getType()) {
                  case BLOB:
                     break;
                  case FOLDER:
                  case RELATIVE_PATH:
                     if (options.isRecursive() && !fullPath.equals(options.getDir())) {
                        execute(containerName, options.clone().inDirectory(fullPath));
                     }
                     break;
                  case CONTAINER:
                     throw new IllegalArgumentException("Container type not supported");
               }
            }
         }

         // remove blobs and now-empty subdirectories
         Map<StorageMetadata, Future<?>> responses = Maps.newHashMap();
         for (StorageMetadata md : listing) {
            String fullPath = parentIsFolder(options, md) ? options.getDir() + "/"
                     + md.getName() : md.getName();
            switch (md.getType()) {
               case BLOB:
                  responses.put(md, connection.removeBlob(containerName, fullPath));
                  break;
               case FOLDER:
                  if (options.isRecursive()) {
                     responses.put(md, connection.deleteDirectory(containerName, fullPath));
                  }
                  break;
               case RELATIVE_PATH:
                  if (options.isRecursive()) {
                     responses.put(md, connection.deleteDirectory(containerName, md.getName()));
                  }
                  break;
               case CONTAINER:
                  throw new IllegalArgumentException("Container type not supported");
            }
         }

         exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, message);
         if (!exceptions.isEmpty()) {
            ++numErrors;
            retryHandler.imposeBackoffExponentialDelay(numErrors, message);
            continue;
         }

         String marker = listing.getNextMarker();
         if (marker == null) {
            break;
         }
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
      return (options.getDir() != null && md.getName().indexOf('/') == -1);
   }
}
