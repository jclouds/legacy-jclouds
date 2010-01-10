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

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Retrieves all blobs in the blobstore by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListBlobMetadataInContainer implements ListBlobMetadataStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final AsyncBlobStore connection;

   @Inject
   ListBlobMetadataInContainer(AsyncBlobStore connection) {
      this.connection = connection;
   }

   public SortedSet<? extends BlobMetadata> execute(String container, ListContainerOptions options) {
      try {
         ListResponse<? extends ResourceMetadata> resources = connection.list(container, options)
                  .get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         SortedSet<BlobMetadata> blobM = Sets.newTreeSet();
         for (ResourceMetadata from : resources) {
            if (from.getType() == ResourceType.BLOB)
               blobM.add((BlobMetadata) from);
         }
         return blobM;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException("Error getting resource metadata in container: "
                  + container, e);
      }
   }

}