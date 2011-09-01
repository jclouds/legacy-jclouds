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
package org.jclouds.filesystem.config;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.filesystem.FilesystemAsyncBlobStore;
import org.jclouds.filesystem.FilesystemBlobStore;
import org.jclouds.rest.config.RestClientModule;

/**
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemBlobStoreModule extends RestClientModule<FilesystemBlobStore, AsyncBlobStore> {

   public FilesystemBlobStoreModule() {
      super(FilesystemBlobStore.class, AsyncBlobStore.class);
   }

    @Override
    protected void configure() {
        super.configure();
    }



   @Override
   protected void bindAsyncClient() {
      bind(AsyncBlobStore.class).to(FilesystemAsyncBlobStore.class).asEagerSingleton();
   }
}
