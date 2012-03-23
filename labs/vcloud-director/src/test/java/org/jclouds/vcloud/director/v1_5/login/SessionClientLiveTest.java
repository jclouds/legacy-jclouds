/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.login;

import static org.jclouds.rest.RestContextFactory.contextSpec;
import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Properties;

import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.vcloud.director.testng.FormatApiResultsListener;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SessionClient}. Note this class is tested completely independently of
 * VCloudClient as it is a dependency of the VCloud context working.
 * 
 * @author Adrian Cole
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = { "live", "user", "login" }, testName = "SessionClientLiveTest")
public class SessionClientLiveTest extends BaseVersionedServiceLiveTest {
   public SessionClientLiveTest() {
      provider = "vcloud-director";
   }

   private RestContext<SessionClient, SessionAsyncClient> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      RestContextSpec<SessionClient, SessionAsyncClient> contextSpec = contextSpec("vcloud-director", endpoint,
               apiVersion, buildVersion, "", identity, credential, SessionClient.class, SessionAsyncClient.class);

      context = createContextBuilder(contextSpec, overrides).withModules(
               ImmutableSet.<Module> of(new Log4JLoggingModule())).buildContext();

      // session client isn't typically exposed to the user, as it is implicit
      client = context.utils().injector().getInstance(SessionClient.class);
   }

   private SessionClient client;
   private SessionWithToken sessionWithToken;

   @Test(testName = "POST /sessions")
   public void testLogin() {
      String user = identity.substring(0, identity.lastIndexOf('@'));
      String org = identity.substring(identity.lastIndexOf('@') + 1);
      String password = credential;

      sessionWithToken = client.loginUserInOrgWithPassword(URI.create(endpoint + "/sessions"), user, org, password);
      assertEquals(sessionWithToken.getSession().getUser(), user);
      assertEquals(sessionWithToken.getSession().getOrg(), org);
      assertTrue(sessionWithToken.getSession().getLinks().size() > 0);
      assertNotNull(sessionWithToken.getToken());
   }

   @Test(testName = "GET /session", dependsOnMethods = "testLogin")
   public void testGetSession() {
      assertEquals(client.getSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken()),
               sessionWithToken.getSession());
   }

   @Test(testName = "DELETE /session", dependsOnMethods = "testGetSession")
   public void testLogout() {
      client.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
   }

   @AfterGroups(groups = "live")
   protected void tearDown() {
      if (context != null)
         context.close();
   }

}
