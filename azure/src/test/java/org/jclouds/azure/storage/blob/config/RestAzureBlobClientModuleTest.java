/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.azure.storage.blob.config;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.Constants;
import org.jclouds.azure.storage.blob.handlers.AzureBlobClientErrorRetryHandler;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.RestAzureBlobClientModuleTest")
public class RestAzureBlobClientModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new AzureBlobRestClientModule(), new ExecutorServiceModule(
               sameThreadExecutor(), sameThreadExecutor()), new ParserModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(
                              "user");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                              Base64.encodeBytes("secret".getBytes()));
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS)).to("1");
                     bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS))
                              .to("1");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT)).to("0");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST)).to("1");
                     bindConstant()
                              .annotatedWith(
                                       Jsr330
                                                .named(AzureStorageConstants.PROPERTY_AZURESTORAGE_SESSIONINTERVAL))
                              .to(1l);
                     bind(UriBuilder.class).to(UriBuilderImpl.class);
                  }
               });
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(),
               ParseAzureStorageErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(),
               ParseAzureStorageErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AzureBlobClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
   }

}