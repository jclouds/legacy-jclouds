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
package org.jclouds.openstack.keystone.v2_0;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiMetadata;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;

/**
 * Provides synchronous access to the KeyStone Admin API.
 * <p/>
 *
 * @author Adam Lowe
 * @see UserAsyncClient
 * @see <a href=
 *       "http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Client_Operations.html"
 *      />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface AdminClient {

   /**
    * Discover API version information, links to documentation (PDF, HTML, WADL), and supported media types
    *
    * @return the requested information
    */
   ApiMetadata getApiMetadata();

   /**
    * The operation returns a list of tenants which the current token provides access to.
    */
   Set<Tenant> listTenants();

   /**
    * Retrieve information about a tenant, by tenant ID
    *
    * @return the information about the tenant
    */
   Tenant getTenant(String tenantId);

   /**
    * Retrieve information about a tenant, by tenant name
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/956687 )
    *
    * @return the information about the tenant
    */
   Tenant getTenantByName(String tenantName);

   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   Token getToken(String token);
   
   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   User getUserOfToken(String token);
   
   /**
    * Validate a token. This is a high-performance variant of the #getToken() call that does not return any further
    * information.
    *
    * @return true if the token is valid
    */
   Boolean checkTokenIsValid(String token);

   /**
    * List all endpoints for a token
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/988672 )
    *
    * @return the set of endpoints
    */
   Set<Endpoint> getEndpointsForToken(String token);

   /**
    * Retrieve the list of users
    * <p/>
    * NOTE: this method is not in API documentation for keystone, but does work
    *
    * @return the list of users
    */
   Set<User> listUsers();

   /**
    * Retrieve information about a user, by user ID
    *
    * @return the information about the user
    */
   User getUser(String userId);

   /**
    * Retrieve information about a user, by user name
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/956687 )
    *
    * @return the information about the user
    */
   User getUserByName(String userName);

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