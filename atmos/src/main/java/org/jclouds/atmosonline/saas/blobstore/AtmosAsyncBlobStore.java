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
import static com.google.common.util.concurrent.Futures.compose;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.atmosonline.saas.util.AtmosStorageUtils;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.internal.BaseAsyncBlobStore;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.options.GetOptions;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
@Singleton
public class AtmosAsyncBlobStore extends BaseAsyncBlobStore {
   private final AtmosStorageAsyncClient async;
   private final AtmosStorageClient sync;
   private final ObjectToBlob object2Blob;
   private final ObjectToBlobMetadata object2BlobMd;
   private final BlobToObject blob2Object;
   private final BlobStoreListOptionsToListOptions container2ContainerListOptions;
   private final DirectoryEntryListToResourceMetadataList container2ResourceList;
   private final EncryptionService encryptionService;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;

   @Inject
   AtmosAsyncBlobStore(BlobStoreUtils blobUtils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            AtmosStorageAsyncClient async, AtmosStorageClient sync, ObjectToBlob object2Blob,
            ObjectToBlobMetadata object2BlobMd, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList,
            EncryptionService encryptionService, BlobToHttpGetOptions blob2ObjectGetOptions) {
      super(blobUtils, service);
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.sync = checkNotNull(sync, "sync");
      this.async = checkNotNull(async, "async");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.encryptionService = checkNotNull(encryptionService, "encryptionService");
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#headFile}
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return compose(async.headFile(container + "/" + key),
               new Function<AtmosObject, BlobMetadata>() {
                  @Override
                  public BlobMetadata apply(AtmosObject from) {
                     return object2BlobMd.apply(from);
                  }
               }, service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#createDirectory}
    * <p/>
    * Note location is ignored
    */
   @Override
   public ListenableFuture<Boolean> createContainerInLocation(String location, String container) {
      return compose(async.createDirectory(container), new Function<URI, Boolean>() {

         public Boolean apply(URI from) {
            return true;
         }

      });
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#createDirectory}
    */
   @Override
   public ListenableFuture<Void> createDirectory(String container, String directory) {
      return compose(async.createDirectory(container + "/" + directory), new Function<URI, Void>() {

         public Void apply(URI from) {
            return null;// no etag
         }

      });
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#deletePath} followed by
    * {@link AtmosStorageAsyncClient#pathExists} until it is true.
    */
   protected boolean deleteAndVerifyContainerGone(final String container) {
      sync.deletePath(container);
      return !sync.pathExists(container);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return async.pathExists(container);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> directoryExists(String container, String directory) {
      return async.pathExists(container + "/" + directory + "/");
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#pathExists}
    * 
    * @param container
    *           container
    * @param key
    *           file name
    */
   @Override
   public ListenableFuture<Boolean> blobExists(String container, String key) {
      return async.pathExists(container + "/" + key);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#readFile}
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<AtmosObject> returnVal = async.readFile(container + "/" + key, httpOptions);
      return compose(returnVal, object2Blob, service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#listDirectories}
    */
   @Override
   public ListenableFuture<? extends PageSet<? extends StorageMetadata>> list() {
      return compose(async.listDirectories(), container2ResourceList, service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#listDirectory}
    */
   @Override
   public ListenableFuture<? extends PageSet<? extends StorageMetadata>> list(String container,
            org.jclouds.blobstore.options.ListContainerOptions options) {
      container = AtmosStorageUtils.adjustContainerIfDirOptionPresent(container, options);
      ListOptions nativeOptions = container2ContainerListOptions.apply(options);
      return compose(async.listDirectory(container, nativeOptions), container2ResourceList, service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#createFile}
    * <p/>
    * Since there is no etag support in atmos, we just return the path.
    */
   @Override
   public ListenableFuture<String> putBlob(final String container, final Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();
      return compose(async.deletePath(path), new Function<Void, String>() {

         public String apply(Void from) {
            try {
               if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
                  public Boolean get() {
                     return !sync.pathExists(path);
                  }
               }, 300)) {
                  throw new IllegalStateException(path + " still exists after deleting!");
               }
               if (blob.getMetadata().getContentMD5() != null)
                  blob.getMetadata().getUserMetadata().put("content-md5",
                           encryptionService.toHexString(blob.getMetadata().getContentMD5()));
               sync.createFile(container, blob2Object.apply(blob));
               return path;
            } catch (InterruptedException e) {
               Throwables.propagate(e);
            }
            assert false : " should have propagated error";
            return null;
         }

      }, service);

   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#deletePath}
    */
   @Override
   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deletePath(container + "/" + key);
   }

}
