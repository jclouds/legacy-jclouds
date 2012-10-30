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
package org.jclouds.s3.blobstore.config;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.config.BlobStoreMapModule;
import org.jclouds.domain.Location;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.blobstore.S3AsyncBlobStore;
import org.jclouds.s3.blobstore.S3BlobRequestSigner;
import org.jclouds.s3.blobstore.S3BlobStore;
import org.jclouds.s3.blobstore.functions.LocationFromBucketName;
import org.jclouds.s3.domain.AccessControlList;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the {@link S3BlobStoreContext}; requires {@link S3AsyncBlobStore} bound.
 * 
 * @author Adrian Cole
 */
public class S3BlobStoreContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new BlobStoreMapModule());
      bind(ConsistencyModel.class).toInstance(ConsistencyModel.EVENTUAL);
      bind(AsyncBlobStore.class).to(S3AsyncBlobStore.class).in(Scopes.SINGLETON);
      bind(BlobStore.class).to(S3BlobStore.class).in(Scopes.SINGLETON);
      bind(BlobRequestSigner.class).to(S3BlobRequestSigner.class);
      bind(new TypeLiteral<Function<String, Location>>() {
      }).to(LocationFromBucketName.class);
   }

   @Provides
   @Singleton
   protected LoadingCache<String, AccessControlList> bucketAcls(final S3Client client) {
      return CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build(
               new CacheLoader<String, AccessControlList>() {
                  @Override
                  public AccessControlList load(String bucketName) {
                     return client.getBucketACL(bucketName);
                  }

                  @Override
                  public String toString() {
                     return "getBucketAcl()";
                  }
               });
   }
}
