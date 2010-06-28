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
package org.jclouds.rackspace;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.RackspaceAuthAsyncClient.AuthenticationResponse;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "rackspace.RackspaceAuthenticationLiveTest")
public class RackspaceAuthenticationLiveTest {

   @Timeout(duration = 10, timeUnit = TimeUnit.SECONDS)
   public interface RackspaceAuthClient {

      AuthenticationResponse authenticate(String user, String key);
   }

   private RestContext<RackspaceAuthClient, RackspaceAuthAsyncClient> context;
   private String identity;
   private String credential;

   @Test
   public void testAuthentication() throws Exception {
      RackspaceAuthClient authentication = context.getApi();
      AuthenticationResponse response = authentication.authenticate(identity, credential);
      assertNotNull(response);
      assertNotNull(response.getStorageUrl());
      assertNotNull(response.getCDNManagementUrl());
      assertNotNull(response.getServerManagementUrl());
      assertNotNull(response.getAuthToken());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testBadAuthentication() throws Exception {
      RackspaceAuthAsyncClient authentication = context.getAsyncApi();
      authentication.authenticate("foo", "bar").get(10, TimeUnit.SECONDS);
   }

   @BeforeClass
   void setupFactory() {

      identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      ContextSpec<RackspaceAuthClient, RackspaceAuthAsyncClient> contextSpec = contextSpec("test",
               "https://api.mosso.com", "1", null, null, RackspaceAuthClient.class,
               RackspaceAuthAsyncClient.class);

      context = createContextBuilder(
               contextSpec,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new ExecutorServiceModule(
                        sameThreadExecutor(), sameThreadExecutor()))).buildContext();

   }
}
