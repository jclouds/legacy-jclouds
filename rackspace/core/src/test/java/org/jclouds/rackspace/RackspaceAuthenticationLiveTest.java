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
package org.jclouds.rackspace;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloud.CloudContext;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.cloud.internal.CloudContextImpl;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.RackspaceAuthentication.AuthenticationResponse;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.jclouds.rest.RestClientFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "rackspace.RackspaceAuthenticationLiveTest")
public class RackspaceAuthenticationLiveTest {

   @ConfiguresCloudConnection
   @RequiresHttp
   private final class RestRackspaceAuthenticationConnectionModule extends AbstractModule {

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      RackspaceAuthentication provideConnection(RestClientFactory factory) {
         return factory.create(RackspaceAuthentication.class);
      }


      @Override
      protected void configure() {

      }
   }

   private final class RackspaceAuthenticationContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      CloudContext<RackspaceAuthentication> provideContext(Closer closer, RackspaceAuthentication api,
               @Authentication URI endPoint,
               @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
         return new CloudContextImpl<RackspaceAuthentication>(closer, api, endPoint, account);
      }

      @Override
      protected void configure() {

      }
   }

   String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

   private CloudContext<RackspaceAuthentication> context;

   @Test
   public void testAuthentication() throws Exception {
      RackspaceAuthentication authentication = context.getApi();
      AuthenticationResponse response = authentication.authenticate(account, key);
      assertNotNull(response);
      assertNotNull(response.getStorageUrl());
      assertNotNull(response.getCDNManagementUrl());
      assertNotNull(response.getServerManagementUrl());
      assertNotNull(response.getAuthToken());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testBadAuthentication() throws Exception {
      RackspaceAuthentication authentication = context.getApi();
      try {
         authentication.authenticate("foo", "bar");
      } catch (UndeclaredThrowableException e) {
         HttpResponseException ew = (HttpResponseException) e.getCause().getCause();
         assertEquals(ew.getResponse().getStatusCode(), 401);
         throw ew;
      }
      fail();
   }

   @BeforeClass
   void setupFactory() {
      context = new RackspaceContextBuilder<RackspaceAuthentication>(
               new TypeLiteral<RackspaceAuthentication>() {
               }, account, key) {
         @Override
         protected void addConnectionModule(List<Module> modules) {
            super.addConnectionModule(modules);
            modules.add(new RestRackspaceAuthenticationConnectionModule());
         }

         public void addContextModule(List<Module> modules) {
            modules.add(new RackspaceAuthenticationContextModule());
         }
      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService())).buildContext();
   }
}
