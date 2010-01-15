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

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.IsDirectoryStrategy;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MarkersIsDirectoryStrategy implements IsDirectoryStrategy {

   public boolean execute(StorageMetadata metadata) {
      switch (metadata.getType()) {
         case CONTAINER:
         case FOLDER:
         case RELATIVE_PATH:
            return true;
         case BLOB:
            BlobMetadata blobMd = (BlobMetadata) metadata;
            if (blobMd.getContentType() != null
                     && blobMd.getContentType().equals("application/directory"))
               return true;
            for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
               if (metadata.getName().endsWith(suffix)) {
                  return true;
               }
            }
      }
      return false;
   }
}