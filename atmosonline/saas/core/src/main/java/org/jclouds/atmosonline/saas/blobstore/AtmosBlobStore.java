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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobStoreListOptionsToListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.DirectoryEntryListToResourceMetadataList;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionCallable;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

@ConsistencyModel(ConsistencyModels.EVENTUAL)
public class AtmosBlobStore implements BlobStore {
   private final AtmosStorageClient connection;
   private final Blob.Factory blobFactory;
   private final LoggerFactory logFactory;
   private final ClearListStrategy clearContainerStrategy;
   private final ObjectToBlobMetadata object2BlobMd;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobStoreListOptionsToListOptions container2ContainerListOptions;
   private final BlobToHttpGetOptions blob2ObjectGetOptions;
   private final DirectoryEntryListToResourceMetadataList container2ResourceList;
   private final ExecutorService service;
   
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   
   @Inject
   private AtmosBlobStore(AtmosStorageClient connection, Blob.Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            ObjectToBlobMetadata object2BlobMd, ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListOptionsToListOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            DirectoryEntryListToResourceMetadataList container2ResourceList, ExecutorService service) {
      this.connection = checkNotNull(connection, "connection");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.logFactory = checkNotNull(logFactory, "logFactory");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.container2ResourceList = checkNotNull(container2ResourceList, "container2ResourceList");
      this.service = checkNotNull(service, "service");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   /**
    * This implementation uses the AtmosStorage HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(connection.headFile(container + "/" + key));
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
      return wrapFuture(connection.createDirectory(container), new Function<URI, Boolean>() {

         public Boolean apply(URI from) {
            return true;// no etag
         }

      });
   }

   public Future<Void> deleteContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            connection.deletePath(container).get();
            if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
               public Boolean get() {
                  return !connection.pathExists(container);
               }
            }, requestTimeoutMilliseconds)) {
               throw new IllegalStateException(container + " still exists after deleting!");
            }
            return null;
         }

      });
   }

   public boolean exists(String container) {
      return connection.pathExists(container);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<AtmosObject> returnVal = connection.readFile(container + "/" + key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends ListResponse<? extends ResourceMetadata>> list() {
      return wrapFuture(connection.listDirectories(), container2ResourceList);
   }

   public Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(
            String container, org.jclouds.blobstore.options.ListContainerOptions... optionsList) {
      if (optionsList.length == 1) {
         if (!optionsList[0].isRecursive()) {
            throw new UnsupportedOperationException("recursive not currently supported in emcsaas");
         }
         if (optionsList[0].getPath() != null) {
            container = container + "/" + optionsList[0].getPath();
         }
      }
      ListOptions nativeOptions = container2ContainerListOptions.apply(optionsList);
      return wrapFuture(connection.listDirectory(container, nativeOptions), container2ResourceList);
   }

   public Future<String> putBlob(final String container, final Blob blob) {
      final String path = container + "/" + blob.getMetadata().getName();

      Callable<String> valueCallable = new FutureFunctionCallable<Void, String>(connection
               .deletePath(path), new Function<Void, String>() {

         public String apply(Void from) {
            boolean exists = connection.pathExists(path);
            if (!exists)
               try {
                  if (blob.getMetadata().getContentMD5() != null)
                     blob.getMetadata().getUserMetadata().put("content-md5",
                              HttpUtils.toHexString(blob.getMetadata().getContentMD5()));
                  connection.createFile(container, blob2Object.apply(blob)).get();
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               } catch (ExecutionException e) {
                  throw new RuntimeException(e);
               }
            return null;
         }

      });
      return service.submit(valueCallable);

   }

   public Future<Void> removeBlob(String container, String key) {
      return connection.deletePath(container + "/" + key);
   }

   public Blob newBlob() {
      return blobFactory.create(null);
   }

}
