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
package org.jclouds.atmosonline.saas.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.compose;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobMetadataToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.ListOptionsToBlobStoreListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ResourceMetadataListToDirectoryEntryList;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.http.options.GetOptions;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link AtmosStorageAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubAtmosStorageAsyncClient implements AtmosStorageAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final AtmosObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobMetadataToObject blob2ObjectInfo;
   private final ListOptionsToBlobStoreListOptions container2ContainerListOptions;
   private final ResourceMetadataListToDirectoryEntryList resource2ObjectList;

   @Inject
   private StubAtmosStorageAsyncClient(StubAsyncBlobStore blobStore,
            AtmosObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, BlobMetadataToObject blob2ObjectInfo,
            ListOptionsToBlobStoreListOptions container2ContainerListOptions,
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
   }

   public ListenableFuture<URI> createDirectory(String directoryName) {
      final String container;
      final String path;
      if (directoryName.indexOf('/') != -1) {
         container = directoryName.substring(0, directoryName.indexOf('/'));
         path = directoryName.substring(directoryName.indexOf('/') + 1);
      } else {
         container = directoryName;
         path = null;
      }
      return Futures.compose(blobStore.createContainerInLocation("default", container),
               new Function<Boolean, URI>() {

                  public URI apply(Boolean from) {
                     if (path != null) {
                        Blob blob = blobStore.newBlob(path + "/");
                        blob.getMetadata().setContentType("application/directory");
                        blob.setPayload("");
                        blobStore.putBlob(container, blob);
                     }
                     return URI.create("http://stub/containers/" + container);
                  }

               });
   }

   public ListenableFuture<URI> createFile(String parent, AtmosObject object) {
      final String uri = "http://stub/containers/" + parent + "/"
               + object.getContentMetadata().getName();
      String file = object.getContentMetadata().getName();
      String container = parent;
      if (parent.indexOf('/') != -1) {
         container = parent.substring(0, parent.indexOf('/'));
         String path = parent.substring(parent.indexOf('/') + 1);
         if (!path.equals(""))
            object.getContentMetadata().setName(path + "/" + file);
      }
      Blob blob = object2Blob.apply(object);
      return compose(blobStore.putBlob(container, blob), new Function<String, URI>() {

         public URI apply(String from) {
            return URI.create(uri);
         }

      });
   }

   public ListenableFuture<Void> deletePath(String path) {
      if (path.indexOf('/') == -1)
         return compose(blobStore.deleteContainerImpl(path), new Function<Boolean, Void>() {

            public Void apply(Boolean from) {
               return null;
            }

         });
      else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return blobStore.removeBlob(container, path);
      }
   }

   public ListenableFuture<SystemMetadata> getSystemMetadata(String path) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<UserMetadata> getUserMetadata(String path) {
      if (path.indexOf('/') == -1)
         throw new UnsupportedOperationException();
      else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return compose(blobStore.blobMetadata(container, path),
                  new Function<BlobMetadata, UserMetadata>() {
                     public UserMetadata apply(BlobMetadata from) {
                        return blob2ObjectInfo.apply(from).getUserMetadata();
                     }
                  });
      }
   }

   public ListenableFuture<AtmosObject> headFile(String path) {
      String container = path.substring(0, path.indexOf('/'));
      path = path.substring(path.indexOf('/') + 1);
      try {
         return Futures.compose(blobStore.getBlob(container, path), blob2Object);
      } catch (Exception e) {
         return immediateFailedFuture(Throwables.getRootCause(e));
      }
   }

   public ListenableFuture<? extends BoundedSet<? extends DirectoryEntry>> listDirectories(
            ListOptions... optionsList) {
      // org.jclouds.blobstore.options.ListOptions options = container2ContainerListOptions
      // .apply(optionsList);
      return Futures.compose(blobStore.list(), resource2ObjectList);
   }

   public ListenableFuture<? extends BoundedSet<? extends DirectoryEntry>> listDirectory(
            String directoryName, ListOptions... optionsList) {
      org.jclouds.blobstore.options.ListContainerOptions options = container2ContainerListOptions
               .apply(optionsList);
      String container = directoryName;
      if (directoryName.indexOf('/') != -1) {
         container = directoryName.substring(0, directoryName.indexOf('/'));
         String path = directoryName.substring(directoryName.indexOf('/') + 1);
         if (!path.equals(""))
            options.inDirectory(path);
      }
      return compose(blobStore.list(container, options), resource2ObjectList);
   }

   public AtmosObject newObject() {
      return this.objectProvider.create(null);
   }

   public ListenableFuture<Boolean> pathExists(final String path) {
      if (path.indexOf('/') == -1 )
         return blobStore.containerExists(path);
      else {
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

   public ListenableFuture<AtmosObject> readFile(String path, GetOptions... options) {
      String container = path.substring(0, path.indexOf('/'));
      String blobName = path.substring(path.indexOf('/') + 1);
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return Futures.compose(blobStore.getBlob(container, blobName, getOptions), blob2Object);
   }

   public ListenableFuture<Void> updateFile(String parent, AtmosObject object) {
      throw new UnsupportedOperationException();
   }

}
