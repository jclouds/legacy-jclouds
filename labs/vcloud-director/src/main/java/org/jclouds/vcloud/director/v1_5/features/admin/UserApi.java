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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;
import org.jclouds.vcloud.director.v1_5.domain.User;

/**
 * Provides synchronous access to {@link User} objects.
 * 
 * @see UserAsyncApi
 * @author danikov, Adrian Cole
 */
public interface UserApi {

   /**
    * Creates or imports a user in an organization. The user could be enabled or disabled.
    * 
    * <pre>
    * POST /admin/org/{id}/users
    * </pre>
    * 
    * @param orgUrn
    *           the urn for the org
    * @return the addd user
    */
   User addUserToOrg(User user, String orgUrn);

   User addUserToOrg(User user, URI orgAdminHref);

   /**
    * Retrieves a user. This entity could be enabled or disabled.
    * 
    * <pre>
    * GET /admin/user/{id}
    * </pre>
    * 
    * @param userUrn
    *           the reference for the user
    * @return a user
    */
   User get(String userUrn);

   User get(URI userHref);

   /**
    * Modifies a user. The user object could be enabled or disabled. Note: the lock status cannot be
    * changed using this call: use unlockUser.
    * 
    * <pre>
    * PUT /admin/user/{id}
    * </pre>
    * 
    * @param userUrn
    *           the reference for the user
    * @return the modified user
    */
   User edit(String userUrn, User user);
   
   User edit(URI userHref, User user);

   /**
    * Deletes a user. Enabled and disabled users could be removed.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   void remove(String userUrn);
   
   void remove(URI userHref);

   /**
    * Unlocks a user.
    * 
    * <pre>
    * POST /admin/user/{id}/action/unlock
    * </pre>
    */
   void unlock(String userUrn);

   void unlock(URI userHref);
}
