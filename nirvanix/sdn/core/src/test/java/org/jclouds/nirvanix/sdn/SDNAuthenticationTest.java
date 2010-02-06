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
package org.jclouds.nirvanix.sdn;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.HttpMethod;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.nirvanix.sdn.functions.ParseSessionTokenFromJsonResponse;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "sdn.SDNAuthentication")
public class SDNAuthenticationTest {

   private RestAnnotationProcessor<SDNAuthentication> processor;

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = SDNAuthentication.class.getMethod("authenticate", String.class, String.class,
               String.class);
      HttpRequest httpMethod = processor.createRequest(method,
               new Object[] { "apple", "foo", "bar" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/ws/Authentication/Login.ashx");
      assertEquals(httpMethod.getEndpoint().getQuery(),
               "output=json&appKey=apple&password=bar&username=foo");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(RestAnnotationProcessor.getParserOrThrowException(method),
               ParseSessionTokenFromJsonResponse.class);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(SDN.class)
                     .toInstance(URI.create("http://localhost:8080"));
            Jsr330.bindProperties(this.binder(), new SDNPropertiesBuilder("user", "key", "key",
                     "key").build());
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }
      }, new RestModule(), new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()),
               new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<SDNAuthentication>>() {
               }));
   }
}
