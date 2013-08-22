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
package org.jclouds.openstack.swift.blobstore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.createParentIfNeededAsync;
import static org.jclouds.openstack.swift.options.ListContainerOptions.Builder.withPrefix;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.http.options.GetOptions;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceList;
import org.jclouds.openstack.swift.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlob;
import org.jclouds.openstack.swift.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.openstack.swift.blobstore.strategy.internal.MultipartUploadStrategy;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SwiftBlobStore extends BaseBlobStore {
   private final CommonSwiftClient sync;
   private final ContainerToResourceMetadata container2ResourceMd;
   private final BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions;
   private final ContainerToResourceList container2ResourceList;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   private final Provider<MultipartUploadStrategy> multipartUploadStrategy;

   @Inject
   protected SwiftBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, CommonSwiftClient sync,
            ContainerToResourceMetadata container2ResourceMd,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            ContainerToResourceList container2ResourceList, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ObjectToBlobMetadata object2BlobMd, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            Provider<MultipartUploadStrategy> multipartUploadStrategy) {
      super(context, blobUtils, defaultLocation, locations);
      this.sync = sync;
      this.container2ResourceMd = container2ResourceMd;
      this.container2ContainerListOptions = container2ContainerListOptions;
      this.container2ResourceList = container2ResourceList;
      this.object2Blob = object2Blob;
      this.blob2Object = blob2Object;
      this.object2BlobMd = object2BlobMd;
      this.blob2ObjectGetOptions = blob2ObjectGetOptions;
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider, "fetchBlobMetadataProvider");
      this.multipartUploadStrategy = multipartUploadStrategy;
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#listContainers}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<Set<ContainerMetadata>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(Set<ContainerMetadata> from) {
            return new PageSetImpl<StorageMetadata>(Iterables.transform(from, container2ResourceMd), null);
         }
      }.apply(sync.listContainers());
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#containerExists}
    * 
    * @param container
    *           container name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.containerExists(container);
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#createContainer}
    * 
    * @param location
    *           currently ignored
    * @param container
    *           container name
    */
   @Override
   public boolean createContainerInLocation(Location location, String container) {
      return sync.createContainer(container);
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#listObjects}
    * 
    * @param container
    *           container name
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {
      org.jclouds.openstack.swift.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(options);
      PageSet<? extends StorageMetadata> list = container2ResourceList.apply(sync.listObjects(container, httpOptions));
      return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container).apply(list) : list;
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#objectExists}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#getObjectInfo}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.getObjectInfo(container, key));
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#getObject}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public Blob getBlob(String container, String key, org.jclouds.blobstore.options.GetOptions optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getObject(container, key, httpOptions));
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      createParentIfNeededAsync(context.getAsyncBlobStore(), container, blob);
      return sync.putObject(container, blob2Object.apply(blob));
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#putObject}
    * 
    * @param container
    *           container name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.isMultipart()) {
        return multipartUploadStrategy.get().execute(container, blob);
      } else {
        return putBlob(container, blob);
      }
   }

   /**
    * This implementation invokes {@link CommonSwiftClient#removeObject}
    * 
    * @param container
    *           container name
    * @param key
    *           file name
    */
   @Override
   public void removeBlob(String container, String key) {
      String objectManifest = getObjectManifestOrNull(container, key);

      sync.removeObject(container, key);

      if (!Strings.isNullOrEmpty(objectManifest)) {
         removeObjectsWithPrefix(objectManifest);
      }
   }

   private String getObjectManifestOrNull(String container, String key) {
      MutableObjectInfoWithMetadata objectInfo = sync.getObjectInfo(container, key);
      return objectInfo == null ? null : objectInfo.getObjectManifest();
   }

   private void removeObjectsWithPrefix(String containerAndPrefix) {
      String[] parts = splitContainerAndKey(containerAndPrefix);

      String container = parts[0];
      String prefix = parts[1];

      removeObjectsWithPrefix(container, prefix);
   }

   @VisibleForTesting
   static String[] splitContainerAndKey(String containerAndKey) {
      String[] parts = containerAndKey.split("/", 2);
      checkArgument(parts.length == 2,
                    "No / separator found in \"%s\"",
                    containerAndKey);
      return parts;
   }

   private void removeObjectsWithPrefix(String container, String prefix) {
      String nextMarker = null;
      do {
         org.jclouds.openstack.swift.options.ListContainerOptions listContainerOptions =
            withPrefix(prefix);
         if (nextMarker != null) {
            listContainerOptions = listContainerOptions.afterMarker(nextMarker);
         }

         PageSet<ObjectInfo> chunks = sync.listObjects(container, listContainerOptions);
         for (ObjectInfo chunk : chunks) {
            sync.removeObject(container, chunk.getName());
         }
         nextMarker = chunks.getNextMarker();
      } while (nextMarker != null);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      sync.deleteContainerIfEmpty(container);
      return !sync.containerExists(container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      if (options.isPublicRead())
         throw new UnsupportedOperationException("publicRead");
      return createContainerInLocation(location, container);
   }
}
