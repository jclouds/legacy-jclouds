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
package org.jclouds.hpcloud.objectstorage.lvs.blobstore;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasAsyncClient;
import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasClient;
import org.jclouds.openstack.swift.blobstore.SwiftAsyncBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class HPCloudObjectStorageLasVegasAsyncBlobStore extends SwiftAsyncBlobStore {

   @Inject
   protected HPCloudObjectStorageLasVegasAsyncBlobStore(BlobStoreContext context, BlobUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, HPCloudObjectStorageLasVegasClient sync, HPCloudObjectStorageLasVegasAsyncClient async,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils, service, defaultLocation, locations, sync, async, container2ResourceMd,
               container2ContainerListOptions, container2ResourceList, object2Blob, blob2Object, object2BlobMd,
               blob2ObjectGetOptions, fetchBlobMetadataProvider);
   }

}
