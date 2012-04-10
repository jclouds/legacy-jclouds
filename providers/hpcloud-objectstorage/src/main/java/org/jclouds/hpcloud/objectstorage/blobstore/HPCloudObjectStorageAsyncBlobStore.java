/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.hpcloud.objectstorage.blobstore;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.concurrent.Futures;
import org.jclouds.domain.Location;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageAsyncClient;
import org.jclouds.hpcloud.objectstorage.HPCloudObjectStorageClient;
import org.jclouds.hpcloud.objectstorage.blobstore.functions.EnableCDNAndCache;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class HPCloudObjectStorageAsyncBlobStore extends SwiftAsyncBlobStore {
   private final EnableCDNAndCache enableCDNAndCache;

   @Inject
   protected HPCloudObjectStorageAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, HPCloudObjectStorageClient sync, HPCloudObjectStorageAsyncClient async,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider, EnableCDNAndCache enableCDNAndCache) {
      super(context, blobUtils, service, defaultLocation, locations, sync, async, container2ResourceMd,
               container2ContainerListOptions, container2ResourceList, object2Blob, blob2Object, object2BlobMd,
               blob2ObjectGetOptions, fetchBlobMetadataProvider, null);
      this.enableCDNAndCache = enableCDNAndCache;
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, final String container,
            CreateContainerOptions options) {

      ListenableFuture<Boolean> returnVal = createContainerInLocation(location, container);
      if (options.isPublicRead())
         return Futures.compose(createContainerInLocation(location, container), new Function<Boolean, Boolean>() {

            @Override
            public Boolean apply(Boolean input) {
               if (Boolean.TRUE.equals(input)) {
                  return enableCDNAndCache.apply(container) != null;
               }
               return false;
            }

         }, service);
      return returnVal;
   }
}
