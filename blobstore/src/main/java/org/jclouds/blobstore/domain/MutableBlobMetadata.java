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
package org.jclouds.blobstore.domain;

import java.net.URI;

import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.javax.annotation.Nullable;

import com.google.inject.ImplementedBy;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(MutableBlobMetadataImpl.class)
public interface MutableBlobMetadata extends BlobMetadata, MutableStorageMetadata {
   /**
    * {@inheritDoc}
    */
   @Override
   MutableContentMetadata getContentMetadata();

   /**
    * @see BlobMetadata#getContentMetadata
    */
   void setContentMetadata(MutableContentMetadata md);

   /**
    * @see BlobMetadata#getPublicUri
    */
   void setPublicUri(@Nullable URI publicUri);

   /**
    * @see BlobMetadata#getContainer
    */
   void setContainer(@Nullable String container);
}
