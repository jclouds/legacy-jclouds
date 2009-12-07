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

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.ExpirableSupplier;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.VCloudDiscovery;
import org.jclouds.vcloud.VCloudLogin;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.VCloudVersions;
import org.jclouds.vcloud.VCloudLogin.VCloudSession;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.endpoints.VCloudApi;

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
public class VCloudDiscoveryRestClientModule extends AbstractModule {

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
   URI provideVCloudApi(@org.jclouds.vcloud.endpoints.VCloudLogin URI vcloudUri) {
      return URI.create(vcloudUri.toASCIIString().replace("/login", ""));
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   Supplier<VCloudSession> provideVCloudTokenCache(
            @Named(PROPERTY_VCLOUD_SESSIONINTERVAL) long seconds, final VCloudLogin login) {
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
   protected URI provideAuthenticationURI(VCloudVersions versionService,
            @Named(PROPERTY_VCLOUD_VERSION) String version) throws InterruptedException,
            ExecutionException, TimeoutException {
      return versionService.getSupportedVersions().get(180, TimeUnit.SECONDS).get(version);
   }

   @Provides
   @Singleton
   protected VCloudLogin provideVCloudLogin(RestClientFactory factory) {
      return factory.create(VCloudLogin.class);
   }

   @Provides
   @Singleton
   protected VCloudDiscovery provideVCloudDiscovery(RestClientFactory factory) {
      return factory.create(VCloudDiscovery.class);
   }

   @Provides
   @Singleton
   protected VCloudVersions provideVCloudVersions(RestClientFactory factory) {
      return factory.create(VCloudVersions.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(@Named(PROPERTY_VCLOUD_USER) String user,
            @Named(PROPERTY_VCLOUD_KEY) String key) throws UnsupportedEncodingException {
      return new BasicAuthentication(user, key);
   }

}
