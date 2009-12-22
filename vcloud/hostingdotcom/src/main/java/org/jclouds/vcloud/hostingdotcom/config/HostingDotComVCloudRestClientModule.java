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
package org.jclouds.vcloud.hostingdotcom.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudAsyncClient;
import org.jclouds.vcloud.hostingdotcom.HostingDotComVCloudClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class HostingDotComVCloudRestClientModule extends VCloudRestClientModule {

   @Provides
   @Singleton
   protected HostingDotComVCloudAsyncClient provideHostingDotComVCloudAsyncClient(
            VCloudAsyncClient in) {
      return (HostingDotComVCloudAsyncClient) in;
   }

   @Override
   protected VCloudAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(HostingDotComVCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   protected HostingDotComVCloudClient provideHostingDotComVCloudClient(VCloudClient in) {
      return (HostingDotComVCloudClient) in;
   }

   @Override
   public VCloudClient provideClient(VCloudAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(HostingDotComVCloudClient.class, client);
   }

   @Override
   protected URI provideDefaultNetwork(VCloudAsyncClient client) {
      return URI.create("https://vcloud.safesecureweb.com/network/1990");
   }

}
