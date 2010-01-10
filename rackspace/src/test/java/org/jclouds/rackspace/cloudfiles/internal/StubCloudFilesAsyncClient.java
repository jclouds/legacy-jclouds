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
package org.jclouds.rackspace.cloudfiles.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.compose;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ListContainerOptionsToBlobStoreListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ResourceToObjectInfo;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ResourceToObjectList;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link CloudFilesAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubCloudFilesAsyncClient implements CloudFilesAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final CFObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ResourceToObjectInfo blob2ObjectInfo;
   private final ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions;
   private final ResourceToObjectList resource2ObjectList;

   @Inject
   private StubCloudFilesAsyncClient(StubAsyncBlobStore blobStore,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            CFObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, ResourceToObjectInfo blob2ObjectInfo,
            ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions,
            ResourceToObjectList resource2ContainerList) {
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

   public ListenableFuture<Boolean> containerExists(final String container) {
      return immediateFuture(blobStore.getContainerToBlobs().containsKey(container));
   }

   public ListenableFuture<Boolean> createContainer(String container) {
      return blobStore.createContainer(container);
   }

   public ListenableFuture<Boolean> deleteContainerIfEmpty(String container) {
      return blobStore.deleteContainerImpl(container);
   }

   public ListenableFuture<Boolean> disableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> enableCDN(String container, long ttl) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> enableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<AccountMetadata> getAccountStatistics() {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<ContainerCDNMetadata> getCDNMetadata(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<CFObject> getObject(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return compose(blobStore.getBlob(container, key, getOptions), blob2Object);
   }

   public ListenableFuture<MutableObjectInfoWithMetadata> getObjectInfo(String container, String key) {
      return compose(blobStore.blobMetadata(container, key),
               new Function<BlobMetadata, MutableObjectInfoWithMetadata>() {

                  @Override
                  public MutableObjectInfoWithMetadata apply(BlobMetadata from) {

                     return blob2ObjectInfo.apply(from);
                  }

               });
   }

   public ListenableFuture<? extends SortedSet<ContainerCDNMetadata>> listCDNContainers(
            ListCdnContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<? extends SortedSet<ContainerMetadata>> listContainers(
            org.jclouds.rackspace.cloudfiles.options.ListContainerOptions... options) {
      return immediateFuture(Sets.newTreeSet(Iterables.transform(blobStore.getContainerToBlobs()
               .keySet(), new Function<String, ContainerMetadata>() {
         public ContainerMetadata apply(String name) {
            return new ContainerMetadata(name, -1, -1);
         }
      })));
   }

   public ListenableFuture<ListContainerResponse<ObjectInfo>> listObjects(String container,
            org.jclouds.rackspace.cloudfiles.options.ListContainerOptions... optionsList) {
      ListContainerOptions options = container2ContainerListOptions.apply(optionsList);
      return compose(blobStore.list(container, options), resource2ObjectList);
   }

   public ListenableFuture<String> putObject(String container, CFObject object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public ListenableFuture<Void> removeObject(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

   public ListenableFuture<Boolean> setObjectInfo(String container, String key,
            Map<String, String> userMetadata) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> updateCDN(String container, long ttl) {
      throw new UnsupportedOperationException();
   }

   public CFObject newCFObject() {
      return objectProvider.create(null);
   }

}
