/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmosonline.saas.blobstore.internal.BaseAtmosBlobStore;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Utils;

import com.google.common.base.Supplier;

public class AtmosBlobStore extends BaseAtmosBlobStore implements BlobStore {

   @Inject
   public AtmosBlobStore(AtmosStorageAsyncClient async, AtmosStorageClient sync,
            Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               container2ResourceList, service);
   }

   /**
    * This implementation uses the AtmosStorage HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.headFile(container + "/" + key));
   }

   public void clearContainer(final String container) {

      clearContainerStrategy.execute(container, recursive());
   }

   public boolean createContainer(String container) {
      sync.createDirectory(container);
      return true;// no etag
   }

   public void deleteContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
      deleteAndEnsurePathGone(container);
   }

   private void deleteAndEnsurePathGone(final String path) {
      sync.deletePath(path);
      try {
         if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
            public Boolean get() {
               return !sync.pathExists(path);
            }
         }, 30000)) {
            throw new IllegalStateException(path + " still exists after deleting!");
         }
      } catch (InterruptedException e) {
         new IllegalStateException(path + " interrupted during deletion!", e);
      }
   }

   public boolean exists(String container) {
      return sync.pathExists(container);
   }

   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.readFile(container + "/" + key, httpOptions));
   }

   public ListResponse<? extends ResourceMetadata> list() {
      return container2ResourceList.apply(sync.listDirectories());
   }

   public ListContainerResponse<? extends ResourceMetadata> list(String container,
            org.jclouds.blobstore.options.ListContainerOptions... optionsList) {
      if (optionsList.length == 1) {
         if (optionsList[0].isRecursive()) {
            throw new UnsupportedOperationException("recursive not currently supported in emcsaas");
         }
         if (optionsList[0].getPath() != null) {
            container = container + "/" + optionsList[0].getPath();
         }
      }
      ListOptions nativeOptions = container2ContainerListOptions.apply(optionsList);
      return container2ResourceList.apply(sync.listDirectory(container, nativeOptions));
   }

   /**
    * Since there is no etag support in atmos, we just return the path.
    */
   public String putBlob(final String container, final Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();
      deleteAndEnsurePathGone(path);
      if (blob.getMetadata().getContentMD5() != null)
         blob.getMetadata().getUserMetadata().put("content-md5",
                  HttpUtils.toHexString(blob.getMetadata().getContentMD5()));
      sync.createFile(container, blob2Object.apply(blob));
      return path;
   }

   public void removeBlob(String container, String key) {
      sync.deletePath(container + "/" + key);
   }

}
