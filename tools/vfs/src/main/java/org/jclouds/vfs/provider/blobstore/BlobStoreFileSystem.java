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
package org.jclouds.vfs.provider.blobstore;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.provider.AbstractFileSystem;
import org.jclouds.blobstore.BlobStore;

/**
 * @author Adrian Cole
 */
public class BlobStoreFileSystem extends AbstractFileSystem {
   final BlobStore blobStore;
   final String container;

   public BlobStoreFileSystem(BlobStoreFileName fileName, BlobStore blobStore,
            FileSystemOptions fileSystemOptions) throws FileSystemException {
      super(fileName, null, fileSystemOptions);
      this.container = fileName.getContainer();
      checkArgument(!container.equals(""), "container must not be an empty String");
      this.blobStore = blobStore;
   }

   @SuppressWarnings("unchecked")
   protected void addCapabilities(Collection caps) {
      caps.addAll(BlobStoreFileProvider.capabilities);
   }

   protected FileObject createFile(FileName fileName) throws Exception {
      return new BlobStoreFileObject(fileName, this, blobStore, container);
   }
}
