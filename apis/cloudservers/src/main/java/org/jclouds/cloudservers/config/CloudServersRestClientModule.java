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
package org.jclouds.cloudservers.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.cloudservers.CloudServersAsyncClient;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.ServerManagement;
import org.jclouds.cloudservers.handlers.ParseCloudServersErrorFromHttpResponse;
import org.jclouds.cloudservers.handlers.RetryOnRenew;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class CloudServersRestClientModule extends RestClientModule<CloudServersClient, CloudServersAsyncClient> {

   private final OpenStackAuthenticationModule module;

   public CloudServersRestClientModule(OpenStackAuthenticationModule module) {
      super(CloudServersClient.class, CloudServersAsyncClient.class);
      this.module = module;
   }

   public CloudServersRestClientModule() {
      this(new OpenStackAuthenticationModule());
   }

   @Override
   protected void configure() {
      install(module);
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseCloudServersErrorFromHttpResponse.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
   }

   @Provides
   @Singleton
   @ServerManagement
   protected URI provideServerUrl(AuthenticationResponse response) {
      return response.getServices().get(AuthHeaders.SERVER_MANAGEMENT_URL);
   }

}
