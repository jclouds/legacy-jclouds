/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.BucketMetadata.LocationConstraint;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.options.GetOptions;

/**
 * Provides access to S3 via their REST API.
 * <p/>
 * All commands return a Future of the result from S3. Any exceptions incurred during processing
 * will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @author Adrian Cole
 * @author James Murty
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAPI.html" />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface S3Client {

   /**
    * Creates a default implementation of S3Object
    */
   public S3Object newS3Object();

   /**
    * Retrieves the S3Object associated with the Key or KeyNotFoundException if not available;
    * 
    * <p/>
    * To use GET, you must have READ access to the object. If READ access is granted to the
    * anonymous user, you can request the object without an authorization header.
    * 
    * <p />
    * This command allows you to specify {@link GetObjectOptions} to control delivery of content.
    * 
    * <h2>Note</h2 If you specify any of the below options, you will receive partial content:
    * <ul>
    * <li>{@link GetObjectOptions#range}</li>
    * <li>{@link GetObjectOptions#startAt}</li>
    * <li>{@link GetObjectOptions#tail}</li>
    * </ul>
    * 
    * @param bucketName
    *           namespace of the object you are retrieving
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @return Future reference to a fully populated S3Object including data stored in S3 or
    *         {@link S3Object#NOT_FOUND} if not present.
    * 
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set were not satisfied by the object on the server.
    * @see #getObject(String, String)
    * @see GetObjectOptions
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   S3Object getObject(String bucketName, String key, GetOptions... options);

   /**
    * Retrieves the {@link org.jclouds.aws.s3.domain.internal.BucketListObjectMetadata metadata} of
    * the object associated with the key or
    * {@link org.jclouds.aws.s3.domain.internal.BucketListObjectMetadata#NOT_FOUND} if not
    * available.
    * 
    * <p/>
    * The HEAD operation is used to retrieve information about a specific object or object size,
    * without actually fetching the object itself. This is useful if you're only interested in the
    * object metadata, and don't want to waste bandwidth on the object data.
    * 
    * 
    * @param bucketName
    *           namespace of the metadata you are retrieving
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @return metadata associated with the key or
    *         {@link org.jclouds.aws.s3.domain.internal.BucketListObjectMetadata#NOT_FOUND} if not
    *         present;
    * @see #getObject(String, String)
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectHEAD.html"
    *      />
    */
   ObjectMetadata headObject(String bucketName, String key);

   /**
    * Removes the object and metadata associated with the key.
    * <p/>
    * The DELETE request operation removes the specified object from Amazon S3. Once deleted, there
    * is no method to restore or undelete an object.
    * 
    * 
    * @param bucketName
    *           namespace of the object you are deleting
    * @param key
    *           unique key in the s3Bucket identifying the object
    * @return true if deleted
    * @throws org.jclouds.http.HttpResponseException
    *            if the bucket is not available
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
    *      RESTObjectDELETE.html" />
    */
   void deleteObject(String bucketName, String key);

   /**
    * Store data by creating or overwriting an object.
    * <p/>
    * This method will store the object with the default <code>private</code acl.
    * 
    * <p/>
    * This returns a byte[] of the eTag hash of what Amazon S3 received
    * <p />
    * 
    * @param bucketName
    *           namespace of the object you are storing
    * @param object
    *           contains the data and metadata to create or overwrite
    * @param options
    *           options for creating the object
    * @return MD5 hash of the content uploaded
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set are not satisfied by the object on the server.
    * @see org.jclouds.aws.s3.domain.CannedAccessPolicy#PRIVATE
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectPUT.html"
    *      />
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   String putObject(String bucketName, S3Object object, PutObjectOptions... options);

   /**
    * Create and name your own bucket in which to store your objects.
    * 
    * <p/>
    * you can use {@link PutBucketOptions} to create the bucket in EU.
    * <p/>
    * The PUT request operation with a bucket URI creates a new bucket. Depending on your latency
    * and legal requirements, you can specify a location constraint that will affect where your data
    * physically resides. You can currently specify a Europe (EU) location constraint via
    * {@link PutBucketOptions}.
    * 
    * @param options
    *           for creating your bucket
    * @return true, if the bucket was created or already exists
    * 
    * @see PutBucketOptions
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketPUT.html"
    *      />
    * 
    */
   boolean putBucketIfNotExists(String bucketName, PutBucketOptions... options);

   /**
    * Deletes the bucket, if it is empty.
    * <p/>
    * The DELETE request operation deletes the bucket named in the URI. All objects in the bucket
    * must be deleted before the bucket itself can be deleted.
    * <p />
    * Only the owner of a bucket can delete it, regardless of the bucket's access control policy.
    * 
    * 
    * @param bucketName
    *           what to delete
    * @return false, if the bucket was not empty and therefore not deleted
    * @see org.jclouds.aws.s3.commands.DeleteBucket
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketDELETE.html"
    *      />
    */
   boolean deleteBucketIfEmpty(String bucketName);

   /**
    * Issues a HEAD command to determine if the bucket exists or not.
    */
   boolean bucketExists(String bucketName);

   /**
    * Retrieve a <code>S3Bucket</code listing. A GET request operation using a bucket URI lists
    * information about the objects in the bucket. You can use {@link ListBucketOptions} to control
    * the amount of S3Objects to return.
    * <p />
    * To list the keys of a bucket, you must have READ access to the bucket.
    * <p/>
    * 
    * @param bucketName
    *           namespace of the objects you wish to list
    * @return Future reference to a fully populated S3Bucket including metadata of the S3Objects it
    *         contains or {@link BoundedList<ObjectMetadata>#NOT_FOUND} if not present.
    * @see ListBucketOptions
    * 
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html"
    *      />
    */
   ListBucketResponse listBucket(String bucketName, ListBucketOptions... options);

   /**
    * Returns a list of all of the buckets owned by the authenticated sender of the request.
    * 
    * @return list of all of the buckets owned by the authenticated sender of the request.
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTServiceGET.html"
    *      />
    * 
    */
   SortedSet<BucketMetadata> listOwnedBuckets();

   /**
    * Copies one object to another bucket, retaining UserMetadata from the source. The destination
    * will have a private acl. The copy operation creates a copy of an object that is already stored
    * in Amazon S3.
    * <p/>
    * When copying an object, you can preserve all metadata (default) or
    * {@link CopyObjectOptions#overrideMetadataWith(com.google.common.collect.Multimap) specify new
    * metadata}. However, the ACL is not preserved and is set to private for the user making the
    * request. To override the default ACL setting,
    * {@link CopyObjectOptions#overrideAcl(org.jclouds.aws.s3.domain.CannedAccessPolicy) specify a
    * new ACL} when generating a copy request.
    * 
    * @return metadata populated with lastModified and eTag of the new object
    * @see org.jclouds.aws.s3.commands.CopyObject
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectCOPY.html"
    *      />
    * @throws org.jclouds.http.HttpResponseException
    *            if the conditions requested set are not satisfied by the object on the server.
    * @see CopyObjectOptions
    * @see org.jclouds.aws.s3.domain.CannedAccessPolicy
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   ObjectMetadata copyObject(String sourceBucket, String sourceObject, String destinationBucket,
            String destinationObject, CopyObjectOptions... options);

   /**
    * 
    * A GET request operation directed at an object or bucket URI with the "acl" parameter retrieves
    * the Access Control List (ACL) settings for that S3 item.
    * <p />
    * To list a bucket's ACL, you must have READ_ACP access to the item.
    * 
    * @return access permissions of the bucket
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   AccessControlList getBucketACL(String bucketName);

   /**
    * Update a bucket's Access Control List settings.
    * <p/>
    * A PUT request operation directed at a bucket URI with the "acl" parameter sets the Access
    * Control List (ACL) settings for that S3 item.
    * <p />
    * To set a bucket or object's ACL, you must have WRITE_ACP or FULL_CONTROL access to the item.
    * 
    * @param bucketName
    *           the bucket whose Access Control List settings will be updated.
    * @param acl
    *           the ACL to apply to the bucket. This acl object <strong>must</strong include a valid
    *           owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the bucket's Access Control List was updated successfully.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   boolean putBucketACL(String bucketName, AccessControlList acl);

   /**
    * A GET request operation directed at an object or bucket URI with the "acl" parameter retrieves
    * the Access Control List (ACL) settings for that S3 item.
    * <p />
    * To list a object's ACL, you must have READ_ACP access to the item.
    * 
    * @return access permissions of the object
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   AccessControlList getObjectACL(String bucketName, String key);

   /**
    * Update an object's Access Control List settings.
    * <p/>
    * A PUT request operation directed at an object URI with the "acl" parameter sets the Access
    * Control List (ACL) settings for that S3 item.
    * <p />
    * To set a bucket or object's ACL, you must have WRITE_ACP or FULL_CONTROL access to the item.
    * 
    * @param bucket
    *           the bucket containing the object to be updated
    * @param objectKey
    *           the key of the object whose Access Control List settings will be updated.
    * @param acl
    *           the ACL to apply to the object. This acl object <strong>must</strong include a valid
    *           owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the object's Access Control List was updated successfully.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   boolean putObjectACL(String bucketName, String key, AccessControlList acl);

   /**
    * A GET location request operation using a bucket URI lists the location constraint of the
    * bucket.
    * <p/>
    * To view the location constraint of a bucket, you must be the bucket owner.
    * 
    * @param bucket
    *           the bucket you wish to know where exists
    * 
    * @return location of the bucket
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonS3/latest/index.html?RESTBucketLocationGET.html"
    *      />
    */
   LocationConstraint getBucketLocation(String bucketName);

}
