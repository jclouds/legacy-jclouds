/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.enterprise.UserDto;

/**
 * Provides synchronous access to Abiquo Admin API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see AdminAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public interface AdminApi {
   /* ********************** User ********************** */

   /**
    * Get the information of the current user.
    * 
    * @return The information of the current user.
    */
   UserDto getCurrentUser();

   /* ********************** Role ********************** */

   /**
    * List global roles.
    * 
    * @return The list of global Roles.
    */
   RolesDto listRoles();

   /**
    * List enterprise roles.
    * 
    * @return The list of Roles for the given enterprise.
    */
   RolesDto listRoles(EnterpriseDto enterprise);

   /**
    * Retrieves the role of the given user.
    * 
    * @param user
    *           The user.
    * @return The role of the user.
    */
   RoleDto getRole(UserDto user);

   /**
    * Get the given role.
    * 
    * @param roleId
    *           The id of the role.
    * @return The role or <code>null</code> if it does not exist.
    */
   RoleDto getRole(Integer roleId);

   /**
    * Deletes an existing role.
    * 
    * @param role
    *           The role to delete.
    */
   void deleteRole(final RoleDto role);

   /**
    * Updates an existing role.
    * 
    * @param role
    *           The new attributes for the role.
    * @return The updated role.
    */
   RoleDto updateRole(RoleDto role);

   /**
    * Create a new role.
    * 
    * @param role
    *           The role to be created.
    * @return The created role.
    */
   RoleDto createRole(RoleDto role);

   /**
    * Get privileges of the given role.
    * 
    * @param role
    *           The role.
    * @return The list of privileges.
    */
   PrivilegesDto listPrivileges(RoleDto role);
}
