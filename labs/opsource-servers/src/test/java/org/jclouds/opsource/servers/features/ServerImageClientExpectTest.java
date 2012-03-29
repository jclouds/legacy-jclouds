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
package org.jclouds.opsource.servers.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.opsource.servers.OpSourceServersClient;
import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.internal.BaseOpSourceServersRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * Allows us to test the {@link ServerImageClient}
 * 
 * @author Kedar Dave
 */
@Test(groups = { "unit" }, singleThreaded = true, testName = "AccountClientExpectTest")
public class ServerImageClientExpectTest extends BaseOpSourceServersRestClientExpectTest {

   @Test
   public void testGetMyAccount() {
      OpSourceServersClient client = requestSendsResponse(
            HttpRequest
                  .builder()
                  .method("GET")
                  .endpoint(URI.create("https://api.opsourcecloud.net/oec/0.9/myaccount"))
                  .headers(
                        ImmutableMultimap.<String, String> builder().put("Accept", "*/*")
                              .put("Authorization", "Basic dXNlcjpwYXNzd29yZA==").build()).build(),

            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/myaccount.xml")).build());

      Account expected = Account.builder().orgId("8a8f6abc-2745-4d8a-9cbc-8dabe5a7d0e4").build();

      assertEquals(client.getAccountClient().getMyAccount(), expected);
   }

}
