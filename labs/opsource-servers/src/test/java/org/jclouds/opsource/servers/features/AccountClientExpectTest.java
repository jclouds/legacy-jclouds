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

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.opsource.servers.OpSourceServersClient;
import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.internal.BaseOpSourceServersRestClientExpectTest;
import org.testng.annotations.Test;

/**
 * Allows us to test the {@link AccountClient} via its side effects.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, singleThreaded = true, testName = "AccountClientExpectTest")
public class AccountClientExpectTest extends BaseOpSourceServersRestClientExpectTest {

   @Test
   public void testGetMyAccount() {
      OpSourceServersClient client = requestSendsResponse(
            HttpRequest.builder().method("GET")
                       .endpoint("https://api.opsourcecloud.net/oec/0.9/myaccount")
                       .addHeader("Accept", "*/*")
                       .addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==").build(),

            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/myaccount.xml")).build());

      Account expected = Account.builder().orgId("8a8f6abc-2745-4d8a-9cbc-8dabe5a7d0e4").build();

      assertEquals(client.getAccountClient().getMyAccount(), expected);
   }

}
