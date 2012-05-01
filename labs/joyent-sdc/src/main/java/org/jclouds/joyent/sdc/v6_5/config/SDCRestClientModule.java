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
package org.jclouds.joyent.sdc.v6_5.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.joyent.sdc.v6_5.SDCAsyncClient;
import org.jclouds.joyent.sdc.v6_5.SDCClient;
import org.jclouds.joyent.sdc.v6_5.features.DatacenterAsyncClient;
import org.jclouds.joyent.sdc.v6_5.features.DatacenterClient;
import org.jclouds.joyent.sdc.v6_5.handlers.SDCErrorHandler;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the SDC connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class SDCRestClientModule extends RestClientModule<SDCClient, SDCAsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
      .put(DatacenterClient.class, DatacenterAsyncClient.class)
      .build();

   public SDCRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(SDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(SDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(SDCErrorHandler.class);
   }
}
