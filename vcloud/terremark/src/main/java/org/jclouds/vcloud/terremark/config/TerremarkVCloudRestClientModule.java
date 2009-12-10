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
package org.jclouds.vcloud.terremark.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;

import com.google.common.base.Predicate;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TerremarkVCloudRestClientModule extends VCloudRestClientModule {
   @Provides
   @Singleton
   protected Predicate<InetSocketAddress> socketTester(SocketOpen open) {
      return new RetryablePredicate<InetSocketAddress>(open, 130, 10, TimeUnit.SECONDS);// make it
                                                                                        // longer
                                                                                        // then
      // default internet
   }

   @Provides
   @Singleton
   protected Predicate<InetAddress> addressTester(AddressReachable reachable) {
      return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<URI> successTester(TaskSuccess success) {
      return new RetryablePredicate<URI>(success, 300, 10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected TerremarkVCloudAsyncClient provideTerremarkVCloudAsyncClient(VCloudAsyncClient in) {
      return (TerremarkVCloudAsyncClient) in;
   }

   @Override
   protected VCloudAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(TerremarkVCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   protected TerremarkVCloudClient provideTerremarkVCloudClient(VCloudClient in) {
      return (TerremarkVCloudClient) in;
   }

   @Override
   public VCloudClient provideClient(VCloudAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(TerremarkVCloudClient.class, client);
   }

   @Override
   protected String provideInstantiateVAppTemplateParams() throws IOException {
      InputStream is = getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams.xml");
      return Utils.toStringAndClose(is);
   }

   @Singleton
   @Provides
   @Named("CreateInternetService")
   String provideCreateInternetService() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService.xml"));
   }

   @Singleton
   @Provides
   @Named("CreateNodeService")
   String provideCreateNodeService() throws IOException {
      return Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService.xml"));
   }
}
