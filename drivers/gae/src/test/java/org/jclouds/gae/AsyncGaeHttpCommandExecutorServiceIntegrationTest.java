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
package org.jclouds.gae;

import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jclouds.concurrent.Futures;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.gae.config.GoogleAppEngineConfigurationModule;
import org.jclouds.http.BaseHttpCommandExecutorServiceIntegrationTest;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.logging.Logger;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * Integration test for the URLFetchService
 * 
 * @author Adrian Cole
 */
@Test
public class AsyncGaeHttpCommandExecutorServiceIntegrationTest extends BaseHttpCommandExecutorServiceIntegrationTest {
   Logger logger = Logger.CONSOLE;

   @Override
   protected void setupAndStartSSLServer(final int testPort) throws Exception {
   }

   protected boolean redirectEveryTwentyRequests(HttpServletRequest request, HttpServletResponse response)
         throws IOException {
      return false;
   }

   @Test(enabled = false)
   public void testPerformanceVsNothing() throws TimeoutException {
      setupApiProxy();
      int count = 5;
      final URI fetch = URI.create("http://www.google.com");

      final URLFetchService service = URLFetchServiceFactory.getURLFetchService();
      Results gae = getTest(count, "gae", new Supplier<ListenableFuture<?>>() {

         @Override
         public ListenableFuture<?> get() {
            try {
               return Futures.makeListenable(service.fetchAsync(fetch.toURL()), MoreExecutors.sameThreadExecutor());
            } catch (MalformedURLException e) {
               throw Throwables.propagate(e);
            }
         }

      }, new Consumer() {

         @SuppressWarnings("unchecked")
         @Override
         public void consume(Object in) {
            try {
               new String(((ListenableFuture<com.google.appengine.api.urlfetch.HTTPResponse>) in).get().getContent());
            } catch (InterruptedException e) {
               Throwables.propagate(e);
            } catch (ExecutionException e) {
               Throwables.propagate(e);
            }
         }

      });
      Results jclouds = getTest(count, "jclouds", new Supplier<ListenableFuture<?>>() {

         @Override
         public ListenableFuture<?> get() {
            return AsyncGaeHttpCommandExecutorServiceIntegrationTest.this.context.utils().asyncHttp().get(fetch);
         }

      }, new Consumer() {

         @SuppressWarnings("unchecked")
         @Override
         public void consume(Object in) {
            try {
               Strings2.toStringAndClose(((ListenableFuture<InputStream>) in).get());
            } catch (InterruptedException e) {
               Throwables.propagate(e);
            } catch (ExecutionException e) {
               Throwables.propagate(e);
            } catch (IOException e) {
               Throwables.propagate(e);
            }
         }

      });
      System.err.println(jclouds + " " + gae);
      long overhead = 10;
      assert jclouds.createFutures <= gae.createFutures + overhead : jclouds + " " + gae;
      assert jclouds.futuresReady <= gae.futuresReady + overhead : jclouds + " " + gae;
      assert jclouds.futuresConsumed <= gae.futuresConsumed + overhead : jclouds + " " + gae;
   }

   interface Consumer {
      void consume(Object in);
   }

   class Results {

      public long createFutures;
      public long futuresReady;
      public long futuresConsumed;
      public int count;
      public String who;

      @Override
      public String toString() {
         return "[count=" + count + ", createFutures=" + createFutures + ", futuresConsumed=" + futuresConsumed
               + ", futuresReady=" + futuresReady + ", who=" + who + "]";
      }

   }

   private Results getTest(int count, String who, Supplier<ListenableFuture<?>> getSupplier, Consumer consumer)
         throws TimeoutException {
      Results results = new Results();
      results.count = count;
      results.who = who;
      Map<String, ListenableFuture<?>> responses = Maps.newConcurrentMap();
      long start = System.currentTimeMillis();
      for (int i = 0; i < count; i++)
         responses.put(i + "", getSupplier.get());
      results.createFutures = System.currentTimeMillis() - start;
      start = System.currentTimeMillis();
      Map<String, Exception> exceptions = awaitCompletion(responses, MoreExecutors.sameThreadExecutor(), null, logger,
            who);
      results.futuresReady = System.currentTimeMillis() - start;
      assert exceptions.size() == 0 : exceptions;
      start = System.currentTimeMillis();
      for (ListenableFuture<?> value : responses.values())
         consumer.consume(value);
      results.futuresConsumed = System.currentTimeMillis() - start;
      return results;
   }

   @Override
   @Test(enabled = false)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      // streams aren't supported
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testPostBinder() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testPostBinder();
   }

   @BeforeMethod
   void setupApiProxy() {
      LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalURLFetchServiceTestConfig());
      helper.setUp();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetAndParseSax() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetAndParseSax();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetString() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testGetString();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000, dataProvider = "gets")
   public void testGetStringSynch(String path) throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringSynch(path);
   }

   // TODO: determine how to get redirects to operate
   @Override
   @Test(enabled = false)
   public void testGetStringRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringRedirect();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetException();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetStringPermanentRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringPermanentRedirect();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetSynchException() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetSynchException();
   }

   @Override
   @Test(enabled = false)
   public void testPost() {
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testPut() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testPut();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetStringViaRequest() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      setupApiProxy();
      super.testGetStringViaRequest();
   }

   // TODO: determine how to get redirects to operate
   @Override
   @Test(enabled = false)
   public void testPutRedirect() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testPutRedirect();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testGetStringWithHeader() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testGetStringWithHeader();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testHead() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
      setupApiProxy();
      super.testHead();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testRequestFilter() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testRequestFilter();
   }

   protected Module createConnectionModule() {
      setupApiProxy();
      return new AsyncGoogleAppEngineConfigurationModule();
   }

   @ConfiguresHttpCommandExecutorService
   @ConfiguresExecutorService
   @SingleThreaded
   public class AsyncGoogleAppEngineConfigurationModule extends GoogleAppEngineConfigurationModule {

      public AsyncGoogleAppEngineConfigurationModule() {
         super();
      }

      protected HttpCommandExecutorService providerHttpCommandExecutorService(Injector injector) {
         return injector.getInstance(AsyncGaeHttpCommandExecutorService.class);
      }

   }

   @Override
   protected void addConnectionProperties(Properties props) {
   }

   @Override
   @Test(enabled = true)
   public void testGetBigFile() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      // disabled since test data is too big
   }

   @Override
   @Test(enabled = true)
   public void testUploadBigFile() throws IOException {
      // disabled since test data is too big
   }

   @Override
   @Test(enabled = false)
   public void testPostContentDisposition() throws ExecutionException, InterruptedException, TimeoutException,
         IOException {
      setupApiProxy();
      super.testPostContentDisposition();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testPostContentEncoding() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      setupApiProxy();
      super.testPostContentEncoding();
   }

   @Override
   @Test(enabled = true, invocationCount = 5, timeOut = 3000)
   public void testPostContentLanguage() throws ExecutionException, InterruptedException, TimeoutException, IOException {
      setupApiProxy();
      super.testPostContentLanguage();
   }

   // http://code.google.com/p/googleappengine/issues/detail?id=3599
   @Override
   @Test(enabled = true, expectedExceptions = IllegalArgumentException.class)
   public void testAlternateMethod() throws MalformedURLException, ExecutionException, InterruptedException,
         TimeoutException {
      setupApiProxy();
      super.testAlternateMethod();
   }

}
