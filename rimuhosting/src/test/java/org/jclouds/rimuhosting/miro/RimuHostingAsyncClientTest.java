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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code RimuHostingAsyncClient}
 * 
 * @author Ivan Meredith
 */
@Test(groups = "unit", testName = "rimuhosting.RimuHostingAsyncClientTest")
public class RimuHostingAsyncClientTest extends RestClientTest<RimuHostingAsyncClient> {

   public void testGetMyMentions() throws SecurityException, NoSuchMethodException, IOException {
      /*
       * Method method = RimuHostingAsyncClient.class.getMethod("TODO: insert test method name");
       * GeneratedHttpRequest<RimuHostingAsyncClient> httpMethod = processor.createRequest(method);
       * 
       * assertRequestLineEquals(httpMethod, "TODO: insert expected request");
       * assertHeadersEqual(httpMethod, ""); assertPayloadEquals(httpMethod, null);
       * 
       * assertResponseParserClassEquals(method, httpMethod, ParseStatusesFromJsonResponse.class);
       * assertSaxResponseParserClassEquals(method, null); assertExceptionParserClassEquals(method,
       * null);
       * 
       * checkFilters(httpMethod);
       */
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<RimuHostingAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<RimuHostingAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), checkNotNull(new RimuHostingPropertiesBuilder(
                     new Properties()).build(), "properties"));
            bind(URI.class).annotatedWith(RimuHosting.class).toInstance(
                     URI.create("https://rimuhosting.com/r"));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication(EncryptionService encryptionService)
                  throws UnsupportedEncodingException {
            return new BasicAuthentication("foo", "bar", encryptionService);
         }

      };
   }
}
