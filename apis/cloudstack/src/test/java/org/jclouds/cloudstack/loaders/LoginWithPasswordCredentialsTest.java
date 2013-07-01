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
package org.jclouds.cloudstack.loaders;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionApi;
import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

public class LoginWithPasswordCredentialsTest {

   /**
    * This test is different from the single domainname test as it included testing on how the 
    * domainname is rebuild. It is here to prove that this particular test fails on systems
    * with a different path separator.
    */
   @Test
   public void testWithDoubleDomainname() {
      LoginResponse response = createMock(LoginResponse.class);
      SessionApi client = createMock(SessionApi.class);

      expect(client.loginUserInDomainWithHashOfPassword(eq("User"), eq("Test/Domain"), (String) anyObject())).andReturn(response);
      replay(client);

      LoginWithPasswordCredentials obj = new LoginWithPasswordCredentials(client);
      Credentials cred = new Credentials("Test/Domain/User", "koffiedik");

      obj.load(cred);
   }

   @Test
   public void testWithSingleDomainname() {
      LoginResponse response = createMock(LoginResponse.class);
      SessionApi client = createMock(SessionApi.class);

      expect(client.loginUserInDomainWithHashOfPassword(eq("User"), eq("Domain"), (String) anyObject())).andReturn(response);
      replay(client);

      LoginWithPasswordCredentials obj = new LoginWithPasswordCredentials(client);
      Credentials cred = new Credentials("Domain/User", "koffiedik");

      obj.load(cred);
   }

   @Test
   public void testWithNoDomainname() {
       LoginResponse response = createMock(LoginResponse.class);
       SessionApi client = createMock(SessionApi.class);

       expect(client.loginUserInDomainWithHashOfPassword(eq("User"), eq(""), (String) anyObject())).andReturn(response);
       replay(client);

       LoginWithPasswordCredentials obj = new LoginWithPasswordCredentials(client);
       Credentials cred = new Credentials("User", "koffiedik");

       obj.load(cred);
   }
}
