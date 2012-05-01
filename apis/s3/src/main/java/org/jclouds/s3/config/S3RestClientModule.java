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
package org.jclouds.s3.config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.config.AWSRestClientModule;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Region;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.jclouds.s3.handlers.ParseS3ErrorFromXmlContent;
import org.jclouds.s3.handlers.S3RedirectionRetryHandler;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class S3RestClientModule<S extends S3Client, A extends S3AsyncClient> extends AWSRestClientModule<S, A> {

   @SuppressWarnings("unchecked")
   public S3RestClientModule() {
      this((TypeToken) TypeToken.of(S3Client.class), (TypeToken) TypeToken.of(S3AsyncClient.class));
   }

   protected S3RestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Provides
   @Bucket
   @Singleton
   protected Map<String, String> bucketToRegion() {
      return Maps.newConcurrentMap();
   }

   @Provides
   @Bucket
   @Singleton
   protected Supplier<String> defaultRegionForBucket(@Region Supplier<String> defaultRegion) {
      return defaultRegion;
   }

   @Override
   protected void configure() {
      super.configure();
      install(new S3ObjectModule());
      install(new S3ParserModule());
      bind(RequestAuthorizeSignature.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseS3ErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseS3ErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseS3ErrorFromXmlContent.class);
   }

   @Provides
   @Singleton
   protected RequestSigner provideRequestSigner(RequestAuthorizeSignature in) {
      return in;
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(S3RedirectionRetryHandler.class);
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
      return cache.get();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   @Singleton
   protected Supplier<String> provideTimeStampCache(@Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
            final DateService dateService) {
      return Suppliers.memoizeWithExpiration(new Supplier<String>() {
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }
}
