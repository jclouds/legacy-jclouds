/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.features;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
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

   HttpRequest login = HttpRequest.builder().method("GET")
                                  .endpoint("http://localhost:8080/client/api")
                                  .addQueryParam("response", "json")
                                  .addQueryParam("command", "login")
                                  .addQueryParam("username", "jcloud")
                                  .addQueryParam("domain", "Partners/jCloud")
                                  .addQueryParam("password", "30e14b3727225d833aad2206acea1275")
                                  .addHeader("Accept", "application/json").build();

   public void testLoginWhenResponseIs2xxIncludesJSessionId() throws IOException {
      String domain = "Partners/jCloud";
      String user = "jcloud";
      String password = "jcl0ud";
      String md5password = base16().lowerCase().encode(md5().hashString(password, UTF_8).asBytes());

      String jSessionId = "90DD65D13AEAA590ECCA312D150B9F6D";
      SessionClient client = requestSendsResponse(login,
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
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getSessionClient();
   }
}
