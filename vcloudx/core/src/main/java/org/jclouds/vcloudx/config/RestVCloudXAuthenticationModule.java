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
package org.jclouds.vcloudx.config;

import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_KEY;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_USER;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.vcloudx.VCloudXLogin;
import org.jclouds.vcloudx.VCloudToken;
import org.jclouds.vcloudx.VCloudXLogin.VCloudXSession;
import org.jclouds.vcloudx.endpoints.Org;
import org.jclouds.vcloudx.endpoints.VCloudX;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the VCloudX authentication service connection, including logging and http
 * transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class RestVCloudXAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @VCloudToken
   @Provides
   String provideVCloudToken(ConcurrentMap<String, VCloudXSession> cache) {
      return cache.get("doesn't matter").getVCloudToken();
   }
   
   @Provides
   @Org
   protected URI provideOrg(ConcurrentMap<String, VCloudXSession> cache) {
      return cache.get("doesn't matter").getOrg();
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   ConcurrentMap<String, VCloudXSession> provideVCloudTokenCache(
            @Named(PROPERTY_VCLOUDX_SESSIONINTERVAL) long seconds, final VCloudXLogin login) {
      return new MapMaker().expiration(seconds, TimeUnit.SECONDS).makeComputingMap(
               new Function<String, VCloudXSession>() {
                  public VCloudXSession apply(String key) {
                     return login.login();
                  }
               });
   }

   @Provides
   @Singleton
   @VCloudX
   protected URI provideAuthenticationURI(@Named(PROPERTY_VCLOUDX_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected VCloudXLogin provideVCloudXLogin(RestClientFactory factory) {
      return factory.create(VCloudXLogin.class);
   }

   @Provides
   @Singleton
   public BasicAuthentication provideBasicAuthentication(@Named(PROPERTY_VCLOUDX_USER) String user,
            @Named(PROPERTY_VCLOUDX_KEY) String key) throws UnsupportedEncodingException {
      return new BasicAuthentication(user, key);
   }

}
