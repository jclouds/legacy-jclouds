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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionClient;
import org.jclouds.domain.Credentials;

import com.google.common.cache.CacheLoader;

@Singleton
public class LoginWithPasswordCredentials extends CacheLoader<Credentials, LoginResponse> {
   private final SessionClient client;

   @Inject
   public LoginWithPasswordCredentials(SessionClient client) {
      this.client = client;
   }

   @Override
   public LoginResponse load(Credentials input) {
      String username = input.identity;
      String domain = "";  // empty = ROOT domain

      // domain may be present
      if (username.indexOf('/') != -1) {
         // username may not end with slash!
         domain = username.substring(0,username.lastIndexOf('/'));
         username = username.substring(username.lastIndexOf('/') + 1, username.length());
      }
      String hashedPassword = base16().lowerCase().encode(md5().hashString(input.credential, UTF_8).asBytes());
      return client.loginUserInDomainWithHashOfPassword(username, domain, hashedPassword);
   }

   @Override
   public String toString() {
      return "loginWithPasswordCredentials()";
   }
}
