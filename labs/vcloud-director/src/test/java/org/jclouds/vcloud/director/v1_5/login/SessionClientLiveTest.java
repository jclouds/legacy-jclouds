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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.rest.AnonymousRestApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.director.testng.FormatApiResultsListener;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * Tests behavior of {@link SessionClient}. Note this class is tested completely independently of
 * {@link VCloudDirectorClient} as it is a dependency of the {@code vcloud-director} context working.
 * 
 * @author Adrian Cole
 */
@Listeners(FormatApiResultsListener.class)
@Test(groups = { "live", "user" }, testName = "SessionClientLiveTest")
public class SessionClientLiveTest extends BaseContextLiveTest<RestContext<SessionClient, SessionAsyncClient>> {

   public SessionClientLiveTest() {
      provider = "vcloud-director";
   }

   @Override
   @BeforeGroups(groups = { "live" })
   public void setupContext() {
      super.setupContext();
      // session client isn't typically exposed to the user, as it is implicit
      client = context.getApi();
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

   @Test(description = "GET /session", dependsOnMethods = "testLogin")
   public void testGetSession() {
      assertEquals(client.getSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken()),
               sessionWithToken.getSession());
   }

   @Test(description = "DELETE /session", dependsOnMethods = "testGetSession")
   public void testLogout() {
      client.logoutSessionWithToken(sessionWithToken.getSession().getHref(), sessionWithToken.getToken());
   }
   
   @Override
   protected ApiMetadata createApiMetadata() {
      return AnonymousRestApiMetadata.forClientMappedToAsyncClient(SessionClient.class, SessionAsyncClient.class);
   }

   @Override
   protected TypeToken<RestContext<SessionClient, SessionAsyncClient>> contextType() {
      return new TypeToken<RestContext<SessionClient, SessionAsyncClient>>(){

         /** The serialVersionUID */
         private static final long serialVersionUID = -3625362618882122604L;};
   }
}
