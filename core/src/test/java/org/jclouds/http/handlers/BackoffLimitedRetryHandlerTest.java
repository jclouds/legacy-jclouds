/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.http.handlers;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Provider;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.date.internal.DateServiceDateCodecFactory;
import org.jclouds.date.internal.DateServiceDateCodecFactory.DateServiceIso8601Codec;
import org.jclouds.date.internal.DateServiceDateCodecFactory.DateServiceRfc1123Codec;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.BaseJettyTest;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.ContentMetadataCodec.DefaultContentMetadataCodec;
import org.jclouds.io.Payloads;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.sun.jersey.api.uri.UriBuilderImpl;

@Test(groups = "unit", testName = "BackoffLimitedRetryHandlerTest")
public class BackoffLimitedRetryHandlerTest {

   BackoffLimitedRetryHandler handler = new BackoffLimitedRetryHandler();

   @Test
   void testExponentialBackoffDelayDefaultMaxInterval500() throws InterruptedException {
      long period = 500;
      long acceptableDelay = period - 1;

      long startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 1, 5, "TEST FAILURE: 1");
      long elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= period - 1) : elapsedTime;
      assertTrue(elapsedTime < period + acceptableDelay);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 2, 5, "TEST FAILURE: 2");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= period * 4 - 1) : elapsedTime;
      assertTrue(elapsedTime < period * 9);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 3, 5, "TEST FAILURE: 3");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= period * 9 - 1) : elapsedTime;
      assertTrue(elapsedTime < period * 10);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 4, 5, "TEST FAILURE: 4");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= period * 10 - 1) : elapsedTime;
      assertTrue(elapsedTime < period * 11);

      startTime = System.nanoTime();
      handler.imposeBackoffExponentialDelay(period, 2, 5, 5, "TEST FAILURE: 5");
      elapsedTime = (System.nanoTime() - startTime) / 1000000;
      assert (elapsedTime >= period * 10 - 1) : elapsedTime;
      assertTrue(elapsedTime < period * 11);

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
      BackoffLimitedRetryHandler backoff = new BackoffLimitedRetryHandler();
      HttpUtils utils = new HttpUtils(0, 500, 1, 1);
      ContentMetadataCodec contentMetadataCodec = new DefaultContentMetadataCodec(new DateServiceDateCodecFactory(
               new DateServiceRfc1123Codec(new SimpleDateFormatDateService()), new DateServiceIso8601Codec(
                        new SimpleDateFormatDateService())));
      RedirectionRetryHandler retry = new RedirectionRetryHandler(uriBuilderProvider, backoff);
      JavaUrlHttpCommandExecutorService httpService = new JavaUrlHttpCommandExecutorService(utils, 
               contentMetadataCodec, execService,
               new DelegatingRetryHandler(backoff, retry), new BackoffLimitedRetryHandler(),
               new DelegatingErrorHandler(), new HttpWire(), new HostnameVerifier() {

                  @Override
                  public boolean verify(String hostname, SSLSession session) {
                     return false;
                  }
               }, new Supplier<SSLContext>() {

                  @Override
                  public SSLContext get() {
                     return null;
                  }

               });
      executorService = new TransformingHttpCommandExecutorServiceImpl(httpService, execService);
   }

   @Test
   void testClosesInputStream() throws InterruptedException, IOException, SecurityException, NoSuchMethodException {
      HttpCommand command = createCommand();

      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      InputStream inputStream = new InputStream() {
         boolean isOpen = true;

         @Override
         public void close() {
            this.isOpen = false;
         }

         int count = 1;

         @Override
         public int read() throws IOException {
            if (this.isOpen)
               return (count > -1) ? count-- : -1;
            else
               return -1;
         }

         @Override
         public int available() throws IOException {
            if (this.isOpen)
               return count;
            else
               return 0;
         }
      };
      response.setPayload(Payloads.newInputStreamPayload(inputStream));
      response.getPayload().getContentMetadata().setContentLength(1l);
      assertEquals(response.getPayload().getInput().available(), 1);
      assertEquals(response.getPayload().getInput().read(), 1);

      handler.shouldRetryRequest(command, response);

      assertEquals(response.getPayload().getInput().available(), 0);
      assertEquals(response.getPayload().getInput().read(), -1);
   }

   private final RestAnnotationProcessor<IntegrationTestAsyncClient> processor = BaseJettyTest.newBuilder(8100,
            new Properties()).buildInjector().getInstance(
            Key.get(new TypeLiteral<RestAnnotationProcessor<IntegrationTestAsyncClient>>() {
            }));

   private HttpCommand createCommand() throws SecurityException, NoSuchMethodException {
      Method method = IntegrationTestAsyncClient.class.getMethod("download", String.class);

      return new TransformingHttpCommandImpl<String>(executorService, processor.createRequest(method, "1"),
               new ReturnStringIf2xx());
   }

   @Test
   void testIncrementsFailureCount() throws InterruptedException, IOException, SecurityException, NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 1);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 2);

      handler.shouldRetryRequest(command, response);
      assertEquals(command.getFailureCount(), 3);
   }

   @Test
   void testDisallowsExcessiveRetries() throws InterruptedException, IOException, SecurityException,
            NoSuchMethodException {
      HttpCommand command = createCommand();
      HttpResponse response = HttpResponse.builder().statusCode(400).build();

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 1

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 2

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 3

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 4

      assertEquals(handler.shouldRetryRequest(command, response), true); // Failure 5

      assertEquals(handler.shouldRetryRequest(command, response), false); // Failure 6
   }

}
