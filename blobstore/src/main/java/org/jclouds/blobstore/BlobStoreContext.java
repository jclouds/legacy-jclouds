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
package org.jclouds.blobstore;

import java.io.Closeable;

import org.jclouds.View;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.rest.Utils;

import com.google.inject.ImplementedBy;

/**
 * Represents a cloud that has key-value storage functionality. This object is scoped to a service
 * and an identity.
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(BlobStoreContextImpl.class)
public interface BlobStoreContext extends Closeable, View {
   /**
    * 
    * Generates signed requests for blobs. useful in other tools such as backup utilities.
    * 
    */
   BlobRequestSigner getSigner();

   /**
    * Creates a <code>Map<String,InputStream></code> view of the specified container. Use this for
    * simplest access to blobstore, knowing that MD5s will be calculated for every object.
    * 
    * @param container
    *           existing container you wish to read or modify
    * @param options
    *           allow you to specify a directory within the container, or whether to list
    *           recursively.
    */
   InputStreamMap createInputStreamMap(String container, ListContainerOptions options);

   /**
    * Creates a <code>Map<String,InputStream></code> view of the specified container. Use this for
    * simplest access to blobstore, knowing that MD5s will be calculated for every object.
    * 
    * Only root-level blobs will be visible.
    * 
    * @param container
    *           existing container you wish to read or modify
    */
   InputStreamMap createInputStreamMap(String container);

   /**
    * Creates a <code>Map<String,Blob></code> view of the specified container. Use this when you wan
    * to control the content type, or manually specify length or size of blobs.
    * 
    * @param container
    *           existing container you wish to read or modify
    * @param options
    *           allow you to specify a directory within the container, or whether to list
    *           recursively.
    */
   BlobMap createBlobMap(String container, ListContainerOptions options);

   /**
    * Creates a <code>Map<String,Blob></code> view of the specified container. Use this when you wan
    * to control the content type, or manually specify length or size of blobs.
    * 
    * Only root-level blobs will be visible.
    * 
    * @param container
    *           existing container you wish to read or modify
    */
   BlobMap createBlobMap(String container);

   /**
    * @return a portable asynchronous interface for the BlobStore, which returns {@code Future}s for
    *         each call.
    */
   AsyncBlobStore getAsyncBlobStore();

   /**
    * @return a portable interface for the BlobStore.
    */
   BlobStore getBlobStore();

   /**
    * 
    * @return best guess at the consistency model used in this BlobStore.
    */
   ConsistencyModel getConsistencyModel();


   Utils getUtils();

   /**
    * @see #getUtils
    */
   Utils utils();

   /**
    * closes threads and resources related to this connection.
    * 
    */
   @Override
   void close();
}
