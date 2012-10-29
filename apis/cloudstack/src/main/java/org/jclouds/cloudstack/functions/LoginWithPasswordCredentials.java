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
package org.jclouds.cloudstack.functions;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.domain.Credentials;

import com.google.common.base.Function;

@Singleton
public class LoginWithPasswordCredentials implements Function<Credentials, LoginResponse> {
   private final SessionClient client;

   @Inject
   public LoginWithPasswordCredentials(SessionClient client) {
      this.client = client;
   }

   @Override
   public LoginResponse apply(Credentials input) {
      String username = input.identity;
      String domain = "";  // empty = ROOT domain

      // domain may be present
      if (username.indexOf('/') != -1) {
         File domainUsername = new File(username);
         username = domainUsername.getName();
         domain = domainUsername.getParent();
      }

      String hashedPassword = CryptoStreams.md5Hex(input.credential);

      return client.loginUserInDomainWithHashOfPassword(username, domain, hashedPassword);
   }

   @Override
   public String toString() {
      return "loginWithPasswordCredentials()";
   }
}
