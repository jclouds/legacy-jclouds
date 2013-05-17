/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.s3;

import static com.google.common.net.HttpHeaders.EXPECT;
import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import java.io.Closeable;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.s3.S3Fallbacks.TrueOn404OrNotFoundFalseOnIllegalState;
import org.jclouds.s3.binders.BindACLToXMLPayload;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.binders.BindBucketLoggingToXmlPayload;
import org.jclouds.s3.binders.BindNoBucketLoggingToXmlPayload;
import org.jclouds.s3.binders.BindPayerToXmlPayload;
import org.jclouds.s3.binders.BindS3ObjectMetadataToRequest;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.BucketMetadata;
import org.jclouds.s3.domain.ListBucketResponse;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.fallbacks.FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.functions.AssignCorrectHostnameForBucket;
import org.jclouds.s3.functions.BindRegionToXmlPayload;
import org.jclouds.s3.functions.DefaultEndpointThenInvalidateRegion;
import org.jclouds.s3.functions.ObjectKey;
import org.jclouds.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.predicates.validators.BucketNameValidator;
import org.jclouds.s3.xml.AccessControlListHandler;
import org.jclouds.s3.xml.BucketLoggingHandler;
import org.jclouds.s3.xml.CopyObjectHandler;
import org.jclouds.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.s3.xml.ListBucketHandler;
import org.jclouds.s3.xml.LocationConstraintHandler;
import org.jclouds.s3.xml.PayerHandler;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to S3 via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from S3. Any exceptions incurred during
 * processing will be backend in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 *
 * @author Adrian Cole
 * @author James Murty
 * @see AWSS3Client
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAPI.html" />
 * @deprecated please use
 *             {@code org.jclouds.ContextBuilder#buildApi(S3Client.class)}
 *             as {@link S3AsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface S3AsyncClient extends Closeable {
   public static final String VERSION = "2006-03-01";

   /**
    * Creates a default implementation of S3Object
    */
   @Provides
   public S3Object newS3Object();

   /**
    * @see S3Client#getObject
    */
   @Named("GetObject")
   @GET
   @Path("/{key}")
   @Fallback(NullOnKeyNotFound.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   ListenableFuture<S3Object> getObject(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key, GetOptions... options);

   /**
    * @see S3Client#headObject
    */
   @Named("GetObject")
   @HEAD
   @Path("/{key}")
   @Fallback(NullOnKeyNotFound.class)
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   ListenableFuture<ObjectMetadata> headObject(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#objectExists
    */
   @Named("GetObject")
   @HEAD
   @Path("/{key}")
   @Fallback(FalseOnKeyNotFound.class)
   ListenableFuture<Boolean> objectExists(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#deleteObject
    */
   @Named("DeleteObject")
   @DELETE
   @Path("/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteObject(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#putObject
    */
   @Named("PutObject")
   @PUT
   @Path("/{key}")
   @Headers(keys = EXPECT, values = "100-continue")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putObject(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") @ParamParser(ObjectKey.class) @BinderParam(BindS3ObjectMetadataToRequest.class) S3Object object,
            PutObjectOptions... options);

   /**
    * @see S3Client#putBucketInRegion
    */
   @Named("CreateBucket")
   @PUT
   @Path("/")
   @Endpoint(Bucket.class)
   @Fallback(FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists.class)
   ListenableFuture<Boolean> putBucketInRegion(
            @BinderParam(BindRegionToXmlPayload.class) @Nullable String region,
            @Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            PutBucketOptions... options);

   /**
    * @see S3Client#deleteBucketIfEmpty
    */
   @Named("DeleteBucket")
   @DELETE
   @Path("/")
   @Fallback(TrueOn404OrNotFoundFalseOnIllegalState.class)
   ListenableFuture<Boolean> deleteBucketIfEmpty(
            @Bucket @EndpointParam(parser = DefaultEndpointThenInvalidateRegion.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * @see S3Client#bucketExists
    */
   @Named("ListBucket")
   @GET
   @Path("/")
   @QueryParams(keys = "max-keys", values = "0")
   @Fallback(FalseOnContainerNotFound.class)
   ListenableFuture<Boolean> bucketExists(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);


   /**
    * @see S3Client#getBucketLocation
    */
   @Named("GetBucketLocation")
   @GET
   @QueryParams(keys = "location")
   @Path("/")
   @Endpoint(Bucket.class)
   @XMLResponseParser(LocationConstraintHandler.class)
   ListenableFuture<String> getBucketLocation(
            @Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * @see S3Client#getBucketPayer
    */
   @Named("GetBucketRequestPayment")
   @GET
   @QueryParams(keys = "requestPayment")
   @Path("/")
   @XMLResponseParser(PayerHandler.class)
   ListenableFuture<Payer> getBucketPayer(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * @see S3Client#setBucketPayer
    */
   @Named("PutBucketRequestPayment")
   @PUT
   @QueryParams(keys = "requestPayment")
   @Path("/")
   ListenableFuture<Void> setBucketPayer(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @BinderParam(BindPayerToXmlPayload.class) Payer payer);

   /**
    * @see S3Client#listBucket
    */
   @Named("ListBucket")
   @GET
   @Path("/")
   @XMLResponseParser(ListBucketHandler.class)
   ListenableFuture<ListBucketResponse> listBucket(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            ListBucketOptions... options);

   /**
    * @see S3Client#listOwnedBuckets
    */
   @Named("ListAllMyBuckets")
   @GET
   @XMLResponseParser(ListAllMyBucketsHandler.class)
   @Path("/")
   @VirtualHost
   ListenableFuture<? extends Set<BucketMetadata>> listOwnedBuckets();

   /**
    * @see S3Client#copyObject
    */
   @Named("PutObject")
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = "x-amz-copy-source", values = "/{sourceBucket}/{sourceObject}")
   @XMLResponseParser(CopyObjectHandler.class)
   ListenableFuture<ObjectMetadata> copyObject(
            @PathParam("sourceBucket") String sourceBucket,
            @PathParam("sourceObject") String sourceObject,
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String destinationBucket,
            @PathParam("destinationObject") String destinationObject, CopyObjectOptions... options);

   /**
    * @see S3Client#getBucketACL
    */
   @Named("GetBucketAcl")
   @GET
   @QueryParams(keys = "acl")
   @XMLResponseParser(AccessControlListHandler.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Path("/")
   ListenableFuture<AccessControlList> getBucketACL(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * @see S3Client#putBucketACL
    */
   @Named("PutBucketAcl")
   @PUT
   @Path("/")
   @QueryParams(keys = "acl")
   ListenableFuture<Boolean> putBucketACL(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);

   /**
    * @see S3Client#getObjectACL
    */
   @Named("GetObjectAcl")
   @GET
   @QueryParams(keys = "acl")
   @Path("/{key}")
   @XMLResponseParser(AccessControlListHandler.class)
   @Fallback(ThrowKeyNotFoundOn404.class)
   ListenableFuture<AccessControlList> getObjectACL(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#putObjectACL
    */
   @Named("PutObjectAcl")
   @PUT
   @QueryParams(keys = "acl")
   @Path("/{key}")
   ListenableFuture<Boolean> putObjectACL(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key, @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);

   /**
    * @see S3Client#getBucketLogging
    */
   @Named("GetBucketLogging")
   @GET
   @QueryParams(keys = "logging")
   @XMLResponseParser(BucketLoggingHandler.class)
   @Fallback(ThrowContainerNotFoundOn404.class)
   @Path("/")
   ListenableFuture<BucketLogging> getBucketLogging(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName);

   /**
    * @see S3Client#enableBucketLogging
    */
   @Named("PutBucketLogging")
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   ListenableFuture<Void> enableBucketLogging(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @BinderParam(BindBucketLoggingToXmlPayload.class) BucketLogging logging);

   /**
    * @see S3Client#putBucketLogging
    */
   @Named("PutBucketLogging")
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   @Produces(MediaType.TEXT_XML)
   ListenableFuture<Void> disableBucketLogging(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindNoBucketLoggingToXmlPayload.class) @ParamValidators(BucketNameValidator.class) String bucketName);

}
