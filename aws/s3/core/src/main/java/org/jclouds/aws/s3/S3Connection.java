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
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAPI.html" />
 * 
 * @author Adrian Cole
 */
public interface S3Connection {

    /**
     * Retrieve a complete <code>S3Object</code>.
     * 
     * @see GetObject
     * @param bucketName
     *            namespace of the object you are retrieving
     * 
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return Future reference to a fully populated S3Object including data
     *         stored in S3 or {@link S3Object#NOT_FOUND} if not present.
     */
    Future<S3Object> getObject(String bucketName, String key);

    /**
     * Like {@link #getObject(String, String)} except you can use
     * {@link GetObjectOptions} to control delivery.
     * 
     * @see #getObject(String, String)
     * @see GetObjectOptions
     * @return S3Object containing data relevant to the
     *         <code>options</options> specified or {@link S3Object#NOT_FOUND} if not present.
     * 
     * @throws HttpResponseException
     *             if the conditions requested set were not satisfied by the
     *             object on the server.
     */
    Future<S3Object> getObject(String bucketName, String key,
	    GetObjectOptions options);

    /**
     * Retrieves the {@link S3Object.Metadata metadata} of the object associated
     * with the key.
     * 
     * @see HeadObject
     * @param bucketName
     *            namespace of the metadata you are retrieving
     * 
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return metadata associated with the key or
     *         {@link S3Object.Metadata#NOT_FOUND} if not present;
     */
    Future<S3Object.Metadata> headObject(String bucketName, String key);

    /**
     * Removes the object and metadata associated with the key.
     * 
     * @see DeleteObject
     * @param bucketName
     *            namespace of the object you are deleting
     * @param key
     *            unique key in the s3Bucket identifying the object
     * @return true if deleted
     * @throws HttpResponseException
     *             if the bucket is not available
     */
    Future<Boolean> deleteObject(String bucketName, String key);

    /**
     * Store data by creating or overwriting an object.
     * <p/>
     * This method will store the object with the default <code>private</code>
     * acl.
     * 
     * @see CannedAccessPolicy#PRIVATE
     * @see PutObject
     * @param bucketName
     *            namespace of the object you are storing
     * @param object
     *            contains the data and metadata to create or overwrite
     * @return MD5 hash of the content uploaded
     */
    Future<byte[]> putObject(String bucketName, S3Object object);

    /**
     * Like {@link #putObject(String, S3Object)} except you can use
     * {@link CopyObjectOptions} to specify an alternate
     * {@link CannedAccessPolicy acl}, override
     * {@link S3Object.Metadata#getUserMetadata() userMetadata}, or specify
     * conditions for copying the object.
     * 
     * @see S3Connection#putObject(String, S3Object)
     * @see PutObjectOptions
     * @param options
     *            options for creating the object
     * @throws HttpResponseException
     *             if the conditions requested set are not satisfied by the
     *             object on the server.
     */
    Future<byte[]> putObject(String bucketName, S3Object object,
	    PutObjectOptions options);

    /**
     * Create and name your own bucket in which to store your objects.
     * 
     * @see PutBucket
     * @return true, if the bucket was created or already exists
     */
    Future<Boolean> putBucketIfNotExists(String name);

    /**
     * Like {@link #putBucketIfNotExists(String)} except that you can use
     * {@link PutBucketOptions} to create the bucket in EU. Create and name your
     * 
     * @see PutBucketOptions
     * @param options
     *            for creating your bucket
     */
    Future<Boolean> putBucketIfNotExists(String name, PutBucketOptions options);

    /**
     * Deletes the bucket, if it is empty.
     * 
     * @see DeleteBucket
     * @param s3Bucket
     *            what to delete
     * @return false, if the bucket was not empty and therefore not deleted
     */
    Future<Boolean> deleteBucketIfEmpty(String s3Bucket);

    /**
     * Copies one object to another bucket, retaining UserMetadata from the
     * source. The destination will have a private acl.
     * 
     * @see CopyObject
     * @return metadata populated with lastModified and md5 of the new object
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject);

    /**
     * Like {@link #putObject(String, S3Object)} except you can use
     * {@link PutObjectOptions} to specify an alternate
     * {@link CannedAccessPolicy acl}.
     * 
     * @see S3Connection#putObject(String, S3Object)
     * @see PutObjectOptions
     * @param options
     *            options for creating the object
     * @throws HttpResponseException
     *             if the conditions requested set are not satisfied by the
     *             object on the server.
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
	    String sourceObject, String destinationBucket,
	    String destinationObject, CopyObjectOptions options);

    /**
     * @see HeadBucket
     */
    Future<Boolean> bucketExists(String name);

    /**
     * Retrieve a complete <code>S3Bucket</code> listing.
     * 
     * @see ListBucket
     * @param bucketName
     *            namespace of the objects you wish to list
     * 
     * @return Future reference to a fully populated S3Bucket including metadata
     *         of the S3Objects it contains or {@link S3Bucket#NOT_FOUND} if not
     *         present.
     */
    Future<S3Bucket> listBucket(String bucketName);

    /**
     * Like {@link #listBucket(String)} except you can use
     * {@link ListObjectOptions} to control the amount of S3Objects to return.
     * 
     * @see #listBucket(String)
     * @see ListBucketOptions
     * @return S3Bucket containing a subset of {@link S3Object.Metadata}
     *         depending on
     *         <code>options</options> specified or {@link S3Bucket#NOT_FOUND} if not present.
     * 
     */
    Future<S3Bucket> listBucket(String name, ListBucketOptions options);

    /**
     * @see ListOwnedBuckets
     * @return list of all of the buckets owned by the authenticated sender of
     *         the request.
     */
    Future<List<S3Bucket.Metadata>> listOwnedBuckets();
}
