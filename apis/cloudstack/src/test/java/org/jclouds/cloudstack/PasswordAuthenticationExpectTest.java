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
package org.jclouds.cloudstack;

import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.cloudstack.config.CloudStackProperties;
import org.jclouds.cloudstack.features.AccountClient;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * 
 * @see CloudStackProperties#CREDENTIAL_TYPE
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PasswordAuthenticationExpectTest")
public class PasswordAuthenticationExpectTest extends BaseCloudStackExpectTest<AccountClient> {

   /**
    * this reflects the properties that a user would pass to createContext
    */
   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty("jclouds.cloudstack.credential-type", "passwordCredentials");
      return contextProperties;
   }

   public void testLoginWithPasswordSetsSessionKeyAndCookie() {
      
      AccountClient client = requestsSendResponses(
               login, loginResponse, 
         HttpRequest.builder()
            .method("GET")
            .endpoint("http://localhost:8080/client/api?response=json&command=listAccounts&listAll=true&sessionkey=" + Strings2.urlEncode(sessionKey, '/'))
            .addHeader("Accept", "application/json")
            .addHeader(HttpHeaders.COOKIE, "JSESSIONID=" + jSessionId)
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listaccountsresponse.json"))
            .build()
            ,logout, logoutResponse);
      
      assertNotNull(client.listAccounts());
   }

   @Override
   protected AccountClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getAccountClient();
   }
}
