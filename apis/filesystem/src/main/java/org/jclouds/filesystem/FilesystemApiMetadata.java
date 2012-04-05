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
package org.jclouds.filesystem;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BaseBlobStoreApiMetadata;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for jclouds Filesystem-based BlobStore
 * 
 * @author Adrian Cole
 */
public class FilesystemApiMetadata
      extends
      BaseBlobStoreApiMetadata<BlobStore, FilesystemAsyncBlobStore, BlobStoreContext<BlobStore, FilesystemAsyncBlobStore>, FilesystemApiMetadata> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public FilesystemApiMetadata() {
      super(builder());
   }

   protected FilesystemApiMetadata(Builder builder) {
      super(builder);
   }

public static class Builder extends
   BaseBlobStoreApiMetadata.Builder<BlobStore, FilesystemAsyncBlobStore, BlobStoreContext<BlobStore, FilesystemAsyncBlobStore>, FilesystemApiMetadata> {

      protected Builder() {
         id("filesystem")
         .name("Filesystem-based BlobStore")
         .identityName("Unused")
         .contextBuilder(TypeToken.of(FilesystemBlobStoreContextBuilder.class))
         .javaApi(BlobStore.class, FilesystemAsyncBlobStore.class)
         .identityName("Unused")
         .defaultEndpoint("http://localhost/transient")
         .defaultIdentity(System.getProperty("user.name"))
         .defaultCredential("bar")
         .version("1")
         .defaultProperties(
               BaseBlobStoreApiMetadata.Builder.defaultPropertiesAnd(ImmutableMap.of(PROPERTY_USER_THREADS, "0",
                     PROPERTY_IO_WORKER_THREADS, "0")))
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/blobstore-guide"));
      }

      @Override
      public FilesystemApiMetadata build() {
         return new FilesystemApiMetadata(this);
      }

   }

}