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
package org.jclouds.aws.s3.commands;

import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.xml.S3ParserFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Assembles the command objects for S3.
 * 
 * @author Adrian Cole
 */
public class S3CommandFactory {
   @Inject
   private S3ParserFactory parserFactory;

   @Inject
   private DeleteBucketFactory deleteBucketFactory;

   public static interface DeleteBucketFactory {
      DeleteBucket create(String bucket);
   }

   public DeleteBucket createDeleteBucket(String bucket) {
      return deleteBucketFactory.create(bucket);
   }

   @Inject
   private DeleteObjectFactory deleteObjectFactory;

   public static interface DeleteObjectFactory {
      DeleteObject create(@Assisted("bucketName") String bucket, @Assisted("key") String key);
   }

   public DeleteObject createDeleteObject(String bucket, String key) {
      return deleteObjectFactory.create(bucket, key);
   }

   @Inject
   private BucketExistsFactory headBucketFactory;

   public static interface BucketExistsFactory {
      BucketExists create(String bucket);
   }

   public BucketExists createHeadBucket(String bucket) {
      return headBucketFactory.create(bucket);
   }

   @Inject
   private PutBucketFactory putBucketFactoryOptions;

   public static interface PutBucketFactory {
      PutBucket create(String bucket, PutBucketOptions options);
   }

   public PutBucket createPutBucket(String bucket, PutBucketOptions options) {
      return putBucketFactoryOptions.create(bucket, options);
   }

   @Inject
   private PutObjectFactory putObjectFactory;

   public static interface PutObjectFactory {
      PutObject create(String bucket, S3Object object, PutObjectOptions options);
   }

   public PutObject createPutObject(String bucket, S3Object s3Object, PutObjectOptions options) {
      return putObjectFactory.create(bucket, s3Object, options);
   }

   @Inject
   private GetObjectFactory getObjectFactory;

   public static interface GetObjectFactory {
      GetObject create(@Assisted("bucketName") String bucket, @Assisted("key") String key,
               GetObjectOptions options);
   }

   public GetObject createGetObject(String bucket, String key, GetObjectOptions options) {
      return getObjectFactory.create(bucket, key, options);
   }

   @Inject
   private HeadMetadataFactory headMetadataFactory;

   public static interface HeadMetadataFactory {
      HeadObject create(@Assisted("bucketName") String bucket, @Assisted("key") String key);
   }

   public HeadObject createHeadMetadata(String bucket, String key) {
      return headMetadataFactory.create(bucket, key);
   }

   @Inject
   @Named("jclouds.http.address")
   String amazonHost;

   public ListOwnedBuckets createGetMetadataForOwnedBuckets() {
      return new ListOwnedBuckets(amazonHost, parserFactory.createListBucketsParser());
   }

   public ListBucket createListBucket(String bucket, ListBucketOptions options) {
      return new ListBucket(amazonHost, parserFactory.createListBucketParser(), bucket, options);
   }

   public CopyObject createCopyObject(String sourceBucket, String sourceObject,
            String destinationBucket, String destinationObject, CopyObjectOptions options) {
      return new CopyObject(amazonHost, parserFactory.createCopyObjectParser(), sourceBucket,
               sourceObject, destinationBucket, destinationObject, options);
   }

   public GetAccessControlList createGetBucketACL(String bucket) {
      return new GetAccessControlList(
            amazonHost, parserFactory.createAccessControlListParser(), bucket);
   }

   public GetAccessControlList createGetObjectACL(String bucket, String objectKey) {
      return new GetAccessControlList(
            amazonHost, parserFactory.createAccessControlListParser(), bucket, objectKey);
   }

}