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
package org.jclouds.azure.storage.blob.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.compose;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobMetadataToBlobProperties;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsOptionsToListOptions;
import org.jclouds.azure.storage.blob.blobstore.functions.ResourceToListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ContainerProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.internal.ContainerPropertiesImpl;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.domain.internal.BoundedHashSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link AzureBlobAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubAzureBlobAsyncClient implements AzureBlobAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final TransientAsyncBlobStore blobStore;
   private final AzureBlob.Factory objectProvider;
   private final AzureBlobToBlob object2Blob;
   private final BlobToAzureBlob blob2Object;
   private final BlobMetadataToBlobProperties blob2ObjectInfo;
   private final ListBlobsOptionsToListOptions container2ContainerListOptions;
   private final ResourceToListBlobsResponse resource2ObjectList;
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;

   @Inject
   private StubAzureBlobAsyncClient(TransientAsyncBlobStore blobStore,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            AzureBlob.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, AzureBlobToBlob object2Blob,
            BlobToAzureBlob blob2Object, BlobMetadataToBlobProperties blob2ObjectInfo,
            ListBlobsOptionsToListOptions container2ContainerListOptions,
            ResourceToListBlobsResponse resource2ContainerList) {
      this.containerToBlobs = containerToBlobs;
      this.blobStore = blobStore;
      this.objectProvider = objectProvider;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectInfo = checkNotNull(blob2ObjectInfo, "blob2ObjectInfo");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.resource2ObjectList = checkNotNull(resource2ContainerList, "resource2ContainerList");
   }

   public ListenableFuture<Boolean> createContainer(String container,
            CreateContainerOptions... options) {
      return blobStore.createContainerInLocation(null, container);
   }

   public ListenableFuture<Boolean> createRootContainer(CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> deleteBlob(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

   public ListenableFuture<Void> deleteContainer(final String container) {
      StubAzureBlobAsyncClient.this.containerToBlobs.remove(container);
      return immediateFuture(null);
   }

   public ListenableFuture<Void> deleteRootContainer() {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<AzureBlob> getBlob(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return compose(blobStore.getBlob(container, key, getOptions), blob2Object);
   }

   public ListenableFuture<BlobProperties> getBlobProperties(String container, String key) {
      return compose(blobStore.blobMetadata(container, key),
               new Function<BlobMetadata, BlobProperties>() {

                  @Override
                  public BlobProperties apply(BlobMetadata from) {
                     return blob2ObjectInfo.apply(from);
                  }

               });
   }

   public ListenableFuture<ContainerProperties> getContainerProperties(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<ListBlobsResponse> listBlobs(String container,
            ListBlobsOptions... optionsList) {
      org.jclouds.blobstore.options.ListContainerOptions options = container2ContainerListOptions
               .apply(optionsList);
      return compose(blobStore.list(container, options), resource2ObjectList);
   }

   public ListenableFuture<ListBlobsResponse> listBlobs(ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<? extends BoundedSet<ContainerProperties>> listContainers(
            ListOptions... listOptions) {
      return immediateFuture(new BoundedHashSet<ContainerProperties>(Iterables.transform(blobStore
               .getContainerToBlobs().keySet(), new Function<String, ContainerProperties>() {
         public ContainerProperties apply(String name) {
            return new ContainerPropertiesImpl(URI.create("http://stub/" + name), new Date(), "",
                     Maps.<String, String> newHashMap());
         }

      }), null, null, null, null, null));
   }

   public AzureBlob newBlob() {
      return objectProvider.create(null);
   }

   public ListenableFuture<String> putBlob(String container, AzureBlob object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public ListenableFuture<Void> setBlobMetadata(String container, String key,
            Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> setResourceMetadata(String container, Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Boolean> containerExists(final String container) {
      return immediateFuture(blobStore.getContainerToBlobs().containsKey(container));
   }

   @Override
   public ListenableFuture<Boolean> blobExists(String container, String name) {
      return immediateFuture(containerToBlobs.get(container).containsKey(name));
   }

}
