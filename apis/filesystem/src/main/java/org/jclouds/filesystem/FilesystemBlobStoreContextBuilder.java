/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.filesystem;

import com.google.inject.Module;
import java.util.List;
import java.util.Properties;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContextBuilder;
import org.jclouds.filesystem.config.FilesystemBlobStoreContextModule;
import org.jclouds.filesystem.config.FilesystemBlobStoreModule;

/**
 *
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FilesystemBlobStoreContextBuilder extends
         BlobStoreContextBuilder<BlobStore, AsyncBlobStore> {

   /**
    * This is only to have the same syntax.
    *
    */
   public FilesystemBlobStoreContextBuilder() {
      this(new Properties());
   }

   public FilesystemBlobStoreContextBuilder(Properties props) {
      super(BlobStore.class, AsyncBlobStore.class, props);
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
