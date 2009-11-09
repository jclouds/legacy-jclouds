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
package org.jclouds.aws.s3.blobstore.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.S3BlobStore;
import org.jclouds.aws.s3.config.S3ObjectModule;
import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.lifecycle.Closer;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link S3BlobStoreContext}; requires {@link S3BlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3BlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreObjectModule());
      install(new BlobStoreMapModule());
      install(new S3ObjectModule());
      bind(BlobStore.class).to(S3BlobStore.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   BlobStoreContext<S3Client> provideContext(BlobMap.Factory blobMapFactory,
            InputStreamMap.Factory inputStreamMapFactory, Closer closer, BlobStore blobStore,
            S3Client defaultApi, @S3 URI endPoint,
            @Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String account) {
      return new BlobStoreContextImpl<S3Client>(blobMapFactory, inputStreamMapFactory, closer,
               blobStore, defaultApi, endPoint, account);
   }

}
