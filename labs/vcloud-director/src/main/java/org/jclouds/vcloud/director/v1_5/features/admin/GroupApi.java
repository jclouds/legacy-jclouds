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
import org.jclouds.vcloud.director.v1_5.domain.Group;

/**
 * Provides synchronous access to {@link Group} objects.
 * 
 * @see GroupAsyncApi
 * @author danikov, Adrian Cole
 */
public interface GroupApi {
   
   /**
    * Imports a group in an organization.
    *
    * <pre>
    * POST /admin/org/{id}/groups
    * </pre>
    *
    * @param orgUrn the admin org to add the group in
    * @return the addd group
    */
   Group addGroupToOrg(Group group, String orgUrn);
   
   Group addGroupToOrg(Group group, URI orgHref);

   /**
    * Retrieves a group.
    *
    * <pre>
    * GET /admin/group/{id}
    * </pre>
    *
    * @param groupString the reference for the group
    * @return a group
    */
   Group get(String groupUrn);

   Group get(URI groupHref);

   /**
    * Modifies a group.
    * 
    * <pre>
    * PUT /admin/group/{id}
    * </pre>
    * 
    * @return the edited group
    */
   Group edit(String groupUrn, Group group);

   Group edit(URI groupHref, Group group);

   /**
    * Deletes a group.
    * 
    * <pre>
    * DELETE /admin/group/{id}
    * </pre>
    */
   void remove(String groupUrn);

   void remove(URI groupHref);

}
