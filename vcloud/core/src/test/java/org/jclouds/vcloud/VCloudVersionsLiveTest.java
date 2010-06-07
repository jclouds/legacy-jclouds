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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.endpoints.VCloud;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code VCloudVersions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudVersionsLiveTest")
public class VCloudVersionsLiveTest {

   @RequiresHttp
   @ConfiguresRestClient
   private static final class VCloudVersionsRestClientModule extends AbstractModule {
      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected VCloudVersionsAsyncClient provideVCloudVersions(AsyncClientFactory factory) {
         return factory.create(VCloudVersionsAsyncClient.class);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      @VCloud
      URI provideUri(@Named(PROPERTY_VCLOUD_ENDPOINT) String endpoint) {
         return URI.create(endpoint);
      }

      @Override
      protected void configure() {
      }
   }

   private final class VCloudVersionsContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudVersionsAsyncClient, VCloudVersionsAsyncClient> provideContext(
               Closer closer, VCloudVersionsAsyncClient api, @VCloud URI endPoint) {
         return new RestContextImpl<VCloudVersionsAsyncClient, VCloudVersionsAsyncClient>(closer,
                  api, api, endPoint, "");
      }

      @Override
      protected void configure() {

      }
   }

   private RestContext<VCloudVersionsAsyncClient, VCloudVersionsAsyncClient> context;

   @Test
   public void testGetSupportedVersions() throws Exception {
      VCloudVersionsAsyncClient authentication = context.getAsyncApi();
      for (int i = 0; i < 5; i++) {
         SortedMap<String, URI> response = authentication.getSupportedVersions().get(45,
                  TimeUnit.SECONDS);
         assertNotNull(response);
         assertNotNull(response.containsKey("0.8"));
      }
   }

   @BeforeClass
   void setupFactory() {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint");
      String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      context = new RestContextBuilder<VCloudVersionsAsyncClient, VCloudVersionsAsyncClient>(
               "vcloud", VCloudVersionsAsyncClient.class, VCloudVersionsAsyncClient.class,
               new VCloudPropertiesBuilder(URI.create(endpoint), account, key).build()) {

         public void addContextModule(String providerName, List<Module> modules) {

            modules.add(new VCloudVersionsContextModule());
         }

         @Override
         protected void addClientModule(List<Module> modules) {
            modules.add(new VCloudVersionsRestClientModule());
         }

      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()))
               .buildContext();
   }
}
