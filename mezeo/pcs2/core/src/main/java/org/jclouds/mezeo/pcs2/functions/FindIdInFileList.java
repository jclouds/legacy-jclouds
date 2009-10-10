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
package org.jclouds.mezeo.pcs2.functions;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Key;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.HttpException;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

public class FindIdInFileList implements Function<Key, String> {
   private BlobStore<ContainerMetadata, FileMetadata, PCSFile> connection;

   @Inject
   public FindIdInFileList(BlobStore<ContainerMetadata, FileMetadata, PCSFile> connection) {
      this.connection = connection;
   }

   public String apply(Key key) {
      key = BlobStoreUtils.parseKey(key);
      SortedSet<FileMetadata> response;
      try {
         response = connection.listBlobs(key.getContainer()).get(10, TimeUnit.SECONDS);
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new HttpException("could not list blobs for " + Arrays.asList(key.getContainer()), e);
      }
      return idForNameInListOrException(key.getContainer(), key.getKey(), response);
   }

   @VisibleForTesting
   String idForNameInListOrException(String container, String toFind,
            SortedSet<FileMetadata> response) {
      for (FileMetadata data : response) {
         if (toFind.equals(data.getKey())) {
            String path = data.getUrl().getPath();
            int indexAfterContainersSlash = path.indexOf("files/") + "files/".length();
            return path.substring(indexAfterContainersSlash);
         }
      }
      throw new KeyNotFoundException(container, toFind);
   }

}