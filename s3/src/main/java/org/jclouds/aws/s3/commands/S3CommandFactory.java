/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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

import org.jclouds.aws.s3.commands.options.CreateBucketOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.xml.S3ParserFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * // TODO: Adrian: Document this!
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
	DeleteObject create(@Assisted("bucketName") String bucket,
		@Assisted("key") String key);
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
    private PutBucketFactory putBucketFactory;

    public static interface PutBucketFactory {
	PutBucket create(String bucket);
    }

    public PutBucket createPutBucket(String bucket) {
	return putBucketFactory.create(bucket);
    }

    @Inject
    private PutBucketFactoryOptions putBucketFactoryOptions;

    public static interface PutBucketFactoryOptions {
	PutBucket create(String bucket, CreateBucketOptions options);
    }

    public PutBucket createPutBucket(String bucket, CreateBucketOptions options) {
	return putBucketFactoryOptions.create(bucket, options);
    }

    @Inject
    private PutObjectFactory putObjectFactory;

    public static interface PutObjectFactory {
	PutObject create(String bucket, S3Object object);
    }

    public PutObject createPutObject(String bucket, S3Object s3Object) {
	return putObjectFactory.create(bucket, s3Object);
    }

    @Inject
    private GetObjectFactory getObjectFactory;

    public static interface GetObjectFactory {
	GetObject create(@Assisted("bucketName") String bucket,
		@Assisted("key") String key);
    }

    public GetObject createGetObject(String bucket, String key) {
	return getObjectFactory.create(bucket, key);
    }

    @Inject
    private HeadMetaDataFactory headMetaDataFactory;

    public static interface HeadMetaDataFactory {
	HeadMetaData create(@Assisted("bucketName") String bucket,
		@Assisted("key") String key);
    }

    public HeadMetaData createHeadMetaData(String bucket, String key) {
	return headMetaDataFactory.create(bucket, key);
    }

    @Inject
    @Named("jclouds.http.address")
    String amazonHost;

    public GetMetaDataForOwnedBuckets createGetMetaDataForOwnedBuckets() {
	return new GetMetaDataForOwnedBuckets(amazonHost, parserFactory
		.createListBucketsParser());
    }

    public GetBucket createGetBucket(String bucket) {
	return new GetBucket(amazonHost,
		parserFactory.createListBucketParser(), bucket);
    }

    public CopyObject createCopyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject) {
	return new CopyObject(amazonHost, parserFactory
		.createCopyObjectParser(), sourceBucket, sourceObject,
		destinationBucket, destinationObject);
    }

}