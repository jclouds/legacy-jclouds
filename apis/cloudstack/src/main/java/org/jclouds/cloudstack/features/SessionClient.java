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

import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.concurrent.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to CloudStack Sessions
 * <p/>
 * 
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Andrei Savu
 */
@Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
public interface SessionClient {

   /**
    * Logs a user into Cloudstack.  A successful login attempt will generate a JSESSIONID
    * cookie value that can be passed in subsequent Query command calls until the "logout"
    * command has been issued or the session has expired.
    *
    * @param userName
    *          user account name
    * @param hashedPassword
    *          hashed password (by default MD5)
    * @param domainOrEmpty
    *          domain name, if empty defaults to ROOT
    * @return
    *          login response with session key or null
    */
   LoginResponse loginWithHashedPassword(String userName, String hashedPassword, String domainOrEmpty);

   /**
    * Logs a user into Cloudstack.  A successful login attempt will generate a session key
    * cookie value that can be passed in subsequent Query command calls until the "logout"
    * command has been issued or the session has expired.
    *
    * @param userName
    *          user account name
    * @param plainTextPassword
    *          plain text password
    * @param domainOrEmpty
    *          domain name, if empty defaults to ROOT
    * @return
    *          login response with session key or null
    */
   LoginResponse loginWithPlainTextPassword(String userName, String plainTextPassword, String domainOrEmpty);

   /**
    * Retrieve an account by name using the session key for login
    *
    * @param accountName
    *          account name
    * @param sessionKey
    *          a valid session key
    * @return
    *          account instance or null
    */
   Account getAccountByName(String accountName, String sessionKey);

   /**
    * Logs out the user by invalidating the session key
    *
    * @param sessionKey
    *          user session key
    */
   Void logout(String sessionKey);

}
