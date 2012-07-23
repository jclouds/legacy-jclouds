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
package org.jclouds.atmos.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.blobstore.functions.BlobMetadataToObject;
import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.blobstore.functions.ListOptionsToBlobStoreListOptions;
import org.jclouds.atmos.blobstore.functions.ObjectToBlob;
import org.jclouds.atmos.blobstore.functions.ResourceMetadataListToDirectoryEntryList;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.SystemMetadata;
import org.jclouds.atmos.domain.UserMetadata;
import org.jclouds.atmos.options.ListOptions;
import org.jclouds.atmos.options.PutOptions;
import org.jclouds.blobstore.LocalAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.concurrent.Futures;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link AtmosAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubAtmosAsyncClient implements AtmosAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final LocalAsyncBlobStore blobStore;
   private final AtmosObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobMetadataToObject blob2ObjectInfo;
   private final ListOptionsToBlobStoreListOptions container2ContainerListOptions;
   private final ResourceMetadataListToDirectoryEntryList resource2ObjectList;
   private final ExecutorService service;

   @Inject
   private StubAtmosAsyncClient(LocalAsyncBlobStore blobStore, AtmosObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobMetadataToObject blob2ObjectInfo, ListOptionsToBlobStoreListOptions container2ContainerListOptions,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service,
            ResourceMetadataListToDirectoryEntryList resource2ContainerList) {
      this.blobStore = blobStore;
      this.objectProvider = objectProvider;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectInfo = checkNotNull(blob2ObjectInfo, "blob2ObjectInfo");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.resource2ObjectList = checkNotNull(resource2ContainerList, "resource2ContainerList");
      this.service = service;
   }

   @Override
   public ListenableFuture<URI> createDirectory(String directoryName, PutOptions... options) {
      final String container;
      final String path;
      if (directoryName.indexOf('/') != -1) {
         container = directoryName.substring(0, directoryName.indexOf('/'));
         path = directoryName.substring(directoryName.indexOf('/') + 1);
      } else {
         container = directoryName;
         path = null;
      }
      return Futures.compose(blobStore.createContainerInLocation(null, container), new Function<Boolean, URI>() {

         public URI apply(Boolean from) {
            if (path != null) {
               Blob blob = blobStore.blobBuilder(path + "/").payload("").contentType("application/directory").build();
               blobStore.putBlob(container, blob);
            }
            return URI.create("http://stub/containers/" + container);
         }

      }, service);
   }

   @Override
   public ListenableFuture<URI> createFile(String parent, AtmosObject object, PutOptions... options) {
      final String uri = "http://stub/containers/" + parent + "/" + object.getContentMetadata().getName();
      String file = object.getContentMetadata().getName();
      String container = parent;
      if (parent.indexOf('/') != -1) {
         container = parent.substring(0, parent.indexOf('/'));
         String path = parent.substring(parent.indexOf('/') + 1);
         if (!path.equals(""))
            object.getContentMetadata().setName(path + "/" + file);
      }
      Blob blob = object2Blob.apply(object);
      return Futures.compose(blobStore.putBlob(container, blob), new Function<String, URI>() {

         public URI apply(String from) {
            return URI.create(uri);
         }

      }, service);
   }

   @Override
   public ListenableFuture<Void> deletePath(String path) {
      if (path.indexOf('/') == path.length() - 1) {
         // chop off the trailing slash
         return Futures.compose(blobStore.deleteContainerIfEmpty(path.substring(0, path.length() - 1)),
                  new Function<Boolean, Void>() {

                     public Void apply(Boolean from) {
                        return null;
                     }

                  }, service);
      } else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return blobStore.removeBlob(container, path);
      }
   }

   @Override
   public ListenableFuture<SystemMetadata> getSystemMetadata(String path) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<UserMetadata> getUserMetadata(String path) {
      if (path.indexOf('/') == -1)
         throw new UnsupportedOperationException();
      else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return Futures.compose(blobStore.blobMetadata(container, path), new Function<BlobMetadata, UserMetadata>() {
            public UserMetadata apply(BlobMetadata from) {
               return blob2ObjectInfo.apply(from).getUserMetadata();
            }
         }, service);
      }
   }

   @Override
   public ListenableFuture<AtmosObject> headFile(String path) {
      String container = path.substring(0, path.indexOf('/'));
      path = path.substring(path.indexOf('/') + 1);
      try {
         return Futures.compose(blobStore.getBlob(container, path), blob2Object, service);
      } catch (Exception e) {
         return immediateFailedFuture(Throwables.getRootCause(e));
      }
   }

   @Override
   public ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectories(ListOptions... optionsList) {
      // org.jclouds.blobstore.options.ListOptions options = container2ContainerListOptions
      // .apply(optionsList);
      return Futures.compose(blobStore.list(), resource2ObjectList, service);
   }

   @Override
   public ListenableFuture<BoundedSet<? extends DirectoryEntry>> listDirectory(String directoryName,
            ListOptions... optionsList) {
      org.jclouds.blobstore.options.ListContainerOptions options = container2ContainerListOptions.apply(optionsList);
      String container = directoryName;
      if (directoryName.indexOf('/') != -1) {
         container = directoryName.substring(0, directoryName.indexOf('/'));
         String path = directoryName.substring(directoryName.indexOf('/') + 1);
         if (!path.equals(""))
            options.inDirectory(path);
      }
      return Futures.compose(blobStore.list(container, options), resource2ObjectList, service);
   }

   @Override
   public AtmosObject newObject() {
      return this.objectProvider.create(null);
   }

   @Override
   public ListenableFuture<Boolean> pathExists(final String path) {
      if (path.indexOf('/') == path.length() - 1) {
         // chop off the trailing slash
         return blobStore.containerExists(path.substring(0, path.length() - 1));
      } else {
         String container = path.substring(0, path.indexOf('/'));
         String blobName = path.substring(path.indexOf('/') + 1);
         try {
            return immediateFuture(blobStore.blobMetadata(container, blobName).get() != null);
         } catch (InterruptedException e) {
            return immediateFailedFuture(e);
         } catch (ExecutionException e) {
            return immediateFailedFuture(e);
         }
      }
   }

   @Override
   public ListenableFuture<AtmosObject> readFile(String path, GetOptions... options) {
      String container = path.substring(0, path.indexOf('/'));
      String blobName = path.substring(path.indexOf('/') + 1);
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return Futures.compose(blobStore.getBlob(container, blobName, getOptions), blob2Object, service);
   }

   @Override
   public ListenableFuture<Void> updateFile(String parent, AtmosObject object, PutOptions... options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ListenableFuture<Boolean> isPublic(String path) {
      throw new UnsupportedOperationException();
   }

}
