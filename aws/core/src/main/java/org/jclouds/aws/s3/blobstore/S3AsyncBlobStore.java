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
package org.jclouds.aws.s3.blobstore;

import static com.google.common.util.concurrent.Futures.compose;
import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.concurrent.internal.ConcurrentUtils.makeListenable;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.aws.s3.blobstore.internal.BaseS3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class S3AsyncBlobStore extends BaseS3BlobStore implements AsyncBlobStore {

   @Inject
   public S3AsyncBlobStore(S3AsyncClient async, S3Client sync, Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            ObjectToBlobMetadata object2BlobMd, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ContainerToBucketListOptions container2BucketListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            GetDirectoryStrategy getDirectoryStrategy, MkdirStrategy mkdirStrategy,
            BucketToResourceMetadata bucket2ResourceMd, BucketToResourceList bucket2ResourceList,
            ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2BucketListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, bucket2ResourceMd, bucket2ResourceList, service);
   }

   /**
    * This implementation uses the S3 HEAD Object command to return the result
    */
   public ListenableFuture<BlobMetadata> blobMetadata(String container, String key) {
      return compose(makeListenable(async.headObject(container, key), service),
               new Function<ObjectMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(ObjectMetadata from) {
                     return object2BlobMd.apply(from);
                  }

               }, service);
   }

   public ListenableFuture<Void> clearContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      }), service);
   }

   public ListenableFuture<Void> deleteContainer(final String container) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deleteBucketIfEmpty(container).get();
            return null;
         }

      }), service);
   }

   public ListenableFuture<Boolean> createContainerInLocation(String location, String container) {
      return async.putBucketInRegion(Region.DEFAULT, container);// TODO
   }

   public ListenableFuture<Boolean> containerExists(String container) {
      return async.bucketExists(container);
   }

   public ListenableFuture<Void> createDirectory(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            mkdirStrategy.execute(S3AsyncBlobStore.this, container, directory);
            return null;
         }

      }), service);
   }

   public ListenableFuture<Boolean> directoryExists(final String container, final String directory) {
      return makeListenable(service.submit(new Callable<Boolean>() {

         public Boolean call() throws Exception {
            try {
               getDirectoryStrategy.execute(S3AsyncBlobStore.this, container, directory);
               return true;
            } catch (KeyNotFoundException e) {
               return false;
            }
         }

      }), service);
   }

   public ListenableFuture<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return compose(async.getObject(container, key, httpOptions), object2Blob, service);
   }

   public ListenableFuture<? extends ListResponse<? extends StorageMetadata>> list() {
      return compose(
               async.listOwnedBuckets(),
               new Function<SortedSet<BucketMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata> apply(
                           SortedSet<BucketMetadata> from) {
                     return new ListResponseImpl<StorageMetadata>(Iterables.transform(from,
                              bucket2ResourceMd), null, null, false);
                  }
               }, service);
   }

   public ListenableFuture<? extends ListContainerResponse<? extends StorageMetadata>> list(
            String container, ListContainerOptions... optionsList) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(optionsList);
      ListenableFuture<ListBucketResponse> returnVal = async.listBucket(container, httpOptions);
      return compose(returnVal, bucket2ResourceList, service);
   }

   public ListenableFuture<String> putBlob(String container, Blob blob) {
      return async.putObject(container, blob2Object.apply(blob));
   }

   public ListenableFuture<Void> removeBlob(String container, String key) {
      return async.deleteObject(container, key);
   }

}
