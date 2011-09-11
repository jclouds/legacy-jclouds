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
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ListContainerStrategy;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.concurrent.Future;
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

   protected final ListContainerStrategy listContainer;
   protected final BackoffLimitedRetryHandler retryHandler;
   private final ExecutorService userExecutor;

   protected final AsyncBlobStore connection;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   DeleteAllKeysInList(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            AsyncBlobStore connection, ListContainerStrategy listContainer,
            BackoffLimitedRetryHandler retryHandler) {

      this.userExecutor = userExecutor;
      this.connection = connection;
      this.listContainer = listContainer;
      this.retryHandler = retryHandler;
   }

   public void execute(String containerName) {
      execute(containerName, recursive());
   }

   public void execute(final String containerName, final ListContainerOptions options) {
      String message = options.getDir() != null ? String.format("clearing path %s/%s",
               containerName, options.getDir()) : String.format("clearing container %s",
               containerName);
      if (options.isRecursive())
         message = message + " recursively";
      Map<StorageMetadata, Exception> exceptions = Maps.newHashMap();
      Iterable<? extends StorageMetadata> toDelete = getResourcesToDelete(containerName, options);
      for (int i = 0; i < 3; i++) { // TODO parameterize
         Map<StorageMetadata, Future<?>> responses = Maps.newHashMap();
         try {
            for (final StorageMetadata md : toDelete) {
               String fullPath = parentIsFolder(options, md) ? options.getDir() + "/"
                        + md.getName() : md.getName();
               switch (md.getType()) {
                  case BLOB:
                     responses.put(md, connection.removeBlob(containerName, fullPath));
                     break;
                  case FOLDER:
                     if (options.isRecursive() && !fullPath.equals(options.getDir())) {
                        execute(containerName, options.clone().inDirectory(fullPath));
                     }
                     responses.put(md, connection.deleteDirectory(containerName, fullPath));
                     break;
                  case RELATIVE_PATH:
                     if (options.isRecursive() && !fullPath.equals(options.getDir())) {
                        execute(containerName, options.clone().inDirectory(fullPath));
                     }
                     responses.put(md, connection.deleteDirectory(containerName, md.getName()));
                     break;
                  case CONTAINER:
                     throw new IllegalArgumentException("Container type not supported");
               }
            }
         } finally {
            exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, message);
            toDelete = getResourcesToDelete(containerName, options);
            if (Iterables.size(toDelete) == 0) {
               break;
            }
            if (exceptions.size() > 0) {
               toDelete = Iterables.concat(exceptions.keySet(), toDelete);
               retryHandler.imposeBackoffExponentialDelay(i + 1, message);
            }
         }
      }
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("error %s: %s", message, exceptions));
      assert Iterables.size(toDelete) == 0 : String.format("items remaining %s: %s", message,
               toDelete);
   }

   private boolean parentIsFolder(final ListContainerOptions options, final StorageMetadata md) {
      return (options.getDir() != null && md.getName().indexOf('/') == -1);
   }

   private Iterable<? extends StorageMetadata> getResourcesToDelete(final String containerName,
            final ListContainerOptions options) {
      Iterable<? extends StorageMetadata> toDelete = Iterables.filter(listContainer.execute(
               containerName, options), new Predicate<StorageMetadata>() {

         @Override
         public boolean apply(StorageMetadata input) {
            switch (input.getType()) {
               case BLOB:
                  return true;
               case FOLDER:
               case RELATIVE_PATH:
                  if (options.isRecursive())
                     return true;
                  break;
            }
            return false;
         }

      });
      return toDelete;
   }

}