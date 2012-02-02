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
package org.jclouds.cloudstack;

import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.features.BaseCloudStackRestClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;

/**
 * 
 * @see CloudStackProperties#CREDENTIAL_TYPE
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PasswordAuthenticationExpectTest")
public class PasswordAuthenticationExpectTest extends BaseCloudStackRestClientExpectTest<CloudStackContext> {

   /**
    * this reflects the properties that a user would pass to createContext
    */
   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty("jclouds.cloudstack.credential-type", "passwordCredentials");
      return contextProperties;
   }


   @SuppressWarnings("deprecation")
   public void testLoginWithPasswordSetsSessionKeyAndCookie() {
      
      CloudStackContext context = requestsSendResponses(
               loginRequest, loginResponse, 
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=listAccounts&sessionkey=" + URLEncoder.encode(sessionKey)))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .put(HttpHeaders.COOKIE, "JSESSIONID=" + jSessionId)
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listaccountsresponse.json"))
            .build()
            , logoutRequest, logoutResponse);

      AccountClient client = context.getProviderSpecificContext().getApi().getAccountClient();
      
      assertNotNull(client.listAccounts());

      context.close();
   }

   @Override
   protected CloudStackContext clientFrom(CloudStackContext context) {
      return context;
   }
}
