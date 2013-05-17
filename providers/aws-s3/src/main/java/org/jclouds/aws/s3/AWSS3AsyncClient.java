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
package org.jclouds.aws.s3;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.s3.binders.BindIterableAsPayloadToDeleteRequest;
import org.jclouds.aws.s3.binders.BindObjectMetadataToRequest;
import org.jclouds.aws.s3.binders.BindPartIdsAndETagsToRequest;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.aws.s3.functions.ETagFromHttpResponseViaRegex;
import org.jclouds.aws.s3.functions.ObjectMetadataKey;
import org.jclouds.aws.s3.functions.UploadIdFromHttpResponseViaRegex;
import org.jclouds.aws.s3.xml.DeleteResultHandler;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.functions.AssignCorrectHostnameForBucket;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.predicates.validators.BucketNameValidator;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to amazon-specific S3 features
 * 
 * @author Adrian Cole, Jeremy Whitlock
 * @deprecated please use
 *             {@code org.jclouds.ContextBuilder#buildApi(AWSS3Client.class)}
 *             as {@link AWSS3AsyncClient} interface will be removed in jclouds 1.7.
 */
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface AWSS3AsyncClient extends S3AsyncClient {
   
   /**
    * @see AWSS3Client#initiateMultipartUpload
    */
   @Named("PutObject")
   @POST
   @QueryParams(keys = "uploads")
   @Path("/{key}")
   @ResponseParser(UploadIdFromHttpResponseViaRegex.class)
   ListenableFuture<String> initiateMultipartUpload(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") @ParamParser(ObjectMetadataKey.class) @BinderParam(BindObjectMetadataToRequest.class) ObjectMetadata objectMetadata,
            PutObjectOptions... options);

   /**
    * @see AWSS3Client#abortMultipartUpload
    */
   @Named("AbortMultipartUpload")
   @DELETE
   @Path("/{key}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> abortMultipartUpload(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key, @QueryParam("uploadId") String uploadId);

   /**
    * @see AWSS3Client#uploadPart
    */
   @Named("PutObject")
   @PUT
   @Path("/{key}")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> uploadPart(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key, @QueryParam("partNumber") int partNumber,
            @QueryParam("uploadId") String uploadId, Payload part);

   /**
    * @see AWSS3Client#completeMultipartUpload
    */
   @Named("PutObject")
   @POST
   @Path("/{key}")
   @ResponseParser(ETagFromHttpResponseViaRegex.class)
   ListenableFuture<String> completeMultipartUpload(
            @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
            @PathParam("key") String key, @QueryParam("uploadId") String uploadId,
            @BinderParam(BindPartIdsAndETagsToRequest.class) Map<Integer, String> parts);

   /**
    * @see AWSS3Client#deleteObjects
    */
   @Named("DeleteObject")
   @POST                              
   @Path("/")
   @QueryParams(keys = "delete")
   @XMLResponseParser(DeleteResultHandler.class)
   ListenableFuture<DeleteResult> deleteObjects(
      @Bucket @EndpointParam(parser = AssignCorrectHostnameForBucket.class) @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators(BucketNameValidator.class) String bucketName,
      @BinderParam(BindIterableAsPayloadToDeleteRequest.class) Iterable<String> keys);

}
