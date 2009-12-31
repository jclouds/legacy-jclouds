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

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobMetadataToBlobProperties;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsOptionsToListOptions;
import org.jclouds.azure.storage.blob.blobstore.functions.ResourceToListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.BlobProperties;
import org.jclouds.azure.storage.blob.domain.ListBlobsResponse;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.domain.internal.ListableContainerPropertiesImpl;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.domain.internal.BoundedTreeSet;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore.FutureBase;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Implementation of {@link AzureBlobAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubAzureBlobAsyncClient implements AzureBlobAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final LoggerFactory logFactory;
   private final AzureBlob.Factory objectProvider;
   private final AzureBlobToBlob object2Blob;
   private final BlobToAzureBlob blob2Object;
   private final BlobMetadataToBlobProperties blob2ObjectInfo;
   private final ListBlobsOptionsToListOptions container2ContainerListOptions;
   private final ResourceToListBlobsResponse resource2ObjectList;
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;

   @Inject
   private StubAzureBlobAsyncClient(StubAsyncBlobStore blobStore, LoggerFactory logFactory,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            AzureBlob.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, AzureBlobToBlob object2Blob,
            BlobToAzureBlob blob2Object, BlobMetadataToBlobProperties blob2ObjectInfo,
            ListBlobsOptionsToListOptions container2ContainerListOptions,
            ResourceToListBlobsResponse resource2ContainerList) {
      this.logFactory = logFactory;
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

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   public Future<Boolean> createContainer(String container, CreateContainerOptions... options) {
      return blobStore.createContainer(container);
   }

   public Future<Boolean> createRootContainer(CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> deleteBlob(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

   public Future<Void> deleteContainer(final String container) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            StubAzureBlobAsyncClient.this.containerToBlobs.remove(container);
            return null;
         }
      };
   }

   public Future<Boolean> deleteRootContainer() {
      throw new UnsupportedOperationException();
   }

   public Future<AzureBlob> getBlob(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return wrapFuture(blobStore.getBlob(container, key, getOptions), blob2Object);
   }

   public Future<BlobProperties> getBlobProperties(String container, String key) {
      return wrapFuture(blobStore.blobMetadata(container, key),
               new Function<BlobMetadata, BlobProperties>() {

                  @Override
                  public BlobProperties apply(BlobMetadata from) {

                     return blob2ObjectInfo.apply(from);
                  }

               });
   }

   public Future<ListableContainerProperties> getContainerProperties(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<ListBlobsResponse> listBlobs(String container, ListBlobsOptions... optionsList) {
      org.jclouds.blobstore.options.ListContainerOptions options = container2ContainerListOptions
               .apply(optionsList);
      return wrapFuture(blobStore.list(container, options), resource2ObjectList);
   }

   public Future<ListBlobsResponse> listBlobs(ListBlobsOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<? extends BoundedSortedSet<ListableContainerProperties>> listContainers(
            ListOptions... listOptions) {
      return new FutureBase<BoundedSortedSet<ListableContainerProperties>>() {

         public BoundedSortedSet<ListableContainerProperties> get() throws InterruptedException,
                  ExecutionException {
            return new BoundedTreeSet<ListableContainerProperties>(Iterables.transform(blobStore
                     .getContainerToBlobs().keySet(),
                     new Function<String, ListableContainerProperties>() {
                        public ListableContainerProperties apply(String name) {
                           return new ListableContainerPropertiesImpl(URI.create("http://stub/"
                                    + name), new Date(), "");
                        }

                     }), null, null, null, null, null);
         }
      };
   }

   public AzureBlob newBlob() {
      return objectProvider.create(null);
   }

   public Future<String> putBlob(String container, AzureBlob object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public Future<Void> setBlobMetadata(String container, String key, Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public Future<Void> setResourceMetadata(String container, Map<String, String> metadata) {
      throw new UnsupportedOperationException();
   }

   public Future<Boolean> containerExists(final String container) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            return blobStore.getContainerToBlobs().containsKey(container);
         }
      };
   }

}
