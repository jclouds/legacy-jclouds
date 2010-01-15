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
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.ResourceMetadataToRelativePathResourceMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;

import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * Key-value implementations of BlobStore, such as S3, do not have directories. In following the
 * rackspace cloud files project, we use an empty object '#{dirpath}' with content type set to
 * 'application/directory'.
 * 
 * <p/>
 * To interoperate with other S3 tools, we accept the following ways to tell if the directory
 * exists:
 * <ul>
 * <li>an object named '#{dirpath}_$folder$' or '#{dirpath}/' denoting a directory marker</li>
 * <li>an object with content type set to 'application/directory' denoting a directory marker</li>
 * <li>if there exists any objects with the prefix "#{dirpath}/", then the directory is said to
 * exist</li>
 * <li>if both a file with the name of a directory and a marker for that directory exists, then the
 * *file masks the directory*, and the directory is never returned.</li>
 * </ul>
 * 
 * @see MarkerFileMkdirStrategy
 * @author Adrian Cole
 */
@Singleton
public class MarkersGetDirectoryStrategy implements GetDirectoryStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final ResourceMetadataToRelativePathResourceMetadata resource2Directory;

   @Inject
   public MarkersGetDirectoryStrategy(ResourceMetadataToRelativePathResourceMetadata resource2Directory) {
      this.resource2Directory = resource2Directory;
   }

   public StorageMetadata execute(AsyncBlobStore connection, String containerName, String directory) {
      for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
         try {
            return resource2Directory.apply(connection.blobMetadata(containerName,
                     directory + suffix).get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS));
         } catch (KeyNotFoundException e) {
         } catch (Exception e) {
            Throwables.propagateIfPossible(e, BlobRuntimeException.class);
            if (!(e instanceof KeyNotFoundException))
               throw new BlobRuntimeException("Error determining if a directory exists at: "
                        + containerName + "/" + directory, e);
         }
      }
      throw new KeyNotFoundException(containerName, directory);
   }

}