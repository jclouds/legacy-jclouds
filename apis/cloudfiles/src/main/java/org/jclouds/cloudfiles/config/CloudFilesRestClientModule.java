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
package org.jclouds.cloudfiles.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.cloudfiles.CDNManagement;
import org.jclouds.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.handlers.RetryOnRenew;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.config.SwiftObjectModule;
import org.jclouds.openstack.swift.handlers.ParseSwiftErrorFromHttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.Iterables;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class CloudFilesRestClientModule extends RestClientModule<CloudFilesClient, CloudFilesAsyncClient> {
   private final AuthenticationServiceModule module;

   public CloudFilesRestClientModule(AuthenticationServiceModule module) {
      super(CloudFilesClient.class, CloudFilesAsyncClient.class);
      this.module = module;
   }
   
   public CloudFilesRestClientModule() {
      this(new AuthenticationServiceModule());
   }
   
   @Provides
   @Singleton
   CommonSwiftClient provideCommonSwiftClient(CloudFilesClient in) {
      return in;
   }

   @Provides
   @Singleton
   CommonSwiftAsyncClient provideCommonSwiftClient(CloudFilesAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   @CDNManagement
   protected URI provideCDNUrl(Auth response) {
      return Iterables.get(response.getServiceCatalog().get("cloudFilesCDN"), 0).getPublicURL();
   }

   @Provides
   @Singleton
   @Storage
   protected URI provideStorageUrl(Auth response) {
      return Iterables.get(response.getServiceCatalog().get("cloudFiles"), 0).getPublicURL();
   }
   
   @Override
   protected void configure() {
      install(module);
      install(new SwiftObjectModule());
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseSwiftErrorFromHttpResponse.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
   }


}
