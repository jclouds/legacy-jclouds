/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.mezeo.pcs.internal;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.mezeo.pcs.PCSAsyncClient;
import org.jclouds.mezeo.pcs.domain.ContainerList;
import org.jclouds.mezeo.pcs.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs.domain.PCSFile;
import org.jclouds.mezeo.pcs.options.PutBlockOptions;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Implementation of {@link PCSBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubPCSAsyncClient implements PCSAsyncClient {
   public ListenableFuture<? extends ContainerList> list() {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<? extends ContainerList> list(URI container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> createContainer(String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> createContainer(URI parent, String container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> deleteContainer(URI container) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> deleteFile(URI file) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<InputStream> downloadFile(URI file) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> uploadFile(URI container, PCSFile object) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<URI> createFile(URI container, PCSFile object) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> uploadBlock(URI file, PCSFile object, PutBlockOptions... options) {
      throw new UnsupportedOperationException();
   }

   public PCSFile newFile() {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<FileInfoWithMetadata> getFileInfo(URI file) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> addMetadataItemToMap(URI resource, String key,
            Map<String, String> map) {
      throw new UnsupportedOperationException();
   }

   public ListenableFuture<Void> putMetadataItem(URI resource, String key, String value) {
      throw new UnsupportedOperationException();
   }

}
