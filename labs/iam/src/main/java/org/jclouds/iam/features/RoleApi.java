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
package org.jclouds.iam.features;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.domain.Role;
import org.jclouds.javax.annotation.Nullable;

/**
 * @see RoleAsyncApi
 * @author Adrian Cole
 */
public interface RoleApi {

   /**
    * Creates a new role for your AWS account
    * 
    * @param name
    *           Name of the role to create.
    * @param assumeRolePolicy
    *           The policy that grants an entity permission to assume the role.}
    * @return the new role
    */
   Role createWithPolicy(String name, String assumeRolePolicy);

   /**
    * like {@link #createWithPolicy(String, String)}, except you can specify a path.
    */
   Role createWithPolicyAndPath(String name, String assumeRolePolicy, String path);

   /**
    * returns all roles in order.
    */
   PagedIterable<Role> list();

   /**
    * retrieves up to 100 roles in order.
    */
   IterableWithMarker<Role> listFirstPage();

   /**
    * retrieves up to 100 roles in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<Role> listAt(String marker);

   /**
    * returns all roles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   PagedIterable<Role> listPathPrefix(String pathPrefix);

   /**
    * retrieves up to 100 roles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   IterableWithMarker<Role> listPathPrefixFirstPage(String pathPrefix);

   /**
    * retrieves up to 100 roles in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<Role> listPathPrefixAt(String pathPrefix, String marker);

   /**
    * Retrieves information about the specified role, including the role's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the role to get information about.
    * @return null if not found
    */
   @Nullable
   Role get(String name);

   /**
    * returns all instance profiles in order for this role.
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    */
   PagedIterable<InstanceProfile> listInstanceProfiles(String name);

   /**
    * retrieves up to 100 instance profiles in order for this role.
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    */
   IterableWithMarker<InstanceProfile> listFirstPageOfInstanceProfiles(String name);

   /**
    * retrieves up to 100 instance profiles in order for this role, starting at {@code marker}
    * 
    * @param name
    *           Name of the role to get instance profiles for.
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<InstanceProfile> listInstanceProfilesAt(String name, String marker);

   /**
    * Deletes the specified role. The role must not have any policies attached. 
    * 
    * @param name
    *           Name of the role to delete
    */
   void delete(String name);
}
