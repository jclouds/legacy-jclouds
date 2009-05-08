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
package org.jclouds.aws.s3;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

/**
 * Provides access to S3 via their REST API.
 * 
 * All commands return a Future of the result from S3. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as
 * documented in {@link Future#get()}.
 * 
 * @author Adrian Cole
 */
public interface S3Connection {

    /**
     * Retrieves the object and metadata associated with the key.
     * 
     * @param bucketName
     *            namespace of the object you are retrieving
     * 
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return fully populated S3Object containing data stored in S3
     */
    Future<S3Object> getObject(String bucketName, String key);

    /**
     * Retrieves the object and metadata associated with the key.
     * 
     * @param bucketName
     *            namespace of the object you are retrieving
     * 
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @param options
     *            options for retrieving the object
     * @return fully populated S3Object containing data stored in S3
     */
    Future<S3Object> getObject(String bucketName, String key,
	    GetObjectOptions options);

    /**
     * Retrieves the metadata of the object associated with the key.
     * 
     * @param bucketName
     *            namespace of the metadata you are retrieving
     * 
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return metadata associated with the key
     */
    Future<S3Object.Metadata> headObject(String bucketName, String key);

    /**
     * Removes the object and metadata associated with the key.
     * 
     * @param bucketName
     *            namespace of the object you are deleting
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return true if deleted
     */
    Future<Boolean> deleteObject(String bucketName, String key);

    /**
     * Store data by creating or overwriting an object.
     * 
     * @param bucketName
     *            namespace of the object you are storing
     * @param object
     *            contains the data and metadata to create or overwrite
     * @return MD5 hash of the content uploaded
     */
    Future<byte[]> putObject(String bucketName, S3Object object);

    /**
     * Store data by creating or overwriting an object.
     * 
     * @param bucketName
     *            namespace of the object you are storing
     * @param object
     *            contains the data and metadata to create or overwrite
     * @param options
     *            options for creating the object
     * @return MD5 hash of the content uploaded
     */
    Future<byte[]> putObject(String bucketName, S3Object object,
	    PutObjectOptions options);

    /**
     * Create and name your own bucket in which to store your objects.
     * 
     * @return true, if the bucket was created
     */
    Future<Boolean> putBucketIfNotExists(String name);

    /**
     * Create and name your own bucket in which to store your objects.
     * 
     * @param options
     *            for creating your bucket
     * @return true, if the bucket was created
     * @see PutBucketOptions
     */
    Future<Boolean> putBucketIfNotExists(String name, PutBucketOptions options);

    /**
     * Deletes the bucket, if it is empty.
     * 
     * @param s3Bucket
     *            what to delete
     * @return false, if the bucket was not empty and therefore not deleted
     */
    Future<Boolean> deleteBucketIfEmpty(String s3Bucket);

    /**
     * Copies one object to another bucket
     * 
     * @return metaData populated with lastModified and etag of the new object
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject);

    /**
     * Copies one object to another bucket using the specifid options
     * 
     * @return metaData populated with lastModified and etag of the new object
     * @see CopyObjectOptions
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject, CopyObjectOptions options);

    Future<Boolean> bucketExists(String name);

    /**
     * 
     * @param s3Bucket
     * @return
     */
    Future<S3Bucket> listBucket(String name);

    Future<S3Bucket> listBucket(String name, ListBucketOptions options);

    Future<List<S3Bucket.Metadata>> getOwnedBuckets();
}
