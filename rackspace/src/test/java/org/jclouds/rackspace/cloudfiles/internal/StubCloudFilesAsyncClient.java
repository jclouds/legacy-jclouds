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

import java.net.URI;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore.FutureBase;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
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

/**
 * Implementation of {@link CloudFilesAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubCloudFilesAsyncClient implements CloudFilesAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final LoggerFactory logFactory;
   private final CFObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ResourceToObjectInfo blob2ObjectInfo;
   private final ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions;
   private final ResourceToObjectList resource2ObjectList;

   @Inject
   private StubCloudFilesAsyncClient(StubAsyncBlobStore blobStore, LoggerFactory logFactory,
            ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            CFObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, ResourceToObjectInfo blob2ObjectInfo,
            ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions,
            ResourceToObjectList resource2ContainerList) {
      this.blobStore = blobStore;
      this.logFactory = logFactory;
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

   public Future<Boolean> containerExists(final String container) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            return blobStore.getContainerToBlobs().containsKey(container);
         }
      };
   }

   public Future<Boolean> createContainer(String container) {
      return blobStore.createContainer(container);
   }

   public Future<Boolean> deleteContainerIfEmpty(String container) {
      return blobStore.deleteContainerImpl(container);
   }

   public Future<Boolean> disableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> enableCDN(String container, long ttl) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> enableCDN(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<AccountMetadata> getAccountStatistics() {
      throw new UnsupportedOperationException();
   }

   public Future<ContainerCDNMetadata> getCDNMetadata(String container) {
      throw new UnsupportedOperationException();
   }

   public Future<CFObject> getObject(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return wrapFuture(blobStore.getBlob(container, key, getOptions), blob2Object);
   }

   public Future<MutableObjectInfoWithMetadata> getObjectInfo(String container, String key) {
      return wrapFuture(blobStore.blobMetadata(container, key),
               new Function<BlobMetadata, MutableObjectInfoWithMetadata>() {

                  @Override
                  public MutableObjectInfoWithMetadata apply(BlobMetadata from) {

                     return blob2ObjectInfo.apply(from);
                  }

               });
   }

   public Future<? extends SortedSet<ContainerCDNMetadata>> listCDNContainers(
            ListCdnContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public Future<? extends SortedSet<ContainerMetadata>> listContainers(
            org.jclouds.rackspace.cloudfiles.options.ListContainerOptions... options) {
      return new FutureBase<SortedSet<ContainerMetadata>>() {

         public SortedSet<ContainerMetadata> get() throws InterruptedException, ExecutionException {
            return Sets.newTreeSet(Iterables.transform(blobStore.getContainerToBlobs().keySet(),
                     new Function<String, ContainerMetadata>() {
                        public ContainerMetadata apply(String name) {
                           return new ContainerMetadata(name, -1, -1);
                        }

                     }));
         }
      };
   }

   public Future<ListContainerResponse<ObjectInfo>> listObjects(String container,
            org.jclouds.rackspace.cloudfiles.options.ListContainerOptions... optionsList) {
      ListContainerOptions options = container2ContainerListOptions.apply(optionsList);
      return wrapFuture(blobStore.list(container, options), resource2ObjectList);
   }

   public Future<String> putObject(String container, CFObject object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public Future<Void> removeObject(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

   public Future<Boolean> setObjectInfo(String container, String key,
            Map<String, String> userMetadata) {
      throw new UnsupportedOperationException();
   }

   public Future<URI> updateCDN(String container, long ttl) {
      throw new UnsupportedOperationException();
   }

   public CFObject newCFObject() {
      return objectProvider.create(null);
   }

}
