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
package org.jclouds.aws.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.options.ListOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.options.ListOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@ConsistencyModel(ConsistencyModels.EVENTUAL)
public class S3BlobStore implements BlobStore {
   private final S3Client connection;
   private final Blob.Factory blobFactory;
   private final LoggerFactory logFactory;
   private final ClearListStrategy clearContainerStrategy;
   private final ObjectToBlobMetadata object2BlobMd;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final ContainerToBucketListOptions container2BucketListOptions;
   private final BlobToObjectGetOptions blob2ObjectGetOptions;
   private final BucketToResourceMetadata bucket2ResourceMd;
   private final BucketToResourceList bucket2ResourceList;
   private final ExecutorService service;

   @Inject
   private S3BlobStore(S3Client connection, Blob.Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            ContainerToBucketListOptions container2BucketListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            BucketToResourceMetadata bucket2ResourceMd, BucketToResourceList bucket2ResourceList,
            ExecutorService service) {
      this.connection = checkNotNull(connection, "connection");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
      this.logFactory = checkNotNull(logFactory, "logFactory");
      this.clearContainerStrategy = checkNotNull(clearContainerStrategy, "clearContainerStrategy");
      this.object2BlobMd = checkNotNull(object2BlobMd, "object2BlobMd");
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.container2BucketListOptions = checkNotNull(container2BucketListOptions,
               "container2BucketListOptions");
      this.blob2ObjectGetOptions = checkNotNull(blob2ObjectGetOptions, "blob2ObjectGetOptions");
      this.bucket2ResourceMd = checkNotNull(bucket2ResourceMd, "bucket2ResourceMd");
      this.bucket2ResourceList = checkNotNull(bucket2ResourceList, "bucket2ResourceList");
      this.service = checkNotNull(service, "service");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   /**
    * This implementation uses the S3 HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(connection.headObject(container, key));
   }

   public Future<Void> clearContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      });
   }

   public Future<Boolean> createContainer(String container) {
      return connection.putBucketIfNotExists(container);
   }

   public Future<Void> deleteContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            connection.deleteBucketIfEmpty(container).get();
            return null;
         }

      });
   }

   public boolean exists(String container) {
      return connection.bucketExists(container);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<S3Object> returnVal = connection.getObject(container, key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends SortedSet<? extends ResourceMetadata>> list() {
      return wrapFuture(connection.listOwnedBuckets(),
               new Function<SortedSet<BucketMetadata>, SortedSet<? extends ResourceMetadata>>() {
                  public SortedSet<? extends ResourceMetadata> apply(SortedSet<BucketMetadata> from) {
                     return Sets.newTreeSet(Iterables.transform(from, bucket2ResourceMd));
                  }
               });
   }

   public Future<? extends BoundedSortedSet<? extends ResourceMetadata>> list(String container,
            ListOptions... optionsList) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(optionsList);
      Future<ListBucketResponse> returnVal = connection.listBucket(container, httpOptions);
      return wrapFuture(returnVal, bucket2ResourceList);
   }

   public Future<String> putBlob(String container, Blob blob) {
      return connection.putObject(container, blob2Object.apply(blob));
   }

   public Future<Void> removeBlob(String container, String key) {
      return connection.deleteObject(container, key);
   }

   public Blob newBlob() {
      return blobFactory.create(null);
   }

}
