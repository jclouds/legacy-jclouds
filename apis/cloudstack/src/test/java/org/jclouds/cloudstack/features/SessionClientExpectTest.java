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
package org.jclouds.cloudstack.features;

import static org.jclouds.crypto.CryptoStreams.md5Hex;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.cloudstack.util.ApiKeyPairs;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code SessionClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "SessionClientExpectTest")
public class SessionClientExpectTest extends BaseCloudStackExpectTest<SessionClient> {
	private String loginEndpoint = "http://localhost:8080/client";
	private String restEndpoint = loginEndpoint + "/api";
    private String domain = "Partners/jCloud";
    private String user = "jcloud";
    private String password = "jcl0ud";

   public void testLoginWhenResponseIs2xxIncludesJSessionId() throws IOException {
      String md5password = md5Hex(password);

      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(
            URI.create(restEndpoint + "?response=json&command=login&" +
               "username=" + user + "&password=" + md5password + "&domain=" + domain))
         .addHeader("Accept", "application/json")
         .build();

      String jSessionId = "90DD65D13AEAA590ECCA312D150B9F6D";
      SessionClient client = requestSendsResponse(request,
         HttpResponse.builder()
            .statusCode(200)
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Set-Cookie", "JSESSIONID=" + jSessionId + "; Path=/client")
                  .build())
            .payload(payloadFromResource("/loginresponse.json"))
            .build());

      assertEquals(client.loginUserInDomainWithHashOfPassword(user, domain, md5password).toString(),
         LoginResponse.builder().timeout(1800).lastName("Kiran").registered(false).username("jcloud").firstName("Vijay")
            .domainId("11").accountType(Account.Type.DOMAIN_ADMIN).userId("19").sessionKey(
            "uYT4/MNiglgAKiZRQkvV8QP8gn0=").jSessionId(jSessionId).accountName("jcloud").build().toString());
   }

	public void getKeyPair()
	{
		URI url = URI.create(restEndpoint);
//		File f = new File(domain);
		ApiKeyPair pair = ApiKeyPairs.loginToEndpointAsUsernameInDomainWithPasswordAndReturnApiKeyPair(url,user,password,domain);
//		assertEquals("not the right key", pair.getApiKey(), key);
//		assertEquals("not the right secret", pair.getSecretKey(),secret);
	}

   public void testLogout() throws IOException {
      HttpRequest request = HttpRequest.builder()
         .method("GET")
         .endpoint(
            URI.create("http://localhost:8080/client/api?response=json&command=logout&sessionkey=dummy-session-key"))
         .build();

      SessionClient client = requestSendsResponse(request,
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/logoutresponse.json"))
            .build());

      client.logoutUser("dummy-session-key");
   }

   @Override
   protected SessionClient clientFrom(CloudStackContext context) {
      return context.getProviderSpecificContext().getApi().getSessionClient();
   }
}
