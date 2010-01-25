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

import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Deletes all keys in the container
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeleteAllKeysInList implements ClearListStrategy, ClearContainerStrategy {
   @Resource
   protected Logger logger = Logger.NULL;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   private final ExecutorService userExecutor;

   protected final AsyncBlobStore connection;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_HTTP_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   DeleteAllKeysInList(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            AsyncBlobStore connection, ListBlobMetadataStrategy getAllBlobMetadata) {
      this.userExecutor = userExecutor;
      this.connection = connection;
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public void execute(String containerName) {
      execute(containerName, null);
   }

   public void execute(final String containerName, ListContainerOptions options) {
      Set<ListenableFuture<Void>> responses = Sets.newHashSet();
      for (StorageMetadata md : getAllBlobMetadata.execute(containerName, options)) {
         if (md.getType() == StorageType.BLOB)
            responses.add(connection.removeBlob(containerName, md.getName()));
      }
      awaitCompletion(responses, userExecutor, maxTime, logger, String.format(
               "deleting from containerName: %s", containerName));
   }

}