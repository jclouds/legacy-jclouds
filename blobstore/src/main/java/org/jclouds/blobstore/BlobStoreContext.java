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
    * @return a portable asynchronous interface for the BlobStore, which returns
    *         {@code Future}s for each call.
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported. Please use
    *             {@link #getBlobStore()}
    */
   @Deprecated
   AsyncBlobStore getAsyncBlobStore();

   /**
    * @return a portable interface for the BlobStore.
    */
   BlobStore getBlobStore();

   /**
    * 
    * @return best guess at the consistency model used in this BlobStore.
    * @deprecated will be removed in jclouds 1.7, as almost never correct.
    */
   @Deprecated
   ConsistencyModel getConsistencyModel();

   Utils utils();

   /**
    * closes threads and resources related to this connection.
    * 
    */
   @Override
   void close();
}
