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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.PCSCloud.Response;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.RestModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSCloudLiveTest")
public class PCSCloudLiveTest {

   String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
   String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
   URI endpoint = URI.create(checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint"));

   private Injector injector;

   @Test
   public void testAuthentication() throws Exception {
      PCSCloud authentication = injector.getInstance(PCSCloud.class);
      Response response = authentication.authenticate();
      assertNotNull(response);
      assertNotNull(response.getContactsUrl());
      assertNotNull(response.getMetacontainersUrl());
      assertNotNull(response.getProjectsUrl());
      assertNotNull(response.getRecyclebinUrl());
      assertNotNull(response.getRootContainerUrl());
      assertNotNull(response.getSharesUrl());
      assertNotNull(response.getTagsUrl());
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(PCS.class).toInstance(endpoint);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication()
                  throws UnsupportedEncodingException {
            return new BasicAuthentication(user, password);
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected PCSCloud provideCloud(RestClientFactory factory) {
            return factory.create(PCSCloud.class);
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
   }
}
