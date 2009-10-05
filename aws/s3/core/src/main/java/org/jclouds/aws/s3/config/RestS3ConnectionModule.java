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
package org.jclouds.aws.s3.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3BlobStore;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.s3.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.s3.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestS3ConnectionModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(RequestAuthorizeSignature.class).in(Scopes.SINGLETON);
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   @S3
   protected URI provideS3URI(@Named(S3Constants.PROPERTY_S3_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected BlobStore<BucketMetadata, ObjectMetadata, S3Object> provideS3BlobStore(RestClientFactory factory) {
      return factory.create(S3BlobStore.class);
   }

   @Provides
   @Singleton
   protected S3Connection provideS3Connection(RestClientFactory factory) {
      return factory.create(S3Connection.class);
   }

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseAWSErrorFromXmlContent.class);
   }

   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
               AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
               AWSClientErrorRetryHandler.class);
   }
}