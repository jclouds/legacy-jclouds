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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudLogin;
import org.jclouds.vcloud.VCloudLogin.VCloudSession;
import org.jclouds.vcloud.config.VCloudDiscoveryRestClientModule;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.reference.VCloudConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudLoginLiveTest")
public class VCloudLoginLiveTest {

   private final class VCloudLoginContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudLogin> provideContext(Closer closer, VCloudLogin api, @VCloud URI endPoint,
               @Named(VCloudConstants.PROPERTY_VCLOUD_USER) String account) {
         return new RestContextImpl<VCloudLogin>(closer, api, endPoint, account);
      }

      @Override
      protected void configure() {

      }
   }

   String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint");
   String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

   private RestContext<VCloudLogin> context;

   @Test
   public void testLogin() throws Exception {
      VCloudLogin authentication = context.getApi();
      for (int i = 0; i < 5; i++) {
         VCloudSession response = authentication.login().get(45, TimeUnit.SECONDS);
         assertNotNull(response);
         assertNotNull(response.getVCloudToken());
         assertNotNull(response.getOrgs());
      }
   }

   @BeforeClass
   void setupFactory() {
      context = new RestContextBuilder<VCloudLogin>(new TypeLiteral<VCloudLogin>() {
      }, new Properties()) {

         public void addContextModule(List<Module> modules) {

            modules.add(new VCloudLoginContextModule());
         }

         @Override
         protected void addClientModule(List<Module> modules) {
            properties.setProperty(VCloudConstants.PROPERTY_VCLOUD_ENDPOINT, checkNotNull(endpoint,
                     "endpoint").toString());
            properties.setProperty(PROPERTY_VCLOUD_USER, checkNotNull(account, "user"));
            properties.setProperty(PROPERTY_VCLOUD_KEY, checkNotNull(key, "key"));
            properties.setProperty(PROPERTY_VCLOUD_SESSIONINTERVAL, "4");
            modules.add(new VCloudDiscoveryRestClientModule());
         }

      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService())).buildContext();
   }
}
