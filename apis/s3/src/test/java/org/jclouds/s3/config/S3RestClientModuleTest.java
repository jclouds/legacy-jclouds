/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.s3.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.handlers.AWSRedirectionRetryHandler;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientTest.MockModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.handlers.ParseS3ErrorFromXmlContent;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class S3RestClientModuleTest {

   Injector createInjector() throws IOException {
      return new RestContextFactory().createContextBuilder("s3", "foo", "bar",
               ImmutableSet.of(new MockModule(), new NullLoggingModule()), new Properties())
               .buildInjector();

   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      S3RestClientModule<S3Client, S3AsyncClient> module = S3RestClientModule.create();

      Supplier<String> map = module.provideTimeStampCache(1, new SimpleDateFormatDateService());
      String timeStamp = map.get();
      for (int i = 0; i < 10; i++)
         map.get();
      assertEquals(timeStamp, map.get());
      Thread.sleep(1001);
      assertFalse(timeStamp.equals(map.get()));
   }

   @Test
   void testServerErrorHandler() throws IOException {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(), ParseS3ErrorFromXmlContent.class);
   }

   @Test
   void testClientErrorHandler() throws IOException {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(), ParseS3ErrorFromXmlContent.class);
   }

   @Test
   void testClientRetryHandler() throws IOException {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getClientErrorRetryHandler().getClass(),
               AWSClientErrorRetryHandler.class);
   }

   @Test
   void testRedirectionRetryHandler() throws IOException {
      DelegatingRetryHandler handler = createInjector().getInstance(DelegatingRetryHandler.class);
      assertEquals(handler.getRedirectionRetryHandler().getClass(),
               AWSRedirectionRetryHandler.class);
   }

}
