/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.walrus;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.blobstore.functions.ReturnFalseOnContainerNotFound;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.predicates.validators.BucketNameValidator;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(RequestAuthorizeSignature.class)
@BlobScope(CONTAINER)
public interface WalrusAsyncClient extends S3AsyncClient {

   /**
    * @see S3Client#bucketExists
    */
   @Override
   @GET
   @Path("/")
   @QueryParams(keys = "max-keys", values = "0")
   @ExceptionParser(ReturnFalseOnContainerNotFound.class)
   ListenableFuture<Boolean> bucketExists(
            @Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators( { BucketNameValidator.class }) String bucketName);

}
