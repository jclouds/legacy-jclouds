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
package org.jclouds.aws.s3.blobstore.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.s3.AWSS3AsyncClient;
import org.jclouds.aws.s3.AWSS3Client;
import org.jclouds.aws.s3.blobstore.AWSS3AsyncBlobStore;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStore;
import org.jclouds.aws.s3.blobstore.AWSS3BlobStoreContext;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;
import org.jclouds.s3.blobstore.internal.S3BlobStoreContextImpl;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSS3BlobStoreContextImpl extends S3BlobStoreContextImpl<AWSS3Client, AWSS3AsyncClient> implements
      AWSS3BlobStoreContext {

   @Inject
   public AWSS3BlobStoreContextImpl(BlobMap.Factory blobMapFactory, Utils utils, ConsistencyModel consistencyModel,
         InputStreamMap.Factory inputStreamMapFactory, AWSS3AsyncBlobStore ablobStore, AWSS3BlobStore blobStore,
         @SuppressWarnings("rawtypes") RestContext providerSpecificContext, BlobRequestSigner blobRequestSigner) {
      super(blobMapFactory, utils, consistencyModel, inputStreamMapFactory, ablobStore, blobStore, providerSpecificContext,
            blobRequestSigner);
   }

   @Override
   public AWSS3BlobStore getBlobStore() {
      return AWSS3BlobStore.class.cast(super.getBlobStore());
   }

   @Override
   public AWSS3AsyncBlobStore getAsyncBlobStore() {
      return AWSS3AsyncBlobStore.class.cast(super.getAsyncBlobStore());
   }

}
