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
package org.jclouds.openstack.glance.v1_0.config;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.glance.v1_0.GlanceAsyncClient;
import org.jclouds.openstack.glance.v1_0.GlanceClient;
import org.jclouds.openstack.glance.v1_0.handlers.GlanceErrorHandler;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

/**
 * Configures the Glance connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class GlanceRestClientModule extends RestClientModule<GlanceClient, GlanceAsyncClient> {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void installLocations() {
      // TODO: select this from KeystoneProperties.VERSION; note you select from
      // a guice provided
      // property, so it will have to come from somewhere else, maybe we move
      // this to the the
      // ContextBuilder
      install(KeystoneAuthenticationModule.forRegions());
      super.installLocations();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(GlanceErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(GlanceErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(GlanceErrorHandler.class);
   }
}
