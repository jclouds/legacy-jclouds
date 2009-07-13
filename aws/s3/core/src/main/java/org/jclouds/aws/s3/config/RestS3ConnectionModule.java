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

import java.net.MalformedURLException;
import java.net.URI;

import javax.annotation.Resource;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.s3.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.s3.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures the S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestS3ConnectionModule extends AbstractModule {
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_ADDRESS)
   String address;
   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_PORT)
   int port;
   @Inject
   @Named(HttpConstants.PROPERTY_HTTP_SECURE)
   boolean isSecure;

   @Override
   protected void configure() {
      install(new JaxrsModule());
      bind(RequestAuthorizeSignature.class).in(Scopes.SINGLETON);
      bindErrorHandlers();
      bindRetryHandlers();
      requestInjection(this);
      logger.info("S3 Context = %s://%s:%s", (isSecure ? "https" : "http"), address, port);
   }

   @Provides
   @Singleton
   protected S3Connection provideS3Connection(URI uri, RestClientFactory factory) {
      return factory.create(uri, S3Connection.class);
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

   @Singleton
   @Provides
   protected URI provideAddress(@Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
            @Named(HttpConstants.PROPERTY_HTTP_PORT) int port,
            @Named(HttpConstants.PROPERTY_HTTP_SECURE) boolean isSecure)
            throws MalformedURLException {

      return URI.create(String.format("%1$s://%2$s:%3$s", isSecure ? "https" : "http", address,
               port));
   }

}