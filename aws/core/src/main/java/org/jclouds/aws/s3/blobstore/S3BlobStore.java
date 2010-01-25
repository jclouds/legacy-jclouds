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

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;
import static org.jclouds.blobstore.util.BlobStoreUtils.keyNotFoundToNullOrPropagate;

import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.aws.s3.blobstore.internal.BaseS3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.GetDirectoryStrategy;
import org.jclouds.blobstore.strategy.MkdirStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class S3BlobStore extends BaseS3BlobStore implements BlobStore {

   @Inject
   public S3BlobStore(S3AsyncClient async, S3Client sync, Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            ObjectToBlobMetadata object2BlobMd, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ContainerToBucketListOptions container2BucketListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions, GetDirectoryStrategy getDirectoryStrategy,
            MkdirStrategy mkdirStrategy, BucketToResourceMetadata bucket2ResourceMd,
            BucketToResourceList bucket2ResourceList,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2BucketListOptions, blob2ObjectGetOptions,
               getDirectoryStrategy, mkdirStrategy, bucket2ResourceMd, bucket2ResourceList, service);
   }

   /**
    * This implementation invokes {@link S3Client#listOwnedBuckets}
    */
   @Override
   public ListResponse<? extends StorageMetadata> list() {
      return new Function<SortedSet<BucketMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.ListResponse<? extends StorageMetadata> apply(
                  SortedSet<BucketMetadata> from) {
            return new ListResponseImpl<StorageMetadata>(Iterables.transform(from,
                     bucket2ResourceMd), null, null, false);
         }
      }.apply(sync.listOwnedBuckets());
   }

   /**
    * This implementation invokes {@link S3Client#bucketExists}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public boolean containerExists(String container) {
      return sync.bucketExists(container);
   }

   /**
    * This implementation invokes {@link S3Client#putBucketInRegion}
    * 
    * @param location
    *           corresponds to {@link Region#fromValue}
    * @param container
    *           bucket name
    */
   @Override
   public boolean createContainerInLocation(String location, String container) {
      return sync.putBucketInRegion(Region.DEFAULT, container);// TODO parameterize
   }

   /**
    * This implementation invokes
    * {@link #list(String,org.jclouds.blobstore.options.ListContainerOptions)}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public ListContainerResponse<? extends StorageMetadata> list(String container) {
      return this.list(container, org.jclouds.blobstore.options.ListContainerOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3Client#listBucket}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public ListContainerResponse<? extends StorageMetadata> list(String container,
            ListContainerOptions optionsList) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(optionsList);
      return bucket2ResourceList.apply(sync.listBucket(container, httpOptions));
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option.
    * 
    * @param container
    *           bucket name
    */
   @Override
   public void clearContainer(String container) {
      clearContainerStrategy.execute(container, recursive());
   }

   /**
    * This implementation invokes {@link ClearListStrategy#execute} with the
    * {@link ListContainerOptions#recursive} option. Then, it invokes
    * {@link S3Client#deleteBucketIfEmpty}
    * 
    * @param container
    *           bucket name
    */
   @Override
   public void deleteContainer(String container) {
      clearContainer(container);
      sync.deleteBucketIfEmpty(container);
   }
   
   /**
    * This implementation invokes {@link GetDirectoryStrategy#execute}
    * 
    * @param container
    *           bucket name
    * @param directory
    *           virtual path
    */
   @Override
   public boolean directoryExists(String containerName, String directory) {
      try {
         getDirectoryStrategy.execute(containerName, directory);
         return true;
      } catch (KeyNotFoundException e) {
         return false;
      }
   }

   /**
    * This implementation invokes {@link MkdirStrategy#execute}
    * 
    * @param container
    *           bucket name
    * @param directory
    *           virtual path
    */
   @Override
   public void createDirectory(String containerName, String directory) {
      mkdirStrategy.execute(containerName, directory);
   }

   /**
    * This implementation invokes {@link S3Client#objectExists}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public boolean blobExists(String container, String key) {
      return sync.objectExists(container, key);
   }

   /**
    * This implementation invokes {@link S3Client#headObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public BlobMetadata blobMetadata(String container, String key) {
      try {
         return object2BlobMd.apply(sync.headObject(container, key));
      } catch (Exception e) {
         return keyNotFoundToNullOrPropagate(e);
      }
   }

   /**
    * This implementation invokes
    * {@link #getBlob(String,String,org.jclouds.blobstore.options.GetOptions)}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public Blob getBlob(String container, String key) {
      return getBlob(container, key, org.jclouds.blobstore.options.GetOptions.NONE);
   }

   /**
    * This implementation invokes {@link S3Client#getObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      try {
         return object2Blob.apply(sync.getObject(container, key, httpOptions));
      } catch (Exception e) {
         return keyNotFoundToNullOrPropagate(e);
      }
   }

   /**
    * This implementation invokes {@link S3Client#putObject}
    * 
    * @param container
    *           bucket name
    * @param blob
    *           object
    */
   @Override
   public String putBlob(String container, Blob blob) {
      return sync.putObject(container, blob2Object.apply(blob));
   }

   /**
    * This implementation invokes {@link S3Client#deleteObject}
    * 
    * @param container
    *           bucket name
    * @param key
    *           object key
    */
   @Override
   public void removeBlob(String container, String key) {
      sync.deleteObject(container, key);
   }

}
