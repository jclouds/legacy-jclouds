/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef;

import java.util.concurrent.TimeUnit;

import org.jclouds.chef.domain.Organization;
import org.jclouds.chef.domain.User;
import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Provides synchronous access to Chef.
 * <p/>
 * 
 * @see ChefAsyncClient
 * @see <a href="TODO: insert URL of Chef documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 4, timeUnit = TimeUnit.SECONDS)
public interface ChefClient {

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
   String createUser(User user);

   /**
    * updates an existing user. Note: you must have update rights on the user.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            “401 Unauthorized” if you are not a recognized user.
    *            <p/>
    *            “403 Forbidden” if you do not have Update rights on the user.
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
    * @return the last state of the User object in question. * @throws AuthorizationException
    *         <p/>
    *         “401 Unauthorized” if you are not a recognized user.
    *         <p/>
    *         “403 Forbidden” if you do not have Delete rights on the user.
    * @throws ResourceNotFoundException
    *            <p/>
    *            “404 Not Found” if the user does not exist.
    */
   User deleteUser(String username);

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
   String createOrganization(Organization organization);

   /**
    * updates an existing organization. Note: you must have update rights on the organization.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            “401 Unauthorized” if you are not a recognized organization.
    *            <p/>
    *            “403 Forbidden” if you do not have Update rights on the organization.
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
    * @return the last state of the Organization object in question. * @throws
    *         AuthorizationException
    *         <p/>
    *         “401 Unauthorized” if you are not a recognized organization.
    *         <p/>
    *         “403 Forbidden” if you do not have Delete rights on the organization.
    * @throws ResourceNotFoundException
    *            <p/>
    *            “404 Not Found” if the organization does not exist.
    */
   Organization deleteOrganization(String organizationname);
}
