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
package org.jclouds.aws.s3.commands;

import java.util.List;

import org.jclouds.aws.s3.commands.callables.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.commands.callables.xml.ListBucketHandler;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class S3CommandFactory {

    @Inject
    private CopyObjectFactory copyObjectFactory;

    public static interface CopyObjectFactory {
	CopyObject create(@Assisted("sourceBucket") S3Bucket sourceBucket,
		@Assisted("sourceObject") S3Object sourceObject,
		@Assisted("destinationBucket") S3Bucket destinationBucket,
		@Assisted("destinationObject") S3Object destinationObject);
    }

    public CopyObject createCopyObject(S3Bucket sourceBucket,
	    S3Object sourceObject, S3Bucket destinationBucket,
	    S3Object destinationObject) {
	return copyObjectFactory.create(sourceBucket, sourceObject,
		destinationBucket, destinationObject);
    }

    @Inject
    private DeleteBucketFactory deleteBucketFactory;

    public static interface DeleteBucketFactory {
	DeleteBucket create(S3Bucket s3Bucket);
    }

    public DeleteBucket createDeleteBucket(S3Bucket s3Bucket) {
	return deleteBucketFactory.create(s3Bucket);
    }

    @Inject
    private DeleteObjectFactory deleteObjectFactory;

    public static interface DeleteObjectFactory {
	DeleteObject create(S3Bucket s3Bucket, String key);
    }

    public DeleteObject createDeleteObject(S3Bucket s3Bucket, String key) {
	return deleteObjectFactory.create(s3Bucket, key);
    }

    @Inject
    private BucketExistsFactory headBucketFactory;

    public static interface BucketExistsFactory {
	BucketExists create(S3Bucket s3Bucket);
    }

    public BucketExists createHeadBucket(S3Bucket s3Bucket) {
	return headBucketFactory.create(s3Bucket);
    }

    @Inject
    private PutBucketFactory putBucketFactory;

    public static interface PutBucketFactory {
	PutBucket create(S3Bucket s3Bucket);
    }

    public PutBucket createPutBucket(S3Bucket s3Bucket) {
	return putBucketFactory.create(s3Bucket);
    }

    @Inject
    private PutObjectFactory putObjectFactory;

    public static interface PutObjectFactory {
	PutObject create(S3Bucket s3Bucket, S3Object object);
    }

    public PutObject createPutObject(S3Bucket s3Bucket, S3Object s3Object) {
	return putObjectFactory.create(s3Bucket, s3Object);
    }

    @Inject
    private GetObjectFactory getObjectFactory;

    public static interface GetObjectFactory {
	GetObject create(S3Bucket s3Bucket, String key);
    }

    public GetObject createGetObject(S3Bucket s3Bucket, String key) {
	return getObjectFactory.create(s3Bucket, key);
    }

    @Inject
    private HeadMetaDataFactory headMetaDataFactory;

    public static interface HeadMetaDataFactory {
	HeadMetaData create(S3Bucket s3Bucket, String key);
    }

    public HeadMetaData createHeadMetaData(S3Bucket s3Bucket, String key) {
	return headMetaDataFactory.create(s3Bucket, key);
    }

    @Inject
    @Named("jclouds.http.address")
    String amazonHost;

    @Inject
    private GenericParseFactory<List<S3Bucket>> parseListAllMyBucketsFactory;

    public static interface GenericParseFactory<T> {
	ParseSax<T> create(ParseSax.HandlerWithResult<T> handler);
    }

    @Inject
    Provider<ListAllMyBucketsHandler> ListAllMyBucketsHandlerprovider;

    @VisibleForTesting
    public ParseSax<List<S3Bucket>> createListBucketsParser() {
	return parseListAllMyBucketsFactory
		.create(ListAllMyBucketsHandlerprovider.get());
    }

    public ListAllMyBuckets createListAllMyBuckets() {
	return new ListAllMyBuckets(amazonHost, createListBucketsParser());
    }

    @Inject
    private GenericParseFactory<S3Bucket> parseListBucketFactory;

    @Inject
    Provider<ListBucketHandler> ListBucketHandlerprovider;

    @VisibleForTesting
    public ParseSax<S3Bucket> createListBucketParser() {
	return parseListBucketFactory.create(ListBucketHandlerprovider.get());
    }

    public ListBucket createListBucket(S3Bucket bucket) {
	return new ListBucket(amazonHost, createListBucketParser(), bucket);
    }
}