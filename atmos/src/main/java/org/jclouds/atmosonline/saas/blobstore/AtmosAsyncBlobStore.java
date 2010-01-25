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

import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.ConcurrentUtils.convertExceptionToValue;
import static org.jclouds.concurrent.ConcurrentUtils.makeListenable;

import java.net.URI;
import java.util.concurrent.Callable;
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
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
public class AtmosAsyncBlobStore extends BaseAtmosBlobStore implements AsyncBlobStore {
   private final EncryptionService encryptionService;

   @Inject
   public AtmosAsyncBlobStore(AtmosStorageAsyncClient async, AtmosStorageClient sync,
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
    * This implementation invokes {@link AtmosStorageAsyncClient#headFile}
    */
   @Override
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return compose(convertExceptionToValue(async.headFile(container + "/" + key),
               KeyNotFoundException.class, null), new Function<AtmosObject, BlobMetadata>() {
         @Override
         public BlobMetadata apply(AtmosObject from) {
            return object2BlobMd.apply(from);
         }
      }, service);
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option.
    */
   @Override
   public ListenableFuture<Void> clearContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      }), service);
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
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option. Then, it blocks until
    * {@link AtmosStorageAsyncClient#pathExists} fails.
    */
   @Override
   public ListenableFuture<Void> deleteContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            sync.deletePath(container);
            if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
               public Boolean get() {
                  return !sync.pathExists(container);
               }
            }, 300)) {
               throw new IllegalStateException(container + " still exists after deleting!");
            }
            return null;
         }

      }), service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> containerExists(String container) {
      return convertExceptionToValue(async.pathExists(container), ContainerNotFoundException.class,
               false);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#pathExists}
    */
   @Override
   public ListenableFuture<Boolean> directoryExists(String container, String directory) {
      return async.pathExists(container + "/" + directory);
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
    * This implementation invokes
    * {@link #getBlob(String,String,org.jclouds.blobstore.options.GetOptions)}
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key) {
      return this.getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#readFile}
    */
   @Override
   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions options) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(options);
      ListenableFuture<AtmosObject> returnVal = async.readFile(container + "/" + key, httpOptions);
      return compose(convertExceptionToValue(returnVal, KeyNotFoundException.class, null),
               object2Blob, service);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#listDirectories}
    */
   @Override
   public ListenableFuture<? extends ListResponse<? extends StorageMetadata>> list() {
      return compose(async.listDirectories(), container2ResourceList, service);
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    */
   @Override
   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link AtmosStorageAsyncClient#listDirectory}
    */
   @Override
   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container, org.jclouds.blobstore.options.ListContainerOptions options) {
      container = adjustContainerIfDirOptionPresent(container, options);
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
               throw new RuntimeException(e);
            }
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
