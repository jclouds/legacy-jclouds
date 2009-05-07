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
package org.jclouds.aws.s3.internal;

import java.util.List;
import java.util.concurrent.Future;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.commands.BucketExists;
import org.jclouds.aws.s3.commands.CopyObject;
import org.jclouds.aws.s3.commands.DeleteBucket;
import org.jclouds.aws.s3.commands.DeleteObject;
import org.jclouds.aws.s3.commands.GetBucket;
import org.jclouds.aws.s3.commands.GetMetaDataForOwnedBuckets;
import org.jclouds.aws.s3.commands.GetObject;
import org.jclouds.aws.s3.commands.HeadMetaData;
import org.jclouds.aws.s3.commands.PutBucket;
import org.jclouds.aws.s3.commands.PutObject;
import org.jclouds.aws.s3.commands.S3CommandFactory;
import org.jclouds.aws.s3.commands.options.GetBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.MetaData;
import org.jclouds.http.HttpFutureCommandClient;

import com.google.inject.Inject;

/**
 * {@inheritDoc} Uses {@link HttpFutureCommandClient} to invoke the REST API of
 * S3.
 * 
 * @see http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
 * @author Adrian Cole
 */
public class LiveS3Connection implements S3Connection {

    private final HttpFutureCommandClient client;
    /**
     * creates command objects that can be submitted to the client
     */
    private final S3CommandFactory factory;

    @Inject
    public LiveS3Connection(HttpFutureCommandClient client,
	    S3CommandFactory factory) {
	this.client = client;
	this.factory = factory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see GetObject
     */
    public Future<S3Object> getObject(String s3Bucket, String key) {
	GetObject getObject = factory.createGetObject(s3Bucket, key);
	client.submit(getObject);
	return getObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see HeadMetaData
     */
    public Future<S3Object.MetaData> getObjectMetaData(String s3Bucket,
	    String key) {
	HeadMetaData headMetaData = factory.createHeadMetaData(s3Bucket, key);
	client.submit(headMetaData);
	return headMetaData;
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
    public Future<String> addObject(String s3Bucket, S3Object object) {
	PutObject putObject = factory.createPutObject(s3Bucket, object);
	client.submit(putObject);
	return putObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see PutBucket
     */
    public Future<Boolean> createBucketIfNotExists(String s3Bucket) {
	PutBucket putBucket = factory.createPutBucket(s3Bucket);
	client.submit(putBucket);
	return putBucket;
    }

    /**
     * {@inheritDoc}
     * 
     * @see PutBucket
     */
    public Future<Boolean> createBucketIfNotExists(String s3Bucket,
	    PutBucketOptions options) {
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
    public Future<S3Object.MetaData> copyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject) {
	CopyObject copy = factory.createCopyObject(sourceBucket, sourceObject,
		destinationBucket, destinationObject);
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
     * @see GetBucket
     */
    public Future<S3Bucket> getBucket(String s3Bucket) {
	GetBucket getBucket = factory.createGetBucket(s3Bucket);
	client.submit(getBucket);
	return getBucket;
    }

    /**
     * {@inheritDoc}
     * 
     * @see GetBucket
     */
    public Future<S3Bucket> getBucket(String s3Bucket, GetBucketOptions options) {
	GetBucket getBucket = factory.createGetBucket(s3Bucket, options);
	client.submit(getBucket);
	return getBucket;
    }

    /**
     * {@inheritDoc}
     */
    public Future<List<MetaData>> getMetaDataOfOwnedBuckets() {
	GetMetaDataForOwnedBuckets listRequest = factory
		.createGetMetaDataForOwnedBuckets();
	client.submit(listRequest);
	return listRequest;
    }
}
