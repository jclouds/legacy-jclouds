/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.opscodeplatform;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.chef.ChefClient;
import org.jclouds.concurrent.Timeout;
import org.jclouds.opscodeplatform.domain.Organization;
import org.jclouds.opscodeplatform.domain.User;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to the Opscode Platform.
 * <p/>
 * 
 * @see OpscodePlatformAsyncClient
 * @see <a href="TODO: insert URL of Opscode Platform documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface OpscodePlatformClient {
   /**
    * @return list of user names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list users.
    */
   Set<String> listUsers();

   /**
    * 
    * @return true if the specified user name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the user.
    */
   boolean userExists(String name);

   /**
    * creates a new user
    * 
    * @return the private key of the user. You can then use this user name and private key to access
    *         the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a user.
    */
   User createUser(User user);

   /**
    * updates an existing user. Note: you must have update rights on the user.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Update rights on the user.
    * @throws ResourceNotFoundException
    *            if the user does not exist.
    */
   User updateUser(User user);

   /**
    * retrieves an existing user. Note: you must have update rights on the user.
    * 
    * @return null, if the user is not found
    */
   User getUser(String username);

   /**
    * deletes an existing user. Note: you must have delete rights on the user.
    * 
    * @return last state of the user you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the user.
    */
   User deleteUser(String username);

   /**
    * @return list of organization names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list organizations.
    */
   Set<String> listOrganizations();

   /**
    * 
    * @return true if the specified organization name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the organization.
    */
   boolean organizationExists(String name);

   /**
    * creates a new organization
    * 
    * @return the private key of the organization. You can then use this organization name and
    *         private key to access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized organization.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a organization.
    */
   Organization createOrganization(Organization organization);

   /**
    * updates an existing organization. Note: you must have update rights on the organization.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized organization.
    *            <p/>
    *            "403 Forbidden" if you do not have Update rights on the organization.
    * @throws ResourceNotFoundException
    *            if the organization does not exist.
    */
   Organization updateOrganization(Organization organization);

   /**
    * retrieves an existing organization. Note: you must have update rights on the organization.
    * 
    * @return null, if the organization is not found
    */
   Organization getOrganization(String organizationname);

   /**
    * deletes an existing organization. Note: you must have delete rights on the organization.
    * 
    * @return last state of the org you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized organization.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the organization.
    */
   Organization deleteOrganization(String organizationname);
}
