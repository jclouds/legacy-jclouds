/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.googlestorage;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.googlestorage.filters.AddGoogleProjectId;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.*;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.predicates.validators.BucketNameValidator;
import org.jclouds.s3.xml.CopyObjectHandler;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

/**
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters({OAuthAuthenticator.class, AddGoogleProjectId.class})
@BlobScope(CONTAINER)
public interface GoogleStorageAsyncClient extends S3AsyncClient {
   /**
    * @see S3Client#copyObject
    */
   @Override
   @PUT
   @Path("/{destinationObject}")
   @Headers(keys = "x-goog-copy-source", values = "/{sourceBucket}/{sourceObject}")
   @XMLResponseParser(CopyObjectHandler.class)
   ListenableFuture<ObjectMetadata> copyObject(
           @PathParam("sourceBucket") String sourceBucket,
           @PathParam("sourceObject") String sourceObject,
           @Bucket @BinderParam(BindAsHostPrefixIfConfigured.class) @ParamValidators({BucketNameValidator.class})
           String destinationBucket,
           @PathParam("destinationObject") String destinationObject, CopyObjectOptions... options);

}
