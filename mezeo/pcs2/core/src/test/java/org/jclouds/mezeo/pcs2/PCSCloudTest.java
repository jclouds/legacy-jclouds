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
package org.jclouds.mezeo.pcs2;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSCloudTest")
public class PCSCloudTest {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = PCSCloud.class.getMethod("authenticate");
      GeneratedHttpRequest<PCSCloud> httpMethod = processor.createRequest(method);
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost:8080/ HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method),
               CloudXlinkHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   private RestAnnotationProcessor<PCSCloud> processor;

   @BeforeClass
   void setupFactory() {

      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(PCS.class)
                     .toInstance(URI.create("http://localhost:8080"));
            Jsr330.bindProperties(this.binder(), new PCSPropertiesBuilder(URI
                     .create("http://localhost:8080"), "user", "key").build());
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
      }, new RestModule(), new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()),
               new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<PCSCloud>>() {
               }));
   }
}
