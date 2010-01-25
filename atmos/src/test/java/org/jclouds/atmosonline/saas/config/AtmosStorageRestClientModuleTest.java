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
package org.jclouds.atmosonline.saas.config;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.handlers.AtmosStorageClientErrorRetryHandler;
import org.jclouds.atmosonline.saas.handlers.ParseAtmosStorageErrorFromXmlContent;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.encryption.internal.Base64;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.AtmosStorageRestClientModuleTest")
public class AtmosStorageRestClientModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new AtmosStorageRestClientModule(), new ExecutorServiceModule(
               sameThreadExecutor(), sameThreadExecutor()), new ParserModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_UID)).to("uid");
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY)).to(
                              new String(Base64.encodeBytes("key".getBytes())));
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_SESSIONINTERVAL))
                              .to("2");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS)).to("1");
                     bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS))
                              .to("1");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT)).to("0");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST)).to("1");
                  }
               });
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      AtmosStorageRestClientModule module = new AtmosStorageRestClientModule();

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
               ParseAtmosStorageErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(),
               ParseAtmosStorageErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AtmosStorageClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(), RedirectionRetryHandler.class);
   }

}