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

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.util.Utils;

import com.google.common.collect.Sets;

/**
 * Deletes all keys in the container
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeleteAllKeysInList implements ClearListStrategy, ClearContainerStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   protected final BlobStore connection;

   @Inject
   DeleteAllKeysInList(BlobStore connection, ListBlobMetadataStrategy getAllBlobMetadata) {
      this.connection = connection;
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public void execute(String containerName) {
      execute(containerName, null);
   }

   public void execute(final String containerName, ListContainerOptions options) {
      Set<Future<Void>> deletes = Sets.newHashSet();
      for (ResourceMetadata md : getAllBlobMetadata.execute(containerName, options)) {
         if (md.getType() == ResourceType.BLOB)
            deletes.add(connection.removeBlob(containerName, md.getName()));
      }
      for (Future<Void> isdeleted : deletes) {
         try {
            isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         } catch (Exception e) {
            Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
            throw new BlobRuntimeException("Error deleting blob in container: " + containerName, e);
         }
      }
   }

}