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
package org.jclouds.vcloudx.config;

import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_KEY;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_USER;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.CloseContentAndSetExceptionErrorHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.util.Jsr330;
import org.jclouds.vcloudx.VCloudXLogin;
import org.jclouds.vcloudx.VCloudXLogin.VCloudXSession;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloudx.RestVCloudXAuthenticationModuleTest")
public class RestVCloudXAuthenticationModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new RestVCloudXAuthenticationModule(), new ParserModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUDX_USER)).to("user");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUDX_KEY)).to("secret");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUDX_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(Jsr330.named(PROPERTY_VCLOUDX_SESSIONINTERVAL))
                              .to("2");
                  }
               });
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      RestVCloudXAuthenticationModule module = new RestVCloudXAuthenticationModule();
      VCloudXLogin login = new VCloudXLogin() {

         private final AtomicInteger token = new AtomicInteger();

         public VCloudXSession login() {
            return new VCloudXSession() {

               public URI getOrg() {
                  return null;
               }

               public String getVCloudToken() {
                  return token.incrementAndGet() + "";
               }

            };
         }

      };
      Supplier<VCloudXSession> map = module.provideVCloudTokenCache(1, login);
      for (int i = 0; i < 10; i++)
         map.get();
      assert "1".equals(map.get().getVCloudToken());
      Thread.sleep(1001);
      assert "2".equals(map.get().getVCloudToken());
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(),
               CloseContentAndSetExceptionErrorHandler.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(),
               CloseContentAndSetExceptionErrorHandler.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler(), HttpRetryHandler.NEVER_RETRY);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
   }

}