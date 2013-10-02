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
package org.jclouds.cloudfiles.blobstore;

import static com.google.common.util.concurrent.Futures.transform;

import java.util.Set;

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
import org.jclouds.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.cloudfiles.blobstore.functions.EnableCDNAndCache;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.openstack.swift.blobstore.strategy.internal.AsyncMultipartUploadStrategy;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * 
 * @author Adrian Cole
 * @deprecated will be removed in jclouds 1.7, as async interfaces are no longer
 *             supported. Please use {@link CloudFilesBlobStore}
 */
@Deprecated
@Singleton
public class CloudFilesAsyncBlobStore extends SwiftAsyncBlobStore {
   private final EnableCDNAndCache enableCDNAndCache;

   @Inject
   protected CloudFilesAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, CloudFilesClient sync, CloudFilesAsyncClient async,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider, EnableCDNAndCache enableCDNAndCache,
            Provider<AsyncMultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, userExecutor, defaultLocation, locations, sync, async, container2ResourceMd,
               container2ContainerListOptions, container2ResourceList, object2Blob, blob2Object, object2BlobMd,
               blob2ObjectGetOptions, fetchBlobMetadataProvider, multipartUploadStrategy);
      this.enableCDNAndCache = enableCDNAndCache;
   }

   @Override
   public ListenableFuture<Boolean> createContainerInLocation(Location location, final String container,
            CreateContainerOptions options) {

      ListenableFuture<Boolean> returnVal = createContainerInLocation(location, container);
      if (options.isPublicRead())
         return transform(createContainerInLocation(location, container), new Function<Boolean, Boolean>() {

            @Override
            public Boolean apply(Boolean input) {
               if (Boolean.TRUE.equals(input)) {
                  return enableCDNAndCache.apply(container) != null;
               }
               return false;
            }

         }, userExecutor);
      return returnVal;
   }
}
