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
package org.jclouds.twitter.config;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.functions.config.ParserModule.CDateAdapter;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.http.handlers.CloseContentAndSetExceptionErrorHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.twitter.TwitterPropertiesBuilder;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "twitter.TwitterContextModule")
public class TwitterContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new TwitterRestClientModule(), new TwitterContextModule() {
         @Override
         protected void configure() {
            Jsr330.bindProperties(this.binder(), new TwitterPropertiesBuilder("user", "pass")
                     .build());
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            bind(UriBuilder.class).to(UriBuilderImpl.class);
            super.configure();
         }
      }, new ParserModule(), new JavaUrlHttpCommandExecutorServiceModule(),
               new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(),
               CloseContentAndSetExceptionErrorHandler.class);
   }

   @Test
   void testDateAdapter() {
      assertEquals(this.createInjector().getInstance(DateAdapter.class).getClass(),
               CDateAdapter.class);
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