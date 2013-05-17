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
package org.jclouds.filesystem;

import java.net.URI;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.config.FilesystemBlobStoreContextModule;

/**
 * Implementation of {@link ApiMetadata} for jclouds Filesystem-based BlobStore
 * 
 * @author Adrian Cole
 */
public class FilesystemApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public FilesystemApiMetadata() {
      super(new Builder());
   }

   protected FilesystemApiMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {

      protected Builder() {
         id("filesystem")
         .name("Filesystem-based BlobStore")
         .identityName("Unused")
         .defaultEndpoint("http://localhost/transient")
         .defaultIdentity(System.getProperty("user.name"))
         .defaultCredential("bar")
         .version("1")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/blobstore-guide"))
         .view(BlobStoreContext.class)
         .defaultModule(FilesystemBlobStoreContextModule.class);
      }

      @Override
      public FilesystemApiMetadata build() {
         return new FilesystemApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
