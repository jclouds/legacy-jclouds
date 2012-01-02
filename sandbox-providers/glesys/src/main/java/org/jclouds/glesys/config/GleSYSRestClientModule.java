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
package org.jclouds.glesys.config;

import java.util.Map;

import org.jclouds.glesys.GleSYSAsyncClient;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.features.*;
import org.jclouds.glesys.handlers.GleSYSErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the GleSYS connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class GleSYSRestClientModule extends RestClientModule<GleSYSClient, GleSYSAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(ServerClient.class, ServerAsyncClient.class)//
         .put(IpClient.class, IpAsyncClient.class)//
         .put(ArchiveClient.class, ArchiveAsyncClient.class)//
         .put(DomainClient.class, DomainAsyncClient.class)//
         .put(EmailClient.class, EmailAsyncClient.class)//
         .build();

   public GleSYSRestClientModule() {
      super(GleSYSClient.class, GleSYSAsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      install(new GleSYSParserModule());
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GleSYSErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GleSYSErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GleSYSErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(BackoffLimitedRetryHandler.class);
   }

}
