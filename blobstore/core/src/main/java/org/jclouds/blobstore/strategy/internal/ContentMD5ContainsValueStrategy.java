/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import java.util.Arrays;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.strategy.ContainsValueStrategy;
import org.jclouds.blobstore.strategy.GetAllBlobMetadataStrategy;
import org.jclouds.util.Utils;

/**
 * Searches Content-MD5 tag for the value associated with the value
 * 
 * @author Adrian Cole
 */
public class ContentMD5ContainsValueStrategy<C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         implements ContainsValueStrategy<C, M, B> {

   protected final ObjectMD5<M, B> objectMD5;
   protected final GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata;

   @Inject
   private ContentMD5ContainsValueStrategy(ObjectMD5<M, B> objectMD5,
            GetAllBlobMetadataStrategy<C, M, B> getAllBlobMetadata) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
   }

   public boolean execute(BlobStore<C, M, B> connection, String containerName, Object value) {
      try {
         byte[] toSearch = objectMD5.apply(value);
         for (BlobMetadata metadata : getAllBlobMetadata.execute(connection, containerName)) {
            if (Arrays.equals(toSearch, metadata.getContentMD5()))
               return true;
         }
         return false;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format(
                  "Error searching for ETAG of value: [%2$s] in container:%1$s", containerName,
                  value), e);
      }
   }

}