/*
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
package org.jclouds.vcloud.director.v1_5;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.HttpClient;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.director.v1_5.domain.SessionWithToken;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgList;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.jclouds.xml.internal.JAXBParser;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of operations that use {@link HttpClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "HttpClientLiveTest")
public class HttpClientLiveTest extends BaseVCloudDirectorApiLiveTest {

   private JAXBParser parser = new JAXBParser("true");
   private SessionWithToken sessionWithToken;

   @Override
   protected void setupRequiredApis() {
   }

   @Test(description = "POST /login")
   public void testPostLogin() throws IOException {
      testLoginWithMethod("POST");
   }

   @Test(description = "GET /login")
   public void testGetLogin() throws IOException {
      testLoginWithMethod("GET");
   }

   private void testLoginWithMethod(final String method) throws IOException {
      String user = identity.substring(0, identity.lastIndexOf('@'));
      String org = identity.substring(identity.lastIndexOf('@') + 1);
      String password = credential;

      String authHeader = "Basic " + CryptoStreams.base64(String.format("%s@%s:%s", checkNotNull(user), checkNotNull(org), checkNotNull(password)).getBytes("UTF-8"));

      HttpResponse response = context.getUtils().getHttpClient().invoke(HttpRequest.builder()
            .method(method)
            .endpoint(endpoint + "/login")
            .addHeader("Authorization", authHeader)
            .addHeader("Accept", "*/*").build());

      sessionWithToken = SessionWithToken.builder().session(session).token(response.getFirstHeaderOrNull("x-vcloud-authorization")).build();

      assertEquals(sessionWithToken.getSession().getUser(), user);
      assertEquals(sessionWithToken.getSession().getOrg(), org);
      assertTrue(sessionWithToken.getSession().getLinks().size() > 0);
      assertNotNull(sessionWithToken.getToken());

      OrgList orgList = parser.fromXML(Strings2.toString(response.getPayload()), OrgList.class);

      assertTrue(orgList.size() > 0, "must have orgs");

      context.getApi().getOrgApi().getOrg(Iterables.getLast(orgList).getHref());
   }

   @Test(description = "GET /schema/{schemaFileName}", dependsOnMethods = { "testPostLogin", "testGetLogin" })
   public void testGetSchema() throws IOException {
      String schemafileName = "master.xsd";
      HttpResponse response = context.getUtils().getHttpClient().invoke(HttpRequest.builder()
            .method("GET")
            .endpoint(endpoint + "/v1.5/schema/" + schemafileName)
            .addHeader("x-vcloud-authorization", sessionWithToken.getToken())
            .addHeader("Accept", "*/*").build());

      String schema = Strings2.toString(response.getPayload());

      // TODO: asserting something about the schema
   }

}
