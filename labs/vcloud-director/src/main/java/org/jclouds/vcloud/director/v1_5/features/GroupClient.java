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

/**
 * Provides synchronous access to {@link Group} objects.
 * 
 * @see GroupAsyncClient
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface GroupClient {
   
   /**
    * Imports a group in an organization.
    *
    * <pre>
    * POST /admin/org/{id}/groups
    * </pre>
    *
    * @param orgUri the admin org to create the group in
    * @return the created group
    */
   Group createGroup(URI adminOrgUri, Group group);
   
   /**
    * Retrieves a group.
    *
    * <pre>
    * GET /admin/group/{id}
    * </pre>
    *
    * @param groupURI the reference for the group
    * @return a group
    */
   Group getGroup(URI groupUri);

   /**
    * Modifies a group.
    * 
    * <pre>
    * PUT /admin/group/{id}
    * </pre>
    * 
    * @return the updated group
    */
   Group updateGroup(URI groupRef, Group group);

   /**
    * Deletes a group.
    * 
    * <pre>
    * DELETE /admin/group/{id}
    * </pre>
    */
   void deleteGroup(URI groupRef);
}
