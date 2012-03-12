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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.User;

/**
 * Provides synchronous access to {@link Group} objects.
 * 
 * @see GroupAsyncClient
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface UserClient {
   /**
    * Creates or imports a user in an organization. The user could be enabled or disabled.
    *
    * <pre>
    * POST /admin/org/{id}/users
    * </pre>
    *
    * @param orgRef the reference for the org
    * @return the created user
    */
   User createUser(URI orgRef, User user);
   
   /**
    * Retrieves a user. This entity could be enabled or disabled.
    *
    * <pre>
    * GET /admin/user/{id}
    * </pre>
    *
    * @param userRef the reference for the user
    * @return a user
    */
   User getUser(URI userRef);
   
   /**
    * Modifies a user. The user object could be enabled or disabled. 
    * Note: the lock status cannot be changed using this call: use unlockUser.
    *
    * <pre>
    * PUT /admin/user/{id}
    * </pre>
    *
    * @param userRef the reference for the user
    * @return the modified user
    */
   User updateUser(URI userRef, User user);
   
   /**
    * Deletes a user. Enabled and disabled users could be deleted.
    * 
    * <pre>
    * DELETE /admin/catalog/{id}
    * </pre>
    */
   void deleteUser(URI userRef);
   
//   POST /admin/user/{id}/action/unlock
}
