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
package org.jclouds.openstack.keystone.v2_0.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.User;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to the KeyStone User API.
 * <p/>
 * 
 * @author Adam Lowe
 * @see UserAsyncClient
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/User_Operations.html"
 *      />
 */
@Beta
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface UserClient {

   /**
    * Retrieve the list of users
    * <p/>
    * NOTE: this method is not in API documentation for keystone, but does work
    * 
    * @return the list of users
    */
   Set<User> list();

   /**
    * Retrieve information about a user, by user ID
    * 
    * @return the information about the user
    */
   User get(String userId);

   /**
    * Retrieve information about a user, by user name
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/956687 )
    * 
    * @return the information about the user
    */
   User getByName(String userName);

   /**
    * Retrieves the list of global roles associated with a specific user (excludes tenant roles).
    * <p/>
    * NOTE: Broken in openstack ( https://bugs.launchpad.net/keystone/+bug/933565 )
    * 
    * @return the set of Roles granted to the user
    */
   Set<Role> listRolesOfUser(String userId);

   /**
    * List the roles a user has been granted on a specific tenant
    * 
    * @return the set of roles
    */
   Set<Role> listRolesOfUserOnTenant(String userId, String tenantId);

}