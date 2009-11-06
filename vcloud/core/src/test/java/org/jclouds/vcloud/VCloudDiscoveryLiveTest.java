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
import static org.testng.Assert.assertEquals;
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
import org.jclouds.vcloud.config.VCloudDiscoveryRestClientModule;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.reference.VCloudConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudDiscoveryLiveTest")
public class VCloudDiscoveryLiveTest {

   String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint");
   String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
   private final class VCloudDiscoveryContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudDiscovery> provideContext(Closer closer, VCloudDiscovery api,
               @VCloud URI endPoint, @Named(VCloudConstants.PROPERTY_VCLOUD_USER) String account) {
         return new RestContextImpl<VCloudDiscovery>(closer, api, endPoint, account);
      }

      @Override
      protected void configure() {

      }
   }
   
   private RestContext<VCloudDiscovery> context;

   @Test
   public void testOrganization() throws Exception {
      Organization response = context.getApi().getOrganization();
      assertNotNull(response);
      assertEquals(response.getName(), account);
      assertEquals(response.getType(), VCloudMediaType.ORG_XML);
      assertNotNull(response.getCatalog());
      assertEquals(response.getTasksLists().size(), 1);
      assertEquals(response.getVDCs().size(), 1);
   }

   @BeforeClass
   void setupFactory() {
      context = new RestContextBuilder<VCloudDiscovery>(new TypeLiteral<VCloudDiscovery>() {
      }, new Properties()) {

         public void addContextModule(List<Module> modules) {

            modules.add(new VCloudDiscoveryContextModule());
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
