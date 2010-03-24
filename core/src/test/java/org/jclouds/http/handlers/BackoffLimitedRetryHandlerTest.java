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
package org.jclouds.http.handlers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.PropertiesBuilder;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

@Test(groups = "unit", testName = "core.BackoffLimitedRetryHandler")
public class BackoffLimitedRetryHandlerTest {

   BackoffLimitedRetryHandler handler = new BackoffLimitedRetryHandler();

   @Test
   void testExponentialBackoffDelay() throws InterruptedException {
      long acceptableDelay = 25; // Delay to forgive if tests run long.

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(1, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= 49) : elapsedTime;
      assertTrue(elapsedTime < 50 + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(2, "TEST FAILURE: 2");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= 199) : elapsedTime;
      assertTrue(elapsedTime < 200 + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(3, "TEST FAILURE: 3");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= 449) : elapsedTime;
      assertTrue(elapsedTime < 450 + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(4, "TEST FAILURE: 4");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= 799) : elapsedTime;
      assertTrue(elapsedTime < 800 + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(5, "TEST FAILURE: 5");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= 1249) : elapsedTime;
      assertTrue(elapsedTime < 1250 + acceptableDelay);
   }

   TransformingHttpCommandExecutorServiceImpl executorService;
   Provider<UriBuilder> uriBuilderProvider = new Provider<UriBuilder>() {

      @Override
      public UriBuilder get() {
         return new UriBuilderImpl();
      }

   };

   @BeforeTest
   void setupExecutorService() throws Exception {
      ExecutorService execService = Executors.newCachedThreadPool();
      JavaUrlHttpCommandExecutorService httpService = new JavaUrlHttpCommandExecutorService(
               execService, new DelegatingRetryHandler(uriBuilderProvider),
               new DelegatingErrorHandler(), new HttpWire(), new HttpUtils(0, 500, 1, 1), null);
      executorService = new TransformingHttpCommandExecutorServiceImpl(httpService);
   }

   @Test
   void testClosesInputStream() throws InterruptedException, IOException, SecurityException,
            NoSuchMethodException {
      HttpCommand command = createCommand();

      HttpResponse response = new HttpResponse();
      InputStream inputStream = new InputStream() {
         boolean isOpen = true;

         @Override
         public void close() {
            this.isOpen = false;
         }

         @Override
         public int read() throws IOException {
            if (this.isOpen)
               return 1;
            else
               return -1;
         }

         @Override
         public int available() throws IOException {
            if (this.isOpen)
               return 1;
            else
               return 0;
         }
      };
      response.setContent(inputStream);

      assertEquals(response.getContent().available(), 1);
      assertEquals(response.getContent().read(), 1);

      handler.shouldRetryRequest(command, response);

      assertEquals(response.getContent().available(), 0);
      assertEquals(response.getContent().read(), -1);
   }

   private final RestAnnotationProcessor<IntegrationTestAsyncClient> processor = BaseJettyTest
            .newBuilder(8100, new PropertiesBuilder() {

               @Override
               public PropertiesBuilder withCredentials(String account, String key) {
                  return null;
               }

               @Override
               public PropertiesBuilder withEndpoint(URI endpoint) {
                  return null;
               }
            }.build(), new AbstractModule() {

               @Override
               protected void configure() {

               }
            })
            .buildInjector()
            .getInstance(
                     Key
                              .get(new TypeLiteral<RestAnnotationProcessor<IntegrationTestAsyncClient>>() {
                              }));

   private HttpCommand createCommand() throws SecurityException, NoSuchMethodException {
      Method method = IntegrationTestAsyncClient.class.getMethod("download", String.class);

      return new TransformingHttpCommandImpl<String>(uriBuilderProvider, executorService, processor
               .createRequest(method, "1"), new ReturnStringIf200());
   }

   @Test
   void testIncrementsFailureCount() throws InterruptedException, IOException, SecurityException,
            NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = new HttpResponse();

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 1);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 2);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 3);
   }

   @Test
   void testDisallowsExcessiveRetries() throws InterruptedException, IOException,
            SecurityException, NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = new HttpResponse();

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 1

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 2

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 3

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 4

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 5

      assertEquals(handler.shouldRetryRequest(command, response), false); // Failure 6
   }

}