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

import org.jclouds.cloudstack.domain.ApiKeyPair;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.options.CreateUserOptions;
import org.jclouds.cloudstack.options.UpdateUserOptions;

/**
 * Provides synchronous access to CloudStack User features available to Global
 * Admin users.
 * 
 * @author Andrei savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface GlobalUserClient extends DomainUserClient {


   /**
    * Create an user for an account that already exists
    *
    * @param userName unique user name
    * @param accountName Creates the user under the specified account. If no
    *    account is specified, the username will be used as the account name.
    * @param email
    * @param hashedPassword Hashed password (Default is MD5). If you wish to use
    *    any other hashing algorithm, you would need to write a custom authentication
    *    adapter See Docs section.
    * @param firstName
    * @param lastName
    * @param options optional arguments
    * @return
    */
   User createUser(String userName, String accountName, String email, String hashedPassword,
      String firstName, String lastName, CreateUserOptions... options);


   /**
    * This command allows a user to register for the developer API, returning a
    * secret key and an API key
    *
    * @param userId the ID of the user
    * @return
    */
   ApiKeyPair registerUserKeys(String userId);

   /**
    * Update an user
    *
    * @param id the user ID
    * @param options optional arguments
    * @return
    */
   User updateUser(String id, UpdateUserOptions... options);

   /**
    * Delete an user with the specified ID
    *
    * @param id  user ID
    */
   Void deleteUser(String id);
}
