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
package org.jclouds.aws.s3;

import org.jclouds.aws.s3.commands.options.*;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Provides access to S3 via their REST API.
 * <p/>
 * All commands return a Future of the result from S3. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as
 * documented in {@link Future#get()}.
 *
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAPI.html" />
 */
public interface S3Connection {

    /**
     * Retrieve a complete <code>S3Object</code>.
     *
     * @param bucketName namespace of the object you are retrieving
     * @param key        unique key in the s3Bucket identifying the object
     * @return Future reference to a fully populated S3Object including data
     *         stored in S3 or {@link S3Object#NOT_FOUND} if not present.
     * @see org.jclouds.aws.s3.commands.GetObject
     */
    Future<S3Object> getObject(String bucketName, String key);

    /**
     * Like {@link #getObject(String, String)} except you can use
     * {@link GetObjectOptions} to control delivery.
     *
     * @return S3Object containing data relevant to the
     *         <code>options</options> specified or {@link S3Object#NOT_FOUND} if not present.
     * @throws org.jclouds.http.HttpResponseException
     *          if the conditions requested set were not satisfied by the
     *          object on the server.
     * @see #getObject(String, String)
     * @see GetObjectOptions
     */
    Future<S3Object> getObject(String bucketName, String key,
                               GetObjectOptions options);

    /**
     * Retrieves the {@link org.jclouds.aws.s3.domain.S3Object.Metadata metadata} of the object associated
     * with the key.
     *
     * @param bucketName namespace of the metadata you are retrieving
     * @param key        unique key in the s3Bucket identifying the object
     * @return metadata associated with the key or
     *         {@link org.jclouds.aws.s3.domain.S3Object.Metadata#NOT_FOUND} if not present;
     * @see org.jclouds.aws.s3.commands.HeadObject
     */
    Future<S3Object.Metadata> headObject(String bucketName, String key);

    /**
     * Removes the object and metadata associated with the key.
     *
     * @param bucketName namespace of the object you are deleting
     * @param key        unique key in the s3Bucket identifying the object
     * @return true if deleted
     * @throws org.jclouds.http.HttpResponseException
     *          if the bucket is not available
     * @see org.jclouds.aws.s3.commands.DeleteObject
     */
    Future<Boolean> deleteObject(String bucketName, String key);

    /**
     * Store data by creating or overwriting an object.
     * <p/>
     * This method will store the object with the default <code>private</code>
     * acl.
     *
     * @param bucketName namespace of the object you are storing
     * @param object     contains the data and metadata to create or overwrite
     * @return MD5 hash of the content uploaded
     * @see org.jclouds.aws.s3.domain.acl.CannedAccessPolicy#PRIVATE
     * @see org.jclouds.aws.s3.commands.PutObject
     */
    Future<byte[]> putObject(String bucketName, S3Object object);

    /**
     * Like {@link #putObject(String, S3Object)} except you can use
     * {@link CopyObjectOptions} to specify an alternate
     * {@link org.jclouds.aws.s3.domain.acl.CannedAccessPolicy acl}, override
     * {@link org.jclouds.aws.s3.domain.S3Object.Metadata#getUserMetadata() userMetadata}, or specify
     * conditions for copying the object.
     *
     * @param options options for creating the object
     * @throws org.jclouds.http.HttpResponseException
     *          if the conditions requested set are not satisfied by the
     *          object on the server.
     * @see S3Connection#putObject(String, S3Object)
     * @see PutObjectOptions
     */
    Future<byte[]> putObject(String bucketName, S3Object object,
                             PutObjectOptions options);

    /**
     * Create and name your own bucket in which to store your objects.
     *
     * @return true, if the bucket was created or already exists
     * @see org.jclouds.aws.s3.commands.PutBucket
     */
    Future<Boolean> putBucketIfNotExists(String name);

    /**
     * Like {@link #putBucketIfNotExists(String)} except that you can use
     * {@link PutBucketOptions} to create the bucket in EU. Create and name your
     *
     * @param options for creating your bucket
     * @see PutBucketOptions
     */
    Future<Boolean> putBucketIfNotExists(String name, PutBucketOptions options);

    /**
     * Deletes the bucket, if it is empty.
     *
     * @param s3Bucket what to delete
     * @return false, if the bucket was not empty and therefore not deleted
     * @see org.jclouds.aws.s3.commands.DeleteBucket
     */
    Future<Boolean> deleteBucketIfEmpty(String s3Bucket);

    /**
     * Copies one object to another bucket, retaining UserMetadata from the
     * source. The destination will have a private acl.
     *
     * @return metadata populated with lastModified and md5 of the new object
     * @see org.jclouds.aws.s3.commands.CopyObject
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
                                         String sourceObject, String destinationBucket,
                                         String destinationObject);

    /**
     * Like {@link #putObject(String, S3Object)} except you can use
     * {@link PutObjectOptions} to specify an alternate
     * {@link org.jclouds.aws.s3.domain.acl.CannedAccessPolicy acl}.
     *
     * @param options options for creating the object
     * @throws org.jclouds.http.HttpResponseException
     *          if the conditions requested set are not satisfied by the
     *          object on the server.
     * @see S3Connection#putObject(String, S3Object)
     * @see PutObjectOptions
     */
    Future<S3Object.Metadata> copyObject(String sourceBucket,
                                         String sourceObject, String destinationBucket,
                                         String destinationObject, CopyObjectOptions options);

    /**
     * @see org.jclouds.aws.s3.commands.BucketExists
     */
    Future<Boolean> bucketExists(String name);

    /**
     * Retrieve a complete <code>S3Bucket</code> listing.
     *
     * @param bucketName namespace of the objects you wish to list
     * @return Future reference to a fully populated S3Bucket including metadata
     *         of the S3Objects it contains or {@link S3Bucket#NOT_FOUND} if not
     *         present.
     * @see org.jclouds.aws.s3.commands.ListBucket
     */
    Future<S3Bucket> listBucket(String bucketName);

    /**
     * Like {@link #listBucket(String)} except you can use
     * {@link ListBucketOptions} to control the amount of S3Objects to return.
     *
     * @return S3Bucket containing a subset of {@link org.jclouds.aws.s3.domain.S3Object.Metadata}
     *         depending on
     *         <code>options</options> specified or {@link S3Bucket#NOT_FOUND} if not present.
     * @see #listBucket(String)
     * @see ListBucketOptions
     */
    Future<S3Bucket> listBucket(String name, ListBucketOptions options);

    /**
     * @return list of all of the buckets owned by the authenticated sender of
     *         the request.
     * @see org.jclouds.aws.s3.commands.ListOwnedBuckets
     */
    Future<List<S3Bucket.Metadata>> listOwnedBuckets();

    /**
     * @return access permissions of the bucket
     * 
     * @see org.jclouds.aws.s3.commands.GetAccessControlList
     */
    Future<AccessControlList> getBucketACL(String bucket);

    /**
     * Update a bucket's Access Control List settings.
     * @param bucket
     * the bucket whose Access Control List settings will be updated.
     * @param acl
     * the ACL to apply to the bucket. This acl object <strong>must</strong> 
     * include a valid owner identifier string in {@link AccessControlList#getOwner()}.
     * @return
     * true if the bucket's Access Control List was updated successfully.
     * 
     * @see org.jclouds.aws.s3.commands.PutBucketAccessControlList
     */
    Future<Boolean> putBucketACL(String bucket, AccessControlList acl);

    /**
     * @return access permissions of the object
     * 
     * @see org.jclouds.aws.s3.commands.GetAccessControlList
     */
    Future<AccessControlList> getObjectACL(String bucket, String objectKey);

    /**
     * Update an object's Access Control List settings.
     * @param bucket
     * the bucket containing the object to be updated
     * @param objectKey
     * the key of the object whose Access Control List settings will be updated.
     * @param acl
     * the ACL to apply to the object. This acl object <strong>must</strong> 
     * include a valid owner identifier string in {@link AccessControlList#getOwner()}.
     * @return
     * true if the object's Access Control List was updated successfully.
     * 
     * @see org.jclouds.aws.s3.commands.PutBucketAccessControlList
     */
    Future<Boolean> putObjectACL(String bucket, String objectKey, AccessControlList acl);

}
