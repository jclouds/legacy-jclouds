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

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.util.Utils;

import com.google.inject.Inject;

/**
 * Key-value implementations of BlobStore, such as S3, do not have directories. In following the
 * rackspace cloud files project, we use an empty object '#{dirpath}' with content type set to
 * 'application/directory'.
 * 
 * @author Adrian Cole
 */
@Singleton
public class MarkerFileMkdirStrategy implements MkdirStrategy {

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX)
   protected String directorySuffix = "";

   public void execute(AsyncBlobStore connection, String containerName, String directory) {
      try {
         if (!connection.directoryExists(containerName, directory).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS)) {
            Blob blob = connection.newBlob(directory + directorySuffix);
            blob.setPayload("");
            blob.getMetadata().setContentType("application/directory");
            connection.putBlob(containerName, blob).get(requestTimeoutMilliseconds,
                     TimeUnit.MILLISECONDS);
         }
      } catch (Exception e) {
         e = Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         if (!(e instanceof KeyNotFoundException))
            throw new BlobRuntimeException("Error creating marker directory: " + containerName
                     + "/" + directory, e);
      }
   }
}