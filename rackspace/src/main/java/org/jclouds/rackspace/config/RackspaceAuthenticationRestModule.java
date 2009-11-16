/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_ENDPOINT;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.CloudFilesCDN;
import org.jclouds.rackspace.CloudServers;
import org.jclouds.rackspace.RackspaceAuthentication;
import org.jclouds.rackspace.RackspaceAuthentication.AuthenticationResponse;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the Rackspace authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RackspaceAuthenticationRestModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   @Authentication
   protected URI provideAuthenticationURI(@Named(PROPERTY_RACKSPACE_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected AuthenticationResponse provideAuthenticationResponse(RestClientFactory factory,
            @Named(PROPERTY_RACKSPACE_USER) String user, @Named(PROPERTY_RACKSPACE_KEY) String key)
            throws InterruptedException, ExecutionException, TimeoutException {
      return factory.create(RackspaceAuthentication.class).authenticate(user, key).get(30,
               TimeUnit.SECONDS);
   }

   @Provides
   @Authentication
   protected String provideAuthenticationToken(RestClientFactory factory,
            @Named(PROPERTY_RACKSPACE_USER) String user, @Named(PROPERTY_RACKSPACE_KEY) String key)
            throws InterruptedException, ExecutionException, TimeoutException {
      return factory.create(RackspaceAuthentication.class).authenticate(user, key).get(30,
               TimeUnit.SECONDS).getAuthToken();
   }

   @Provides
   @Singleton
   @CloudFiles
   protected URI provideStorageUrl(AuthenticationResponse response) {
      return response.getStorageUrl();
   }

   @Provides
   @Singleton
   @CloudServers
   protected URI provideServerUrl(AuthenticationResponse response) {
      return response.getServerManagementUrl();
   }

   @Provides
   @Singleton
   @CloudFilesCDN
   protected URI provideCDNUrl(AuthenticationResponse response) {
      return response.getCDNManagementUrl();
   }
}