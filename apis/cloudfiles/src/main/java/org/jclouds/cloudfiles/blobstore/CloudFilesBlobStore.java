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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.cloudfiles.blobstore.functions.EnableCDNAndCache;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.blobstore.SwiftBlobStore;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;

import com.google.common.base.Supplier;
import org.jclouds.openstack.swift.blobstore.strategy.internal.MultipartUploadStrategy;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloudFilesBlobStore extends SwiftBlobStore {

   private EnableCDNAndCache enableCDNAndCache;

   @Inject
   protected CloudFilesBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, CommonSwiftClient sync,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider, EnableCDNAndCache enableCDNAndCache,
            Provider<MultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, defaultLocation, locations, sync, container2ResourceMd, container2ContainerListOptions,
               container2ResourceList, object2Blob, blob2Object, object2BlobMd, blob2ObjectGetOptions,
               fetchBlobMetadataProvider, multipartUploadStrategy);
      this.enableCDNAndCache = enableCDNAndCache;

   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      try {
         return createContainerInLocation(location, container);
      } finally {
         if (options.isPublicRead())
            enableCDNAndCache.apply(container);
      }
   }
}
