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
package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;
import org.jclouds.rest.config.RestModule;
import org.jclouds.vcloud.VCloudPropertiesBuilder;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.handlers.ParseVCloudErrorFromHttpResponse;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudRestClientModuleTest")
public class VCloudRestClientModuleTest {

   protected Injector createInjector() {
      return Guice.createInjector(new VCloudRestClientModule(), new RestModule(),
               new AbstractModule() {
                  @Override
                  protected void configure() {
                     Names.bindProperties(binder(), checkNotNull(new VCloudPropertiesBuilder(URI
                              .create("http://localhost"), "user", "pass").build(), "properties"));
                     bind(TransformingHttpCommandExecutorService.class).toInstance(
                              createMock(TransformingHttpCommandExecutorService.class));
                  }
               });
   }

   @Test
   void testUpdatesOnlyOncePerSecond() throws NoSuchMethodException, InterruptedException {
      VCloudRestClientModule module = new VCloudRestClientModule();
      VCloudLoginAsyncClient login = new VCloudLoginAsyncClient() {

         private final AtomicInteger token = new AtomicInteger();

         public ListenableFuture<VCloudSession> login() {
            return new ListenableFuture<VCloudSession>() {
               @Override
               public VCloudSession get() throws InterruptedException, ExecutionException {
                  return new VCloudSession() {

                     public Map<String, NamedResource> getOrgs() {
                        return null;
                     }

                     public String getVCloudToken() {
                        return token.incrementAndGet() + "";
                     }

                  };
               }

               @Override
               public boolean cancel(boolean mayInterruptIfRunning) {
                  return false;
               }

               @Override
               public VCloudSession get(long timeout, TimeUnit unit) throws InterruptedException,
                        ExecutionException, TimeoutException {
                  return get();
               }

               @Override
               public boolean isCancelled() {
                  return false;
               }

               @Override
               public boolean isDone() {
                  return false;
               }

               @Override
               public void addListener(Runnable listener, Executor exec) {

               }
            };

         }

      };
      Supplier<VCloudSession> map = module.provideVCloudTokenCache(1, login);
      for (int i = 0; i < 10; i++)
         map.get();
      assert "1".equals(map.get().getVCloudToken());
      Thread.sleep(1001);
      assert "2".equals(map.get().getVCloudToken());
   }

   @Test
   void testServerErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getServerErrorHandler().getClass(),
               ParseVCloudErrorFromHttpResponse.class);
   }

   @Test
   void testClientErrorHandler() {
      DelegatingErrorHandler handler = createInjector().getInstance(DelegatingErrorHandler.class);
      assertEquals(handler.getClientErrorHandler().getClass(),
               ParseVCloudErrorFromHttpResponse.class);
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