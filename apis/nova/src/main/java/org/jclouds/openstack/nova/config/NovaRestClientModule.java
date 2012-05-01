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
package org.jclouds.openstack.nova.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.functions.URIFromAuthenticationResponseForService;
import org.jclouds.openstack.nova.NovaAsyncClient;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.ServerManagement;
import org.jclouds.openstack.nova.handlers.ParseNovaErrorFromHttpResponse;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class NovaRestClientModule extends RestClientModule<NovaClient, NovaAsyncClient> {

   private final OpenStackAuthenticationModule module;

   public NovaRestClientModule(OpenStackAuthenticationModule module) {
      this.module = module;
   }

   public NovaRestClientModule() {
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
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseNovaErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseNovaErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseNovaErrorFromHttpResponse.class);
   }

   @Provides
   @Singleton
   @ServerManagement
   protected Supplier<URI> provideServerUrl(URIFromAuthenticationResponseForService.Factory factory) {
      return factory.create(AuthHeaders.SERVER_MANAGEMENT_URL);
   }
}
