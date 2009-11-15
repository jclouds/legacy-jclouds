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
package org.jclouds.vcloud.config;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudDiscovery;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class VCloudRestClientModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   protected VCloudAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(VCloudAsyncClient.class);
   }

   @Provides
   @Singleton
   public VCloudClient provideClient(VCloudAsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(VCloudClient.class, client);
   }

   @Provides
   @Catalog
   @Singleton
   protected URI provideCatalog(VCloudDiscovery discovery) throws InterruptedException, ExecutionException, TimeoutException {
      return discovery.getOrganization().get(45, TimeUnit.SECONDS).getCatalog().getLocation();
   }

   @Provides
   @CatalogItemRoot
   @Singleton
   String provideCatalogItemRoot(@VCloud URI vcloudUri) {
      return vcloudUri.toASCIIString() + "/catalogItem";
   }

   @Provides
   @VAppRoot
   @Singleton
   String provideVAppRoot(@VCloud URI vcloudUri) {
      return vcloudUri.toASCIIString() + "/vapp";
   }

   @Provides
   @VDC
   @Singleton
   protected URI provideDefaultVDC(VCloudDiscovery discovery) throws InterruptedException, ExecutionException, TimeoutException {
      return discovery.getOrganization().get(45, TimeUnit.SECONDS).getVDCs().values().iterator()
               .next().getLocation();
   }

   @Provides
   @Network
   @Singleton
   protected URI provideDefaultNetwork(VCloudAsyncClient client) throws InterruptedException,
            ExecutionException, TimeoutException {
      return client.getDefaultVDC().get(45, TimeUnit.SECONDS).getAvailableNetworks().values()
               .iterator().next().getLocation();
   }

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(VCloudDiscovery discovery) throws InterruptedException, ExecutionException, TimeoutException {
      return discovery.getOrganization().get(45, TimeUnit.SECONDS).getTasksLists().values().iterator().next().getLocation();
   }
}
