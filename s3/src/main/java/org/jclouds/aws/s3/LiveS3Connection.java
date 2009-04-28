/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import org.jclouds.aws.s3.commands.CopyObject;
import org.jclouds.aws.s3.commands.DeleteBucket;
import org.jclouds.aws.s3.commands.DeleteObject;
import org.jclouds.aws.s3.commands.HeadBucket;
import org.jclouds.aws.s3.commands.ListAllMyBuckets;
import org.jclouds.aws.s3.commands.ListBucket;
import org.jclouds.aws.s3.commands.PutBucket;
import org.jclouds.aws.s3.commands.PutObject;
import org.jclouds.aws.s3.commands.RetrieveObject;
import org.jclouds.aws.s3.commands.S3CommandFactory;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpFutureCommandClient;

import com.google.inject.Inject;

/**
 * Non-blocking interface to Amazon S3.
 * 
 * @author Adrian Cole
 */
public class LiveS3Connection implements S3Connection {

    /**
     * not all clients are threadsafe, but this connection needs to be.
     */
    private final HttpFutureCommandClient client;
    private final S3CommandFactory factory;

    @Inject
    public LiveS3Connection(HttpFutureCommandClient client,
	    S3CommandFactory factory) {
	this.client = client;
	this.factory = factory;
    }

    public Future<S3Object> getObject(S3Bucket s3Bucket, String key) {
	RetrieveObject getRequestObject = factory.createRetrieveObject(
		s3Bucket, key, true);
	client.submit(getRequestObject);
	return getRequestObject;
    }

    public Future<S3Object> headObject(S3Bucket s3Bucket, String key) {
	RetrieveObject getRequestObject = factory.createRetrieveObject(
		s3Bucket, key, false);
	client.submit(getRequestObject);
	return getRequestObject;
    }

    public Future<Boolean> deleteObject(S3Bucket s3Bucket, String key) {
	DeleteObject deleteObject = factory.createDeleteObject(s3Bucket, key);
	client.submit(deleteObject);
	return deleteObject;
    }

    public Future<String> addObject(S3Bucket s3Bucket, S3Object object) {
	PutObject putObject = factory.createPutObject(s3Bucket, object);
	client.submit(putObject);
	return putObject;
    }

    public Future<Boolean> createBucketIfNotExists(S3Bucket s3Bucket) {
	PutBucket putBucket = factory.createPutBucket(s3Bucket);
	client.submit(putBucket);
	return putBucket;
    }

    public Future<Boolean> deleteBucket(S3Bucket s3Bucket) {
	DeleteBucket deleteBucket = factory.createDeleteBucket(s3Bucket);
	client.submit(deleteBucket);
	return deleteBucket;
    }

    public Future<Boolean> copyObject(S3Bucket sourceBucket,
	    S3Object sourceObject, S3Bucket destinationBucket,
	    S3Object destinationObject) {
	CopyObject copy = factory.createCopyObject(sourceBucket, sourceObject,
		destinationBucket, destinationObject);
	client.submit(copy);
	return copy;
    }

    public Future<Boolean> bucketExists(S3Bucket s3Bucket) {
	HeadBucket headRequestObject = factory.createHeadBucket(s3Bucket);
	client.submit(headRequestObject);
	return headRequestObject;
    }

    public Future<S3Bucket> getBucket(S3Bucket s3Bucket) {
	ListBucket listRequest = factory.createListBucket(s3Bucket);
	client.submit(listRequest);
	return listRequest;
    }

    public Future<List<S3Bucket>> getBuckets() {
	ListAllMyBuckets listRequest = factory.createListAllMyBuckets();
	client.submit(listRequest);
	return listRequest;
    }

    public void close() throws IOException {
	client.close();
    }

}
