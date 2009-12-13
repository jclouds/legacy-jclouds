/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.atmosonline.saas.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

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
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionCallable;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.EncryptionService;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

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
            ExecutorService service, EncryptionService encryptionService) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               container2ResourceList, service);
      this.encryptionService = encryptionService;
   }

   /**
    * This implementation uses the AtmosStorage HEAD Object command to return the result
    */
   public Future<BlobMetadata> blobMetadata(String container, String key) {
      return wrapFuture(async.headFile(container + "/" + key),
               new Function<AtmosObject, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(AtmosObject from) {
                     return object2BlobMd.apply(from);
                  }

               });
   }

   public Future<Void> clearContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      });
   }

   public Future<Boolean> createContainer(String container) {
      return wrapFuture(async.createDirectory(container), new Function<URI, Boolean>() {

         public Boolean apply(URI from) {
            return true;// no etag
         }

      });
   }

   public Future<Void> createDirectory(String container, String directory) {
      return wrapFuture(async.createDirectory(container + "/" + directory),
               new Function<URI, Void>() {

                  public Void apply(URI from) {
                     return null;// no etag
                  }

               });
   }

   public Future<Void> deleteContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deletePath(container).get();
            if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
               public Boolean get() {
                  return !sync.pathExists(container);
               }
            }, requestTimeoutMilliseconds)) {
               throw new IllegalStateException(container + " still exists after deleting!");
            }
            return null;
         }

      });
   }

   public Future<Boolean> containerExists(String container) {
      return async.pathExists(container);
   }

   public Future<Boolean> directoryExists(String container, String directory) {
      return async.pathExists(container + "/" + directory);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<AtmosObject> returnVal = async.readFile(container + "/" + key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends ListResponse<? extends ResourceMetadata>> list() {
      return wrapFuture(async.listDirectories(), container2ResourceList);
   }

   public Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(
            String container, org.jclouds.blobstore.options.ListContainerOptions... optionsList) {
      if (optionsList.length == 1) {
         if (optionsList[0].isRecursive()) {
            throw new UnsupportedOperationException("recursive not currently supported in emcsaas");
         }
         if (optionsList[0].getDir() != null) {
            container = container + "/" + optionsList[0].getDir();
         }
      }
      ListOptions nativeOptions = container2ContainerListOptions.apply(optionsList);
      return wrapFuture(async.listDirectory(container, nativeOptions), container2ResourceList);
   }

   /**
    * Since there is no etag support in atmos, we just return the path.
    */
   public Future<String> putBlob(final String container, final Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();

      Callable<String> valueCallable = new FutureFunctionCallable<Void, String>(async
               .deletePath(path), new Function<Void, String>() {

         public String apply(Void from) {
            try {
               if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
                  public Boolean get() {
                     return !sync.pathExists(path);
                  }
               }, requestTimeoutMilliseconds)) {
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

      });
      return service.submit(valueCallable);

   }

   public Future<Void> removeBlob(String container, String key) {
      return async.deletePath(container + "/" + key);
   }

}
