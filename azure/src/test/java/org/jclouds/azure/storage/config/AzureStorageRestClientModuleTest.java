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
package org.jclouds.azure.storage.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.azure.storage.blob.config.AzureBlobRestClientModule;
import org.jclouds.azure.storage.handlers.AzureStorageClientErrorRetryHandler;
import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.rest.config.RestModule;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azure.RestAzureStorageClientModuleTest")
public class AzureStorageRestClientModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new AzureBlobRestClientModule(), new RestModule() {

         @Override
         protected void configure() {
            bind(TransformingHttpCommandExecutorService.class).toInstance(
                     createMock(TransformingHttpCommandExecutorService.class));
            super.configure();
         }

      }, new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     Names.bindProperties(binder(), checkNotNull(new AzureBlobPropertiesBuilder(
                              "user", "secret").build(), "properties"));
                  }
               });
   }

   @SuppressWarnings("unchecked")
   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      AzureStorageRestClientModule module = new AzureBlobRestClientModule();

      Supplier<String> map = module.provideTimeStampCache(1, new SimpleDateFormatDateService());
      String timeStamp = map.get();
      for (int i = 0; i < 10; i++)
         map.get();
      assertEquals(timeStamp, map.get());
      Thread.sleep(1001);
      assertFalse(timeStamp.equals(map.get()));
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
               AzureStorageClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
   }

}