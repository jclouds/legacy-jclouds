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

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.IfDirectoryReturnNameStrategy;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MarkersIfDirectoryReturnNameStrategy implements IfDirectoryReturnNameStrategy {
   @Override
   public String execute(StorageMetadata metadata) {
      switch (metadata.getType()) {
      case CONTAINER:
      case FOLDER:
      case RELATIVE_PATH:
         return metadata.getName();
      case BLOB:
         BlobMetadata blobMd = (BlobMetadata) metadata;
         for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
            if (metadata.getName().endsWith(suffix)) {
               return metadata.getName().substring(0, metadata.getName().lastIndexOf(suffix));
            }
         }
         // It is important that this is last, in case there is a file with a known directory
         // suffix who also has content type set to application/directory
         if (blobMd.getContentMetadata().getContentType() != null
               && blobMd.getContentMetadata().getContentType().equals("application/directory"))
            return metadata.getName();
      }
      return null;
   }
}
