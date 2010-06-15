/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.s3.config;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

/**
 * Configures the S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class S3RestClientModule extends
      RestClientModule<S3Client, S3AsyncClient> {
   public S3RestClientModule() {
      super(S3Client.class, S3AsyncClient.class);
   }

   @Override
   protected void configure() {
      install(new S3ObjectModule());
      bind(RequestAuthorizeSignature.class).in(Scopes.SINGLETON);
      super.configure();
   }

   @Provides
   @TimeStamp
   protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
      return cache.get();
   }

   @Provides
   @Singleton
   RequestSigner provideRequestSigner(RequestAuthorizeSignature in) {
      return in;
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @TimeStamp
   Supplier<String> provideTimeStampCache(
         @Named(S3Constants.PROPERTY_S3_SESSIONINTERVAL) long seconds,
         final DateService dateService) {
      return new ExpirableSupplier<String>(new Supplier<String>() {
         public String get() {
            return dateService.rfc822DateFormat();
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @S3
   Map<String, URI> provideRegions(
         @Named(S3Constants.PROPERTY_S3_REGIONS) String regionString,
         Injector injector) {
      Map<String, URI> regions = Maps.newLinkedHashMap();
      for (String region : Splitter.on(',').split(regionString)) {
         regions.put(region, URI.create(injector.getInstance(Key.get(
               String.class, Names.named(S3Constants.PROPERTY_S3_ENDPOINT + "."
                     + region)))));
      }
      return regions;
   }

   @Provides
   @Singleton
   @S3
   Set<String> provideRegions(@S3 Map<String, URI> map) {
      return map.keySet();
   }

   @Provides
   @Singleton
   @S3
   protected URI provideS3URI(
         @Named(S3Constants.PROPERTY_S3_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @S3
   String getDefaultRegion(@S3 URI uri, @S3 Map<String, URI> map) {
      return ImmutableBiMap.<String, URI> builder().putAll(map).build()
            .inverse().get(uri);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
            ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
            ParseAWSErrorFromXmlContent.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
            ParseAWSErrorFromXmlContent.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
            AWSRedirectionRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
            AWSClientErrorRetryHandler.class);
   }
}