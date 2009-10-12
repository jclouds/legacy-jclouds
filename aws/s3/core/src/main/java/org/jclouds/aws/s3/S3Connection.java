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

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.aws.s3.binders.BindACLToXMLEntity;
import org.jclouds.aws.s3.binders.BindS3ObjectToEntity;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.aws.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.aws.s3.functions.ReturnTrueIfBucketAlreadyOwnedByYou;
import org.jclouds.aws.s3.functions.ReturnTrueOn404FalseIfNotEmpty;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.aws.s3.xml.AccessControlListHandler;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobKey;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.HostPrefixParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

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
@VirtualHost
@SkipEncoding('/')
@RequestFilters(RequestAuthorizeSignature.class)
@Endpoint(S3.class)
public interface S3Connection {

   /**
    * Retrieves the S3Object associated with the Key or {@link Blob <ObjectMetadata>#NOT_FOUND} if
    * not available;
    * 
    * <p/>
    * To use GET, you must have READ access to the object. If READ access is granted to the
    * anonymous user, you can request the object without an authorization header.
    * 
    * <p />
    * This command allows you to specify {@link GetObjectOptions} to control delivery of content.
    * 
    * <h2>Note</h2> If you specify any of the below options, you will receive partial content:
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
   @GET
   @Path("{key}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   Future<S3Object> getObject(@HostPrefixParam String bucketName, @PathParam("key") String key,
            GetOptions... options);

   /**
    * Retrieves the {@link org.jclouds.aws.s3.domain.ObjectMetadata metadata} of the object
    * associated with the key or {@link org.jclouds.aws.s3.domain.ObjectMetadata#NOT_FOUND} if not
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
    *         {@link org.jclouds.aws.s3.domain.ObjectMetadata#NOT_FOUND} if not present;
    * @see #getObject(String, String)
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectHEAD.html"
    *      />
    */
   @HEAD
   @Path("{key}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   ObjectMetadata headObject(@HostPrefixParam String bucketName, @PathParam("key") String key);

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
   @DELETE
   @Path("{key}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   Future<Void> deleteObject(@HostPrefixParam String bucketName, @PathParam("key") String key);

   /**
    * Store data by creating or overwriting an object.
    * <p/>
    * This method will store the object with the default <code>private</code> acl.
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
   @PUT
   @Path("{key}")
   @ResponseParser(ParseETagHeader.class)
   Future<String> putObject(
            @HostPrefixParam String bucketName,
            @PathParam("key") @ParamParser(BlobKey.class) @BinderParam(BindS3ObjectToEntity.class) S3Object object,
            PutObjectOptions... options);

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
   @PUT
   @Path("/")
   @ExceptionParser(ReturnTrueIfBucketAlreadyOwnedByYou.class)
   Future<Boolean> putBucketIfNotExists(@HostPrefixParam String bucketName,
            PutBucketOptions... options);

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
   @DELETE
   @Path("/")
   @ExceptionParser(ReturnTrueOn404FalseIfNotEmpty.class)
   Future<Boolean> deleteBucketIfEmpty(@HostPrefixParam String bucketName);

   /**
    * Issues a HEAD command to determine if the bucket exists or not.
    */
   @HEAD
   @Path("/")
   @QueryParams(keys = "max-keys", values = "0")
   @ExceptionParser(ReturnFalseOn404.class)
   boolean bucketExists(@HostPrefixParam String bucketName);

   /**
    * Retrieve a <code>S3Bucket</code> listing. A GET request operation using a bucket URI lists
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
   @GET
   @Path("/")
   @XMLResponseParser(ListBucketHandler.class)
   Future<ListBucketResponse> listBucket(@HostPrefixParam String bucketName,
            ListBucketOptions... options);

   /**
    * Returns a list of all of the buckets owned by the authenticated sender of the request.
    * 
    * @return list of all of the buckets owned by the authenticated sender of the request.
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTServiceGET.html"
    *      />
    * 
    */
   @GET
   @XMLResponseParser(ListAllMyBucketsHandler.class)
   @Path("/")
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
   @PUT
   @Path("{destinationObject}")
   @Headers(keys = "x-amz-copy-source", values = "/{sourceBucket}/{sourceObject}")
   @XMLResponseParser(CopyObjectHandler.class)
   Future<ObjectMetadata> copyObject(@PathParam("sourceBucket") String sourceBucket,
            @PathParam("sourceObject") String sourceObject,
            @HostPrefixParam String destinationBucket,
            @PathParam("destinationObject") String destinationObject, CopyObjectOptions... options);

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
   @GET
   @QueryParams(keys = "acl")
   @XMLResponseParser(AccessControlListHandler.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Path("/")
   Future<AccessControlList> getBucketACL(@HostPrefixParam String bucketName);

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
    *           the ACL to apply to the bucket. This acl object <strong>must</strong> include a
    *           valid owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the bucket's Access Control List was updated successfully.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   @PUT
   @Path("/")
   @QueryParams(keys = "acl")
   Future<Boolean> putBucketACL(@HostPrefixParam String bucketName,
            @BinderParam(BindACLToXMLEntity.class) AccessControlList acl);

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
   @GET
   @QueryParams(keys = "acl")
   @Path("{key}")
   @XMLResponseParser(AccessControlListHandler.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   Future<AccessControlList> getObjectACL(@HostPrefixParam String bucketName,
            @PathParam("key") String key);

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
    *           the ACL to apply to the object. This acl object <strong>must</strong> include a
    *           valid owner identifier string in {@link AccessControlList#getOwner()}.
    * @return true if the object's Access Control List was updated successfully.
    * 
    * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAccessPolicy.html"/>
    */
   @PUT
   @QueryParams(keys = "acl")
   @Path("{key}")
   Future<Boolean> putObjectACL(@HostPrefixParam String bucketName, @PathParam("key") String key,
            @BinderParam(BindACLToXMLEntity.class) AccessControlList acl);

}
