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
package org.jclouds.aws.s3.config;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.Constants;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3RestClientModuleTest")
public class S3RestClientModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new S3RestClientModule(), new ExecutorServiceModule(
               sameThreadExecutor(), sameThreadExecutor()), new ParserModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to("key");
                     bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_S3_ENDPOINT))
                              .to("http://localhost");
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_S3_SESSIONINTERVAL)).to("2");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS)).to("1");
                     bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS))
                              .to("1");
                     bind(UriBuilder.class).to(UriBuilderImpl.class);
                  }
               });
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      S3RestClientModule module = new S3RestClientModule();

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
      assertEquals(handler.getServerErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(), ParseAWSErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AWSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(),
               AWSRedirectionRetryHandler.class);
   }

}