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

import static org.jclouds.io.Payloads.newByteArrayPayload;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.MkdirStrategy;

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
   private final BlobStore blobStore;

   @Inject
   MarkerFileMkdirStrategy(BlobStore blobStore) {
      this.blobStore = blobStore;
   }

   public void execute(String containerName, String directory) {
      blobStore.putBlob(
            containerName,
            blobStore.blobBuilder(directory + directorySuffix).type(StorageType.RELATIVE_PATH)
                  .payload(newByteArrayPayload(new byte[] {})).contentType("application/directory").build());
   }
}
