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
package org.jclouds.aws.s3.suncloud.config;

import org.jclouds.aws.s3.config.RestS3ConnectionModule;
import org.jclouds.aws.s3.suncloud.handlers.ParseSunCloudS3ErrorFromXmlContent;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.ServerError;

import com.google.inject.Scopes;

/**
 * Configures the Sun Cloud S3 connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
public class SunCloudS3ConnectionModule extends RestS3ConnectionModule {

   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseSunCloudS3ErrorFromXmlContent.class).in(Scopes.SINGLETON);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseSunCloudS3ErrorFromXmlContent.class).in(Scopes.SINGLETON);
   }

}