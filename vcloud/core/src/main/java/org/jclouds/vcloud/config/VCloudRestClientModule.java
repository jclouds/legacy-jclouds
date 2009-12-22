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

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.endpoints.internal.VAppTemplateRoot;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.predicates.TaskSuccess;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
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

   @Provides
   @Singleton
   protected Predicate<InetSocketAddress> socketTester(SocketOpen open) {
      return new RetryablePredicate<InetSocketAddress>(open, 130, 10, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<InetAddress> addressTester(AddressReachable reachable) {
      return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Predicate<String> successTester(TaskSuccess success) {
      return new RetryablePredicate<String>(success, 600, 10, TimeUnit.SECONDS);
   }

   @Override
   protected void configure() {
   }

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return cache.get().getVCloudToken();
   }

   @Provides
   @Org
   @Singleton
   protected URI provideOrg(Supplier<VCloudSession> cache, @Named(PROPERTY_VCLOUD_USER) String user) {
      return Iterables.getLast(cache.get().getOrgs().values()).getLocation();
   }

   @Provides
   @VCloudApi
   @Singleton
   URI provideVCloudApi(@VCloudLogin URI vcloudUri) {
      return URI.create(vcloudUri.toASCIIString().replace("/login", ""));
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   Supplier<VCloudSession> provideVCloudTokenCache(
            @Named(PROPERTY_VCLOUD_SESSIONINTERVAL) long seconds, final VCloudLoginAsyncClient login) {
      return new ExpirableSupplier<VCloudSession>(new Supplier<VCloudSession>() {
         public VCloudSession get() {
            try {
               return login.login().get(180, TimeUnit.SECONDS);
            } catch (Exception e) {
               Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               throw new RuntimeException("Error logging in", e);
            }
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @VCloud
   protected URI provideBaseURI(@Named(PROPERTY_VCLOUD_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VCloudLogin
   protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
            @Named(PROPERTY_VCLOUD_VERSION) String version) throws InterruptedException,
            ExecutionException, TimeoutException {
      return versionService.getSupportedVersions().get(180, TimeUnit.SECONDS).get(version);
   }

   @Provides
   @Singleton
   protected VCloudLoginAsyncClient provideVCloudLogin(RestClientFactory factory) {
      return factory.create(VCloudLoginAsyncClient.class);
   }

   @Provides
   @Singleton
   protected VCloudVersionsAsyncClient provideVCloudVersions(RestClientFactory factory) {
      return factory.create(VCloudVersionsAsyncClient.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(@Named(PROPERTY_VCLOUD_USER) String user,
            @Named(PROPERTY_VCLOUD_KEY) String key, EncryptionService encryptionService)
            throws UnsupportedEncodingException {
      return new BasicAuthentication(user, key, encryptionService);
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
   @CatalogItemRoot
   @Singleton
   String provideCatalogItemRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/catalogItem");
   }

   @Provides
   @VAppRoot
   @Singleton
   String provideVAppRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/vapp");
   }

   @Provides
   @VAppTemplateRoot
   @Singleton
   String provideVAppTemplateRoot(@VCloudLogin URI vcloudUri) {
      return vcloudUri.toASCIIString().replace("/login", "/vAppTemplate");
   }

   @Provides
   @Singleton
   protected Organization provideOrganization(VCloudClient discovery) throws ExecutionException,
            TimeoutException, InterruptedException {
      return discovery.getOrganization();
   }

   @Provides
   @VDC
   @Singleton
   protected URI provideDefaultVDC(Organization org) {
      return org.getVDCs().values().iterator().next().getLocation();
   }

   @Provides
   @Catalog
   @Singleton
   protected URI provideCatalog(Organization org) {
      return org.getCatalog().getLocation();
   }

   @Provides
   @Network
   @Singleton
   protected URI provideDefaultNetwork(VCloudAsyncClient client) throws InterruptedException,
            ExecutionException, TimeoutException {
      return client.getDefaultVDC().get(180, TimeUnit.SECONDS).getAvailableNetworks().values()
               .iterator().next().getLocation();
   }

   @Provides
   @Named(PROPERTY_VCLOUD_DEFAULT_NETWORK)
   @Singleton
   String provideDefaultNetworkString(@Network URI network) {
      return network.toASCIIString();
   }

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(Organization org) {
      return org.getTasksLists().values().iterator().next().getLocation();
   }
}
