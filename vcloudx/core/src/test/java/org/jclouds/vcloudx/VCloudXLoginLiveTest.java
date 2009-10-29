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
package org.jclouds.vcloudx;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_KEY;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL;
import static org.jclouds.vcloudx.reference.VCloudXConstants.PROPERTY_VCLOUDX_USER;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloudx.VCloudXLogin.VCloudXSession;
import org.jclouds.vcloudx.config.RestVCloudXAuthenticationModule;
import org.jclouds.vcloudx.endpoints.VCloudX;
import org.jclouds.vcloudx.reference.VCloudXConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudXLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloudx.VCloudXLoginLiveTest")
public class VCloudXLoginLiveTest {

   private final class VCloudXLoginContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudXLogin> provideContext(Closer closer, VCloudXLogin api,
               @VCloudX URI endPoint, @Named(VCloudXConstants.PROPERTY_VCLOUDX_USER) String account) {
         return new RestContextImpl<VCloudXLogin>(closer, api, endPoint, account);
      }

      @Override
      protected void configure() {

      }
   }

   String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint");
   String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

   private RestContext<VCloudXLogin> context;

   @Test
   public void testLogin() throws Exception {
      VCloudXLogin authentication = context.getApi();
      for (int i = 0; i < 5; i++) {
         VCloudXSession response = authentication.login();
         assertNotNull(response);
         assertNotNull(response.getVCloudToken());
         assertNotNull(response.getOrg());
      }
   }

   @BeforeClass
   void setupFactory() {
      context = new RestContextBuilder<VCloudXLogin>(new TypeLiteral<VCloudXLogin>() {
      }, new Properties()) {

         public void addContextModule(List<Module> modules) {

            modules.add(new VCloudXLoginContextModule());
         }

         @Override
         protected void addClientModule(List<Module> modules) {
            properties.setProperty(VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT, checkNotNull(
                     endpoint, "endpoint").toString());
            properties.setProperty(PROPERTY_VCLOUDX_USER, checkNotNull(account, "user"));
            properties.setProperty(PROPERTY_VCLOUDX_KEY, checkNotNull(key, "key"));
            properties.setProperty(PROPERTY_VCLOUDX_SESSIONINTERVAL, "4");
            modules.add(new RestVCloudXAuthenticationModule());
         }

      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService())).buildContext();
   }
}
