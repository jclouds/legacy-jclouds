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
package org.jclouds.atmosonline.saas.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.atmosonline.saas.util.AtmosStorageUtils;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.domain.Location;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.options.GetOptions;

/**
 * @author Adrian Cole
 */
@Singleton
public class AtmosBlobStore extends BaseBlobStore {
   private final AtmosStorageClient sync;
   private final ObjectToBlob object2Blob;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToObject blob2Object;
   private final BlobStoreListOptionsToListOptions container2ContainerListOptions;
   private final DirectoryEntryListToResourceMetadataList container2ResourceList;
   private final EncryptionService encryptionService;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final Provider<FetchBlobMetadata> fetchBlobMetadataProvider;

   @Inject
   AtmosBlobStore(BlobStoreContext context, BlobUtils blobUtils, Location defaultLocation,
            Set<? extends Location> locations, AtmosStorageClient sync, ObjectToBlob object2Blob,
            ObjectToBlobMetadata object2BlobMd, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList,
            EncryptionService encryptionService, BlobToHttpGetOptions blob2ObjectGetOptions,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider) {
      super(context, blobUtils, defaultLocation, locations);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.sync = checkNotNull(sync, "sync");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.encryptionService = checkNotNull(encryptionService, "encryptionService");
      this.fetchBlobMetadataProvider = checkNotNull(fetchBlobMetadataProvider,
               "fetchBlobMetadataProvider");
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#headFile}
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.headFile(container + "/" + key));
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#deletePath} followed by
    * {@link AtmosStorageAsyncClient#pathExists} until it is true.
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      sync.deletePath(container + "/");
      return !sync.pathExists(container + "/");
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#createDirectory}
    * 
    * @param location
    *           currently ignored
    * @param container
    *           directory name
    */
   @Override
   public boolean createContainerInLocation(Location location, String container) {
      sync.createDirectory(container);
      return true;
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#createDirectory}
    * 
    * @param container
    *           directory name
    */
   @Override
   public void createDirectory(String container, String directory) {
      sync.createDirectory(container + "/" + directory);
   }

   /**
    * This implementation invokes {@link #removeBlob}
    */
   @Override
   public void deleteDirectory(String containerName, String directory) {
      removeBlob(containerName, directory + "/");
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#pathExists}
    */
   @Override
   public boolean containerExists(String container) {
      return sync.pathExists(container + "/");
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#pathExists}
    */
   @Override
   public boolean directoryExists(String container, String directory) {
      return sync.pathExists(container + "/" + directory + "/");
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#pathExists}
    * 
    * @param container
    *           container
    * @param key
    *           file name
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.pathExists(container + "/" + key);
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#readFile}
    */
   @Override
   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      return object2Blob.apply(sync.readFile(container + "/" + key, httpOptions));
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#listDirectories}
    */
   @Override
   public PageSet<? extends StorageMetadata> list() {
      return container2ResourceList.apply(sync.listDirectories());
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#listDirectory}
    */
   @Override
   public PageSet<? extends StorageMetadata> list(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      container = AtmosStorageUtils.adjustContainerIfDirOptionPresent(container, options);
      ListOptions nativeOptions = container2ContainerListOptions.apply(options);
      // until includeMeta() option works for namespace interface
      PageSet<? extends StorageMetadata> list = container2ResourceList.apply(sync.listDirectory(
               container, nativeOptions));
      return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container)
               .apply(list) : list;
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#createFile}
    * <p/>
    * Since there is no etag support in atmos, we just return the path.
    */
   @Override
   public String putBlob(final String container, final Blob blob) {
      return AtmosStorageUtils.putBlob(sync, encryptionService, blob2Object, container, blob);
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#deletePath}
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deletePath(container + "/" + key);
   }

}
