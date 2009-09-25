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
package org.jclouds.azure.storage.filters;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.azure.storage.AzureBlob;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.Endpoint;
import org.jclouds.rest.QueryParams;
import org.jclouds.rest.RequestFilters;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "azure.SharedKeyAuthenticationLiveTest")
public class SharedKeyAuthenticationLiveTest {

   @RequestFilters(SharedKeyAuthentication.class)
   @Endpoint(AzureBlob.class)
   public interface IntegrationTestClient {

      @GET
      @Path("/")
      @QueryParams(keys = "comp", values = "list")
      String authenticate();

   }

   private Injector injector;
   private IntegrationTestClient client;
   private String uri;

   @Test
   public void testAuthentication() throws Exception {
      String response = client.authenticate();
      assertTrue(response.contains(uri), String.format("expected %s to contain %s", response, uri));
   }

   @BeforeClass
   void setupFactory() {
      final String account = checkNotNull(System.getProperty("jclouds.test.user"),
               "jclouds.test.user");
      final String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      uri = "http://" + account + ".blob.core.windows.net";
      injector = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(AzureBlob.class).toInstance(URI.create(uri));
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(account);
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(key);
         }

      }, new JaxrsModule(), new Log4JLoggingModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());
      RestClientFactory factory = injector.getInstance(RestClientFactory.class);
      client = factory.create(IntegrationTestClient.class);
   }
}
