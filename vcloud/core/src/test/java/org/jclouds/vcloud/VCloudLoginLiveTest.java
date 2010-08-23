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

package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.endpoints.VCloudLogin;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

import domain.VCloudExpressLoginAsyncClient;

/**
 * Tests behavior of {@code VCloudLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudLoginLiveTest")
public class VCloudLoginLiveTest {

   private RestContext<VCloudLoginClient, VCloudExpressLoginAsyncClient> context;

   @Test
   public void testLogin() throws Exception {
      VCloudExpressLoginAsyncClient authentication = context.getAsyncApi();
      for (int i = 0; i < 5; i++) {
         VCloudSession response = authentication.login().get(45, TimeUnit.SECONDS);
         assertNotNull(response);
         assertNotNull(response.getVCloudToken());
         assertNotNull(response.getOrgs());
      }
   }

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface VCloudLoginClient {

      VCloudSession login();
   }

   @BeforeClass
   void setupFactory() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"), "jclouds.test.endpoint")
               + "/v0.8/login";

      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      ContextSpec<VCloudLoginClient, VCloudExpressLoginAsyncClient> contextSpec = contextSpec("test", endpoint, "1", identity,
               credential, VCloudLoginClient.class, VCloudExpressLoginAsyncClient.class);

      context = createContextBuilder(
               contextSpec,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new ExecutorServiceModule(MoreExecutors
                        .sameThreadExecutor(), MoreExecutors.sameThreadExecutor()), new AbstractModule() {

                  @Override
                  protected void configure() {

                  }

                  @SuppressWarnings("unused")
                  @Provides
                  @VCloudLogin
                  URI provideURI(@Provider URI uri) {
                     return uri;
                  }

               })).buildContext();
   }
}
