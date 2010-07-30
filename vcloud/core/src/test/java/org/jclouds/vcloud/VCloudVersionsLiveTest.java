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
import static org.jclouds.concurrent.ConcurrentUtils.sameThreadExecutor;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VCloudVersions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudVersionsLiveTest")
public class VCloudVersionsLiveTest {

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface VCloudVersionsClient {
      SortedMap<String, URI> getSupportedVersions();
   }

   private RestContext<VCloudVersionsClient, VCloudVersionsAsyncClient> context;

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
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      ContextSpec<VCloudVersionsClient, VCloudVersionsAsyncClient> contextSpec = contextSpec(
               "test", endpoint, "1", identity, credential, VCloudVersionsClient.class,
               VCloudVersionsAsyncClient.class);

      context = createContextBuilder(
               contextSpec,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new ExecutorServiceModule(
                        sameThreadExecutor(), sameThreadExecutor()))).buildContext();
   }
}
