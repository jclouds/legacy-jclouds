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
package org.jclouds.vcloud.terremark.config;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.config.BaseVCloudRestClientModule;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.handlers.ParseTerremarkVCloudErrorFromHttpResponse;

import com.google.inject.Provides;

public abstract class TerremarkRestClientModule<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient>
         extends BaseVCloudRestClientModule<S, A> {

   public TerremarkRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Singleton
   @Provides
   @Named("CreateInternetService")
   String provideCreateInternetService() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService.xml"));
   }

   @Singleton
   @Provides
   @Named("CreateNodeService")
   String provideCreateNodeService() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService.xml"));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
   }

}