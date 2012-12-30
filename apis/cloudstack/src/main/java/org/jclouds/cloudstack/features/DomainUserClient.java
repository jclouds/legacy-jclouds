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

import java.util.Set;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.options.ListUsersOptions;

/**
 * Provides synchronous access to CloudStack User features available to Domain
 * Admin users.
 * 
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
public interface DomainUserClient {

   /**
    * Lists Users
    *
    * @param options
    *           if present, how to constrain the list.
    * @return Users matching query, or empty set, if no Accounts are found
    */
   Set<User> listUsers(ListUsersOptions... options);

   /**
    * Enable a user with a specific ID
    *
    * @param userId
    *    the user ID to enable
    */
   User enableUser(String userId);

   /**
    * Disable a user with a specific ID
    */
   AsyncCreateResponse disableUser(String userId);

}
