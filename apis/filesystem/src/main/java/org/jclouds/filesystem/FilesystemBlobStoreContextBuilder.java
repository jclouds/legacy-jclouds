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

import java.util.List;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.filesystem.config.FilesystemBlobStoreContextModule;
import org.jclouds.filesystem.config.FilesystemBlobStoreModule;
import org.jclouds.providers.ProviderMetadata;

import com.google.inject.Module;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemBlobStoreContextBuilder
      extends
      BlobStoreContextBuilder<BlobStore, FilesystemAsyncBlobStore, BlobStoreContext<BlobStore, FilesystemAsyncBlobStore>, FilesystemApiMetadata> {

   public FilesystemBlobStoreContextBuilder(
         ProviderMetadata<BlobStore, FilesystemAsyncBlobStore, BlobStoreContext<BlobStore, FilesystemAsyncBlobStore>, FilesystemApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public FilesystemBlobStoreContextBuilder(FilesystemApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   @Override
   public void addContextModule(List<Module> modules) {
      modules.add(new FilesystemBlobStoreContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new FilesystemBlobStoreModule());
   }

}
