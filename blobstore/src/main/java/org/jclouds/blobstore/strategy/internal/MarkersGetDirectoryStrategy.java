/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.blobstore.strategy.internal;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.ResourceMetadataToRelativePathResourceMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;

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

   protected final ResourceMetadataToRelativePathResourceMetadata resource2Directory;
   private final BlobStore connection;

   @Inject
   public MarkersGetDirectoryStrategy(BlobStore connection,
            ResourceMetadataToRelativePathResourceMetadata resource2Directory) {
      this.connection = connection;
      this.resource2Directory = resource2Directory;
   }

   public StorageMetadata execute(String containerName, String directory) {
      BlobMetadata md = connection.blobMetadata(containerName, directory);
      if (md != null && md.getContentMetadata().getContentType().equals("application/directory"))
         return resource2Directory.apply(md);
      for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
         md = connection.blobMetadata(containerName, directory + suffix);
         if (md != null)
            return resource2Directory.apply(md);
      }
      return null;
   }
}
