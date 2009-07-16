/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.config;

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;

import java.net.MalformedURLException;
import java.net.URI;

import org.jclouds.http.HttpConstants;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.CDN;
import org.jclouds.rackspace.RackspaceAuthentication;
import org.jclouds.rackspace.Server;
import org.jclouds.rackspace.Storage;
import org.jclouds.rackspace.RackspaceAuthentication.AuthenticationResponse;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RackspaceAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new JaxrsModule());
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   protected AuthenticationResponse provideAuthenticationResponse(
            @Authentication URI authenticationUri, RestClientFactory factory,
            @Named(PROPERTY_RACKSPACE_USER) String user, @Named(PROPERTY_RACKSPACE_KEY) String key) {
      return factory.create(authenticationUri, RackspaceAuthentication.class).authenticate(user,
               key);
   }

   @Provides
   @Authentication
   protected String provideAuthenticationToken(@Authentication URI authenticationUri,
            RestClientFactory factory, @Named(PROPERTY_RACKSPACE_USER) String user,
            @Named(PROPERTY_RACKSPACE_KEY) String key) {
      return factory.create(authenticationUri, RackspaceAuthentication.class).authenticate(user,
               key).getAuthToken();
   }

   @Provides
   @Singleton
   @Storage
   protected URI provideStorageUrl(AuthenticationResponse response) {
      return response.getStorageUrl();
   }

   @Provides
   @Singleton
   @Server
   protected URI provideServerUrl(AuthenticationResponse response) {
      return response.getServerManagementUrl();
   }

   @Provides
   @Singleton
   @CDN
   protected URI provideCDNUrl(AuthenticationResponse response) {
      return response.getCDNManagementUrl();
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO retry on 401 by AuthenticateRequest.update()
   }

   @Singleton
   @Provides
   @Authentication
   protected URI provideAddress(@Named(HttpConstants.PROPERTY_HTTP_ADDRESS) String address,
            @Named(HttpConstants.PROPERTY_HTTP_PORT) int port,
            @Named(HttpConstants.PROPERTY_HTTP_SECURE) boolean isSecure)
            throws MalformedURLException {

      return URI.create(String.format("%1$s://%2$s:%3$s", isSecure ? "https" : "http", address,
               port));
   }

}