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

package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextSpec;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SDNAuthentication}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "sdn.SDNAuthenticationLiveTest")
public class SDNAuthenticationLiveTest {

   private RestContext<SDNAuthClient, SDNAuthAsyncClient> context;

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface SDNAuthClient {

      String authenticate(String appKey, String user, String password);
   }

   private String credential;
   private String identity;

   @Test
   public void testAuthentication() throws Exception {
      ArrayList<String> list = Lists.newArrayList(Splitter.on('/').split(credential));
      String response = context.getApi().authenticate(list.get(0), list.get(1), identity);
      assertNotNull(response);
   }

   @BeforeClass
   void setupFactory() {

      String endpoint = "http://services.nirvanix.com";
      identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      RestContextSpec<SDNAuthClient, SDNAuthAsyncClient> contextSpec = contextSpec("test", endpoint, "1", "", identity,
               credential, SDNAuthClient.class, SDNAuthAsyncClient.class);

      context = createContextBuilder(
               contextSpec,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new ExecutorServiceModule(MoreExecutors
                        .sameThreadExecutor(), MoreExecutors.sameThreadExecutor()))).buildContext();
   }
}
