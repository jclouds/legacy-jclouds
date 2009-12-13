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
package org.jclouds.vfs.provider.blobstore;

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.provider.AbstractRandomAccessStreamContent;
import org.apache.commons.vfs.util.RandomAccessMode;
import org.jclouds.blobstore.options.GetOptions;
/**
 * @author Adrian Cole
 */
public class BlobStoreRandomAccessContent extends AbstractRandomAccessStreamContent {
   private final BlobStoreFileObject fileObject;
   private final BlobStoreFileSystem fileSystem;

   protected long filePointer = 0;
   private DataInputStream dis = null;

   BlobStoreRandomAccessContent(final BlobStoreFileObject fileObject, RandomAccessMode mode) {
      super(mode);

      this.fileObject = fileObject;
      fileSystem = (BlobStoreFileSystem) this.fileObject.getFileSystem();
   }

   public long getFilePointer() throws IOException {
      return filePointer;
   }

   public void seek(long pos) throws IOException {
      if (pos == filePointer) {
         // no change
         return;
      }

      if (pos < 0) {
         throw new FileSystemException("vfs.provider/random-access-invalid-position.error",
                  new Object[] { new Long(pos) });
      }
      if (dis != null) {
         close();
      }

      filePointer = pos;
   }

   protected DataInputStream getDataInputStream() throws IOException {
      if (dis != null) {
         return dis;
      }

      dis = new DataInputStream(new FilterInputStream((InputStream) fileSystem.blobStore.getBlob(
               fileSystem.container, fileObject.getNameTrimLeadingSlashes(),
               new GetOptions().startAt(filePointer)).getContent()) {
         public int read() throws IOException {
            int ret = super.read();
            if (ret > -1) {
               filePointer++;
            }
            return ret;
         }

         public int read(byte b[]) throws IOException {
            int ret = super.read(b);
            if (ret > -1) {
               filePointer += ret;
            }
            return ret;
         }

         public int read(byte b[], int off, int len) throws IOException {
            int ret = super.read(b, off, len);
            if (ret > -1) {
               filePointer += ret;
            }
            return ret;
         }
      });

      return dis;
   }

   public void close() throws IOException {
      if (dis != null) {
         dis.close();
         dis = null;
      }
   }

   public long length() throws IOException {
      return fileObject.getContent().getSize();
   }
}