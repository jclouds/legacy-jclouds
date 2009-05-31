/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.aws.s3.internal;

import java.util.List;
import java.util.concurrent.Future;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.commands.BucketExists;
import org.jclouds.aws.s3.commands.CopyObject;
import org.jclouds.aws.s3.commands.DeleteBucket;
import org.jclouds.aws.s3.commands.DeleteObject;
import org.jclouds.aws.s3.commands.GetAccessControlList;
import org.jclouds.aws.s3.commands.ListOwnedBuckets;
import org.jclouds.aws.s3.commands.GetObject;
import org.jclouds.aws.s3.commands.HeadObject;
import org.jclouds.aws.s3.commands.ListBucket;
import org.jclouds.aws.s3.commands.PutBucket;
import org.jclouds.aws.s3.commands.PutObject;
import org.jclouds.aws.s3.commands.S3CommandFactory;
import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata;
import org.jclouds.http.HttpFutureCommandClient;

import com.google.inject.Inject;

/**
 * Uses {@link HttpFutureCommandClient} to invoke the REST API of S3.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?" />
 * @author Adrian Cole
 */
public class LiveS3Connection implements S3Connection {

   private final HttpFutureCommandClient client;
   /**
    * creates command objects that can be submitted to the client
    */
   private final S3CommandFactory factory;

   @Inject
   public LiveS3Connection(HttpFutureCommandClient client, S3CommandFactory factory) {
      this.client = client;
      this.factory = factory;
   }

   /**
    * {@inheritDoc}
    * 
    * @see GetObject
    */
   public Future<S3Object> getObject(String s3Bucket, String key) {
      return getObject(s3Bucket, key, GetObjectOptions.NONE);
   }

   /**
    * {@inheritDoc}
    * 
    * @see GetObject
    */
   public Future<S3Object> getObject(String s3Bucket, String key, GetObjectOptions options) {
      GetObject getObject = factory.createGetObject(s3Bucket, key, options);
      client.submit(getObject);
      return getObject;
   }

   /**
    * {@inheritDoc}
    * 
    * @see HeadObject
    */
   public Future<S3Object.Metadata> headObject(String s3Bucket, String key) {
      HeadObject headMetadata = factory.createHeadMetadata(s3Bucket, key);
      client.submit(headMetadata);
      return headMetadata;
   }

   /**
    * {@inheritDoc}
    * 
    * @see DeleteObject
    */
   public Future<Boolean> deleteObject(String s3Bucket, String key) {
      DeleteObject deleteObject = factory.createDeleteObject(s3Bucket, key);
      client.submit(deleteObject);
      return deleteObject;
   }

   /**
    * {@inheritDoc}
    * 
    * @see PutObject
    */
   public Future<byte[]> putObject(String s3Bucket, S3Object object) {
      return putObject(s3Bucket, object, PutObjectOptions.NONE);
   }

   /**
    * {@inheritDoc}
    * 
    * @see PutObject
    */
   public Future<byte[]> putObject(String bucketName, S3Object object, PutObjectOptions options) {
      PutObject putObject = factory.createPutObject(bucketName, object, options);
      client.submit(putObject);
      return putObject;
   }

   /**
    * {@inheritDoc}
    * 
    * @see PutBucket
    */
   public Future<Boolean> putBucketIfNotExists(String s3Bucket) {
      return putBucketIfNotExists(s3Bucket, PutBucketOptions.NONE);
   }

   /**
    * {@inheritDoc}
    * 
    * @see PutBucket
    */
   public Future<Boolean> putBucketIfNotExists(String s3Bucket, PutBucketOptions options) {
      PutBucket putBucket = factory.createPutBucket(s3Bucket, options);
      client.submit(putBucket);
      return putBucket;
   }

   /**
    * {@inheritDoc}
    * 
    * @see DeleteBucket
    */
   public Future<Boolean> deleteBucketIfEmpty(String s3Bucket) {
      DeleteBucket deleteBucket = factory.createDeleteBucket(s3Bucket);
      client.submit(deleteBucket);
      return deleteBucket;
   }

   /**
    * {@inheritDoc}
    * 
    * @see CopyObject
    */
   public Future<S3Object.Metadata> copyObject(String sourceBucket, String sourceObject,
            String destinationBucket, String destinationObject) {
      return copyObject(sourceBucket, sourceObject, destinationBucket, destinationObject,
               new CopyObjectOptions());
   }

   /**
    * {@inheritDoc}
    * 
    * @see CopyObject
    */
   public Future<S3Object.Metadata> copyObject(String sourceBucket, String sourceObject,
            String destinationBucket, String destinationObject, CopyObjectOptions options) {
      CopyObject copy = factory.createCopyObject(sourceBucket, sourceObject, destinationBucket,
               destinationObject, options);
      client.submit(copy);
      return copy;
   }

   /**
    * {@inheritDoc}
    * 
    * @see BucketExists
    */
   public Future<Boolean> bucketExists(String s3Bucket) {
      BucketExists headRequestObject = factory.createHeadBucket(s3Bucket);
      client.submit(headRequestObject);
      return headRequestObject;
   }

   /**
    * {@inheritDoc}
    * 
    * @see ListBucket
    */
   public Future<S3Bucket> listBucket(String s3Bucket) {
      return listBucket(s3Bucket, ListBucketOptions.NONE);
   }

   /**
    * {@inheritDoc}
    * 
    * @see ListBucket
    */
   public Future<S3Bucket> listBucket(String s3Bucket, ListBucketOptions options) {
      ListBucket getBucket = factory.createListBucket(s3Bucket, options);
      client.submit(getBucket);
      return getBucket;
   }

   /**
    * {@inheritDoc}
    * 
    * @see ListOwnedBuckets
    */
   public Future<List<Metadata>> listOwnedBuckets() {
      ListOwnedBuckets listRequest = factory.createGetMetadataForOwnedBuckets();
      client.submit(listRequest);
      return listRequest;
   }

   /**
    * {@inheritDoc}
    * 
    * @see GetAccessControlList
    */
   public Future<AccessControlList> getBucketACL(String bucket) {
      GetAccessControlList getBucketACLRequest = factory.createGetBucketACL(bucket);
      client.submit(getBucketACLRequest);
      return getBucketACLRequest;
   }

   /**
    * {@inheritDoc}
    * 
    * @see GetAccessControlList
    */
   public Future<AccessControlList> getObjectACL(String bucket, String objectKey) {
      GetAccessControlList getObjectACLRequest = factory.createGetObjectACL(bucket, objectKey);
      client.submit(getObjectACLRequest);
      return getObjectACLRequest;
   }

}
