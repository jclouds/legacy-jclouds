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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpConstants.PROPERTY_JSON_DEBUG;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code SDNAuthentication}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "sdn.SDNAuthenticationLiveTest")
public class SDNAuthenticationLiveTest {
   String app = checkNotNull(System.getProperty("jclouds.test.app"), "jclouds.test.app");
   String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

   private Injector injector;

   @Test
   public void testAuthentication() throws Exception {
      SDNAuthentication authentication = injector.getInstance(SDNAuthentication.class);
      String response = authentication.authenticate(app, user, password);
      assertNotNull(response);
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(SDN.class).toInstance(
                     URI.create("http://services.nirvanix.com/ws"));
            bindConstant().annotatedWith(Jsr330.named(PROPERTY_JSON_DEBUG)).to(true);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected SDNAuthentication provideCloud(RestClientFactory factory) {
            return factory.create(SDNAuthentication.class);
         }
      }, new JaxrsModule(), new Log4JLoggingModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());
   }
}
