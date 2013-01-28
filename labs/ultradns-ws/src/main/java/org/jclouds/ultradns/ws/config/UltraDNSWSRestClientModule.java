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
package org.jclouds.ultradns.ws.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.UltraDNSWSAsyncApi;
import org.jclouds.ultradns.ws.features.TaskApi;
import org.jclouds.ultradns.ws.features.TaskAsyncApi;
import org.jclouds.ultradns.ws.features.ZoneApi;
import org.jclouds.ultradns.ws.features.ZoneAsyncApi;
import org.jclouds.ultradns.ws.handlers.UltraDNSWSErrorHandler;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the UltraDNSWS connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class UltraDNSWSRestClientModule extends RestClientModule<UltraDNSWSApi, UltraDNSWSAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(ZoneApi.class, ZoneAsyncApi.class)
         .put(TaskApi.class, TaskAsyncApi.class).build();

   public UltraDNSWSRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(UltraDNSWSErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(UltraDNSWSErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(UltraDNSWSErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ServerError.class).toInstance(HttpRetryHandler.NEVER_RETRY);
   }
}
