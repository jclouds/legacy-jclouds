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

import java.util.concurrent.ExecutionException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.MkdirStrategy;

import com.google.common.base.Throwables;
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

   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX)
   protected String directorySuffix = "";
   private final AsyncBlobStore connection;

   @Inject
   MarkerFileMkdirStrategy(AsyncBlobStore connection) {
      this.connection = connection;
   }

   public void execute(String containerName, String directory) {
      try {
         if (!connection.directoryExists(containerName, directory).get()) {
            Blob blob = connection.newBlob(directory + directorySuffix);
            blob.setPayload("");
            blob.getMetadata().setContentType("application/directory");
            connection.putBlob(containerName, blob).get();
         }
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      } catch (ExecutionException e) {
         Throwables.propagate(e.getCause());
      }
   }
}