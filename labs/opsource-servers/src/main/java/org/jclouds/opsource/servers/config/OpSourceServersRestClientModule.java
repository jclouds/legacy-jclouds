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
package org.jclouds.opsource.servers.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.opsource.servers.OpSourceServersAsyncApi;
import org.jclouds.opsource.servers.OpSourceServersApi;
import org.jclouds.opsource.servers.features.AccountAsyncApi;
import org.jclouds.opsource.servers.features.AccountApi;
import org.jclouds.opsource.servers.features.ServerAsyncApi;
import org.jclouds.opsource.servers.features.ServerApi;
import org.jclouds.opsource.servers.features.ServerImageAsyncApi;
import org.jclouds.opsource.servers.features.ServerImageApi;
import org.jclouds.opsource.servers.handlers.OpSourceServersErrorHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;

/**
 * Configures the OpSourceServers connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class OpSourceServersRestClientModule extends
      RestClientModule<OpSourceServersApi, OpSourceServersAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(AccountApi.class, AccountAsyncApi.class)
         .put(ServerImageApi.class, ServerImageAsyncApi.class)
         .put(ServerApi.class, ServerAsyncApi.class)
         .build();

   public OpSourceServersRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(OpSourceServersErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(OpSourceServersErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(OpSourceServersErrorHandler.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
   }
}
