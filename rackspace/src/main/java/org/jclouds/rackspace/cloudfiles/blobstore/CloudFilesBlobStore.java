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
package org.jclouds.rackspace.cloudfiles.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceList;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.internal.BaseCloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class CloudFilesBlobStore extends BaseCloudFilesBlobStore implements BlobStore {
   private final CloudFilesAsyncBlobStore aBlobStore;

   @Inject
   public CloudFilesBlobStore(CloudFilesAsyncBlobStore aBlobStore, CloudFilesAsyncClient async,
            CloudFilesClient sync, Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            GetDirectoryStrategy getDirectoryStrategy, MkdirStrategy mkdirStrategy,
            ContainerToResourceMetadata container2ResourceMd,
            ContainerToResourceList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, container2ResourceMd, container2ResourceList,
               service);
      this.aBlobStore = aBlobStore;
   }

   /**
    * This implementation uses the CloudFiles HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.getObjectInfo(container, key));
   }

   public void clearContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
   }

   public boolean createContainer(String container) {
      return sync.createContainer(container);
   }

   public void deleteContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
      sync.deleteContainerIfEmpty(container);
   }

   public boolean containerExists(String path) {
      return sync.containerExists(BlobStoreUtils.parseContainerFromPath(path));
   }

   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getObject(container, key, httpOptions));
   }

   public ListResponse<? extends ResourceMetadata> list() {
      return new Function<SortedSet<ContainerMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
         public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                  SortedSet<ContainerMetadata> from) {
            return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                     container2ResourceMd), null, null, false);
         }
      }.apply(sync.listContainers());
   }

   public ListContainerResponse<? extends ResourceMetadata> list(String container,
            ListContainerOptions... optionsList) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(optionsList);
      return container2ResourceList.apply(sync.listObjects(container, httpOptions));
   }

   public String putBlob(String container, Blob blob) {
      return sync.putObject(container, blob2Object.apply(blob));
   }

   public void removeBlob(String container, String key) {
      sync.removeObject(container, key);
   }

   public boolean directoryExists(String containerName, String directory) {
      try {
         getDirectoryStrategy.execute(aBlobStore, containerName, directory);
         return true;
      } catch (KeyNotFoundException e) {
         return false;
      }
   }

   public void createDirectory(String containerName, String directory) {
      mkdirStrategy.execute(aBlobStore, containerName, directory);
   }

}
