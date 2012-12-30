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
import org.jclouds.cloudstack.options.CreateAccountOptions;
import org.jclouds.cloudstack.options.UpdateAccountOptions;

/**
 * Provides synchronous access to CloudStack Account features available to Global
 * Admin users.
 * 
 * @author Adrian Cole, Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface GlobalAccountClient extends DomainAccountClient {

   /**
    * Create a new Cloudstack account
    *
    * @param userName unique username.
    * @param accountType type of account
    * @param email
    * @param firstName
    * @param lastName
    * @param hashedPassword
    *          Hashed password (Default is MD5). If you wish to use any other
    *          hashing algorithm, you would need to write a custom authentication adapter See Docs section.
    * @param options
    *          optional parameters
    * @return
    */
   Account createAccount(String userName, Account.Type accountType, String email,
      String firstName, String lastName, String hashedPassword, CreateAccountOptions... options);

   /**
    * Update an existing account
    *
    * @param accountName the current account name
    * @param domainId the ID of the domain were the account exists
    * @param newName new name for the account
    * @param options optional arguments
    * @return
    */
   Account updateAccount(String accountName, String domainId, String newName, UpdateAccountOptions... options);

   /**
    * Delete an account with the specified ID
    *
    * @param accountId
    * @return
    */
   Void deleteAccount(String accountId);

}
