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
package org.jclouds.openstack.nova.v1_1.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.nova.v1_1.NovaAsyncClient;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.FloatingIPClient;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupAsyncClient;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v1_1.features.FlavorAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.FlavorClient;
import org.jclouds.openstack.nova.v1_1.features.ImageAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.ImageClient;
import org.jclouds.openstack.nova.v1_1.features.ServerAsyncClient;
import org.jclouds.openstack.nova.v1_1.features.ServerClient;
import org.jclouds.openstack.nova.v1_1.handlers.NovaErrorHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the Nova connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class NovaRestClientModule extends RestClientModule<NovaClient, NovaAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(ServerClient.class, ServerAsyncClient.class)//
            .put(FlavorClient.class, FlavorAsyncClient.class)
            .put(ImageClient.class, ImageAsyncClient.class)
            .put(FloatingIPClient.class, FloatingIPAsyncClient.class)
            .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)
            .put(KeyPairClient.class, KeyPairAsyncClient.class)
            .build();

   public NovaRestClientModule() {
      super(NovaClient.class, NovaAsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      install(new NovaParserModule());
      super.configure();
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      // TODO: select this from KeystoneProperties.VERSION; note you select from a guice provided
      // property, so it will have to come from somewhere else, maybe we move this to the the
      // ContextBuilder
      install(new KeystoneAuthenticationModule());
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(NovaErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(NovaErrorHandler.class);
   }
}
