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

import org.jclouds.aws.s3.binders.S3ObjectBinder;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.functions.ClearAndDeleteBucketIfNotEmpty;
import org.jclouds.aws.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.aws.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.aws.s3.functions.ReturnTrueIfBucketAlreadyOwnedByYou;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobKey;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.Endpoint;
import org.jclouds.rest.EntityParam;
import org.jclouds.rest.ExceptionParser;
import org.jclouds.rest.HostPrefixParam;
import org.jclouds.rest.ParamParser;
import org.jclouds.rest.QueryParams;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.ResponseParser;
import org.jclouds.rest.SkipEncoding;
import org.jclouds.rest.VirtualHost;
import org.jclouds.rest.XMLResponseParser;

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
public interface S3BlobStore extends BlobStore<BucketMetadata, ObjectMetadata, S3Object> {

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
    * @see #getBlob(String, String)
    * @see GetObjectOptions
    */
   @GET
   @Path("{key}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   Future<S3Object> getBlob(@HostPrefixParam String bucketName, @PathParam("key") String key);

   @GET
   @Path("{key}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   Future<S3Object> getBlob(@HostPrefixParam String bucketName, @PathParam("key") String key,
            GetOptions options);

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
    * @see #getBlob(String, String)
    * @see <a
    *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectHEAD.html"
    *      />
    */
   @HEAD
   @Path("{key}")
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   ObjectMetadata blobMetadata(@HostPrefixParam String bucketName, @PathParam("key") String key);

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
   Future<Void> removeBlob(@HostPrefixParam String bucketName, @PathParam("key") String key);

   @PUT
   @Path("{key}")
   @ResponseParser(ParseETagHeader.class)
   Future<byte[]> putBlob(
            @HostPrefixParam String bucketName,
            @PathParam("key") @ParamParser(BlobKey.class) @EntityParam(S3ObjectBinder.class) S3Object object);

   @PUT
   @Path("/")
   @ExceptionParser(ReturnTrueIfBucketAlreadyOwnedByYou.class)
   Future<Boolean> createContainer(@HostPrefixParam String bucketName);

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
    * @see org.jclouds.aws.s3.commands.DeleteBucket
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketDELETE.html"
    *      />
    */
   @DELETE
   @Path("/")
   @ExceptionParser(ClearAndDeleteBucketIfNotEmpty.class)
   Future<Void> deleteContainer(@HostPrefixParam String bucketName);

   /**
    * Issues a HEAD command to determine if the bucket exists or not.
    */
   @HEAD
   @Path("/")
   @QueryParams(keys = "max-keys", values = "0")
   @ExceptionParser(ReturnFalseOn404.class)
   boolean containerExists(@HostPrefixParam String bucketName);

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
   Future<ListBucketResponse> listBlobs(@HostPrefixParam String bucketName);

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
   SortedSet<BucketMetadata> listContainers();

}
