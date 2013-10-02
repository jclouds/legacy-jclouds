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
package org.jclouds.openstack.swift.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.transform;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.http.options.GetOptions;
import org.jclouds.lifecycle.Closer;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.SwiftAsyncClient;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ListContainerOptionsToBlobStoreListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ResourceToObjectInfo;
import org.jclouds.openstack.swift.blobstore.functions.ResourceToObjectList;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.openstack.swift.options.CreateContainerOptions;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Implementation of {@link SwiftAsyncClient} which keeps all data in a local Map object.
 *
 * @author Adrian Cole
 */
@Singleton
public class StubSwiftAsyncClient implements CommonSwiftAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final LocalAsyncBlobStore blobStore;
   private final SwiftObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ResourceToObjectInfo blob2ObjectInfo;
   private final ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions;
   private final ResourceToObjectList resource2ObjectList;
   private final ListeningExecutorService userExecutor;
   private final Closer closer;

   @Inject
   private StubSwiftAsyncClient(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            LocalAsyncBlobStore blobStore,
            SwiftObject.Factory objectProvider, HttpGetOptionsListToGetOptions httpGetOptionsConverter,
            ObjectToBlob object2Blob, BlobToObject blob2Object, ResourceToObjectInfo blob2ObjectInfo,
            ListContainerOptionsToBlobStoreListContainerOptions container2ContainerListOptions,
            ResourceToObjectList resource2ContainerList, Closer closer) {
      this.userExecutor = userExecutor;
      this.blobStore = blobStore;
      this.objectProvider = objectProvider;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectInfo = checkNotNull(blob2ObjectInfo, "blob2ObjectInfo");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.resource2ObjectList = checkNotNull(resource2ContainerList, "resource2ContainerList");
      this.closer = checkNotNull(closer, "closer");
   }

   public ListenableFuture<Boolean> containerExists(final String container) {
      return blobStore.containerExists(container);
   }

   public ListenableFuture<Boolean> createContainer(String container) {
      return blobStore.createContainerInLocation(null, container);
   }

   public ListenableFuture<Boolean> deleteContainerIfEmpty(String container) {
      return blobStore.deleteContainerIfEmpty(container);
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

   public ListenableFuture<SwiftObject> getObject(String container, String key, GetOptions... options) {
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return transform(blobStore.getBlob(container, key, getOptions), blob2Object, userExecutor);
   }

   public ListenableFuture<MutableObjectInfoWithMetadata> getObjectInfo(String container, String key) {
      return transform(blobStore.blobMetadata(container, key),
               new Function<BlobMetadata, MutableObjectInfoWithMetadata>() {

                  @Override
                  public MutableObjectInfoWithMetadata apply(BlobMetadata from) {

                     return blob2ObjectInfo.apply(from);
                  }

               }, userExecutor);
   }

   public ListenableFuture<? extends Set<ContainerMetadata>> listContainers(
            org.jclouds.openstack.swift.options.ListContainerOptions... options) {
      PageSet<? extends StorageMetadata> listing;
      try {
         listing = blobStore.list().get();
      } catch (ExecutionException ee) {
         throw Throwables.propagate(ee);
      } catch (InterruptedException ie) {
         throw Throwables.propagate(ie);
      }
      return immediateFuture(Sets.newHashSet(Iterables.transform(listing,
               new Function<StorageMetadata, ContainerMetadata>() {
                  public ContainerMetadata apply(StorageMetadata md) {
                     return ContainerMetadata.builder().name(md.getName()).count(-1).bytes(-1).metadata(new HashMap<String,String>()).build();
                  }
               })));
   }

   @Override
   public ListenableFuture<ContainerMetadata> getContainerMetadata(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Boolean> setContainerMetadata(String container, Map<String, String> containerMetadata) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Boolean> deleteContainerMetadata(String container, Iterable<String> metadataKeys) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Boolean> createContainer(String container, CreateContainerOptions... options) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<PageSet<ObjectInfo>> listObjects(String container,
            org.jclouds.openstack.swift.options.ListContainerOptions... optionsList) {
      ListContainerOptions options = container2ContainerListOptions.apply(optionsList);
      return transform(blobStore.list(container, options), resource2ObjectList, userExecutor);
   }

   public ListenableFuture<Boolean> copyObject(String sourceContainer, String sourceObject, String destinationContainer, String destinationObject) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<String> putObject(String container, SwiftObject object) {
      return blobStore.putBlob(container, object2Blob.apply(object));
   }

   public ListenableFuture<Void> removeObject(String container, String key) {
      return blobStore.removeBlob(container, key);
   }

    @Override
    public ListenableFuture<String> putObjectManifest(String container, String name) {
        return null;
    }

   public ListenableFuture<Boolean> setObjectInfo(String container, String key, Map<String, String> userMetadata) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> updateCDN(String container, long ttl) {
      throw new UnsupportedOperationException();
   }

   public SwiftObject newSwiftObject() {
      return objectProvider.create(null);
   }

   @Override
   public ListenableFuture<Boolean> objectExists(String bucketName, String key) {
      return blobStore.blobExists(bucketName, key);
   }

   @Override
   public void close() throws IOException {
      closer.close();
   }
}
