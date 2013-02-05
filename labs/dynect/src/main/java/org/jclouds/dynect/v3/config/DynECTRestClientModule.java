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
package org.jclouds.dynect.v3.config;

import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.DynECTAsyncApi;
import org.jclouds.dynect.v3.features.SessionApi;
import org.jclouds.dynect.v3.features.SessionAsyncApi;
import org.jclouds.dynect.v3.features.ZoneApi;
import org.jclouds.dynect.v3.features.ZoneAsyncApi;
import org.jclouds.dynect.v3.filters.SessionManager;
import org.jclouds.dynect.v3.handlers.GetJobRedirectionRetryHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the DynECT connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class DynECTRestClientModule extends RestClientModule<DynECTApi, DynECTAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(SessionApi.class, SessionAsyncApi.class)
         .put(ZoneApi.class, ZoneAsyncApi.class).build();

   public DynECTRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(SessionManager.class);
   }

   @Override
   protected void configure() {
      // binding explicitly ensures singleton despite multiple linked bindings
      bind(SessionManager.class);
      bind(RedirectionRetryHandler.class).to(GetJobRedirectionRetryHandler.class);
      super.configure();
      // Bind apis that are used directly vs via DynECTApi
      bindHttpApi(binder(), SessionApi.class, SessionAsyncApi.class);
   }

}
