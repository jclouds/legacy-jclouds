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
package org.jclouds.atmosonline.saas.blobstore.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutorService;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.inject.Inject;

public class BaseAtmosBlobStore {
   protected final AtmosStorageAsyncClient async;
   protected final AtmosStorageClient sync;
   protected final Blob.Factory blobFactory;
   protected final LoggerFactory logFactory;
   protected final ClearListStrategy clearContainerStrategy;
   protected final ObjectToBlobMetadata object2BlobMd;
   protected final ObjectToBlob object2Blob;
   protected final BlobToObject blob2Object;
   protected final BlobStoreListOptionsToListOptions container2ContainerListOptions;
   protected final BlobToHttpGetOptions blob2ObjectGetOptions;
   protected final DirectoryEntryListToResourceMetadataList container2ResourceList;
   protected final ExecutorService service;

   @Inject
   protected BaseAtmosBlobStore(AtmosStorageAsyncClient async, AtmosStorageClient sync,
            Blob.Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList, ExecutorService service) {
      this.async = checkNotNull(async, "async");
      this.sync = checkNotNull(sync, "sync");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.logFactory = checkNotNull(logFactory, "logFactory");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.service = checkNotNull(service, "service");
   }

   public Blob newBlob(String name) {
      Blob blob = blobFactory.create(null);
      blob.getMetadata().setName(name);
      return blob;
   }

   protected String adjustContainerIfDirOptionPresent(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      if (options != org.jclouds.blobstore.options.ListContainerOptions.NONE) {
         if (options.isRecursive()) {
            throw new UnsupportedOperationException("recursive not currently supported in emcsaas");
         }
         if (options.getDir() != null) {
            container = container + "/" + options.getDir();
         }
      }
      return container;
   }

}
