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

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.blobstore.util.BlobStoreUtils.keyNotFoundToNullOrPropagate;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmosonline.saas.blobstore.internal.BaseAtmosBlobStore;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Utils;

import com.google.common.base.Supplier;

public class AtmosBlobStore extends BaseAtmosBlobStore implements BlobStore {
   private final EncryptionService encryptionService;

   @Inject
   public AtmosBlobStore(AtmosStorageAsyncClient async, AtmosStorageClient sync,
            Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            EncryptionService encryptionService) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               container2ResourceList, service);
      this.encryptionService = encryptionService;
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#headFile}
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      try {
         return object2BlobMd.apply(sync.headFile(container + "/" + key));
      } catch (Exception e) {
         return keyNotFoundToNullOrPropagate(e);
      }
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option.
    */
   @Override
   public void clearContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option. Then, it invokes
    * {@link #deleteAndEnsurePathGone}
    */
   @Override
   public void deleteContainer(final String container) {
      try {
         clearContainerStrategy.execute(container, recursive());
         deleteAndEnsurePathGone(container);
      } catch (ContainerNotFoundException e) {

      }
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#deleteAndEnsurePathGone} then blocks
    * until {@link AtmosStorageClient#pathExists} returns false.
    */
   public void deleteAndEnsurePathGone(final String path) {
      sync.deletePath(path);
      try {
         if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
            public Boolean get() {
               return !sync.pathExists(path);
            }
         }, 30000)) {
            throw new IllegalStateException(path + " still exists after deleting!");
         }
      } catch (InterruptedException e) {
         new IllegalStateException(path + " interrupted during deletion!", e);
      }
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
   public boolean createContainerInLocation(String location, String container) {
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
    * This implementation invokes {@link AtmosStorageClient#pathExists}
    */
   @Override
   public boolean containerExists(String container) {
      try {
         return sync.pathExists(container);
      } catch (ContainerNotFoundException e) {
         return false;
      }
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#pathExists}
    */
   @Override
   public boolean directoryExists(String container, String directory) {
      try {
         return sync.pathExists(container + "/" + directory);
      } catch (Exception e) {
         keyNotFoundToNullOrPropagate(e);
         return false;
      }
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
    * This implementation invokes
    * {@link #getBlob(String,String,org.jclouds.blobstore.options.GetOptions)}
    */
   @Override
   public Blob getBlob(String container, String key) {
      return this.getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#readFile}
    */
   @Override
   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      try {
         return object2Blob.apply(sync.readFile(container + "/" + key, httpOptions));
      } catch (Exception e) {
         return keyNotFoundToNullOrPropagate(e);
      }
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#listDirectories}
    */
   @Override
   public ListResponse<? extends StorageMetadata> list() {
      return container2ResourceList.apply(sync.listDirectories());
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    */
   @Override
   public ListContainerResponse<? extends StorageMetadata> list(String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#listDirectory}
    */
   @Override
   public ListContainerResponse<? extends StorageMetadata> list(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      container = adjustContainerIfDirOptionPresent(container, options);
      ListOptions nativeOptions = container2ContainerListOptions.apply(options);
      return container2ResourceList.apply(sync.listDirectory(container, nativeOptions));
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#createFile}
    * <p/>
    * Since there is no etag support in atmos, we just return the path.
    */
   @Override
   public String putBlob(final String container, final Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();
      deleteAndEnsurePathGone(path);
      if (blob.getMetadata().getContentMD5() != null)
         blob.getMetadata().getUserMetadata().put("content-md5",
                  encryptionService.toHexString(blob.getMetadata().getContentMD5()));
      sync.createFile(container, blob2Object.apply(blob));
      return path;
   }

   /**
    * This implementation invokes {@link AtmosStorageClient#deletePath}
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deletePath(container + "/" + key);
   }

}
