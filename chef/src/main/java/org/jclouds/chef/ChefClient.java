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

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

/**
 * Provides synchronous access to Chef.
 * <p/>
 * 
 * @see ChefAsyncClient
 * @see <a href="TODO: insert URL of Chef documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ChefClient {
   /**
    * 
    * FIXME Comment this
    * 
    * @param md5s
    *           raw md5s; uses {@code Bytes.asList()} and {@code
    *           Bytes.toByteArray()} as necessary
    * @return
    */
   UploadSandbox getUploadSandboxForChecksums(Set<List<Byte>> md5s);

   Sandbox commitSandbox(String id, boolean isCompleted);

   /**
    * 
    * @return a list of all the cookbook names
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have permission to see the
    *            cookbook list.
    */
   Set<String> listCookbooks();

   /**
    * Creates or updates (uploads) a cookbook //TODO document
    * 
    * @param cookbookName
    * @throws HttpResponseException
    *            "409 Conflict" if the cookbook already exists
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   CookbookVersion updateCookbook(String cookbookName, String version, CookbookVersion cookbook);

   /**
    * deletes an existing cookbook.
    * 
    * @return last state of the client you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the
    *            cookbook.
    */
   CookbookVersion deleteCookbook(String cookbookName, String version);

   /**
    * 
    * @return the versions of a cookbook or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to view the
    *            cookbook.
    */
   Set<String> getVersionsOfCookbook(String cookbookName);

   /**
    * Returns a description of the cookbook, with links to all of its component
    * parts, and the metadata.
    * 
    * @return the cookbook or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to view the
    *            cookbook.
    */
   CookbookVersion getCookbook(String cookbookName, String version);

   /**
    * creates a new client
    * 
    * @return the private key of the client. You can then use this client name
    *         and private key to access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String createClient(String name);

   /**
    * generate a new key-pair for this client, and return the new private key in
    * the response body.
    * 
    * @return the new private key
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to modify the
    *            client.
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String generateKeyForClient(String name);

   /**
    * @return list of client names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list clients.
    */
   Set<String> listClients();

   /**
    * 
    * @return true if the specified client name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the client.
    */
   boolean clientExists(String name);

   /**
    * deletes an existing client.
    * 
    * @return last state of the client you deleted or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the client.
    */
   Client deleteClient(String name);

   /**
    * gets an existing client.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the client.
    */
   Client getClient(String name);

   /**
    * creates a new node
    * 
    * @return //TODO
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            node.
    * @throws HttpResponseException
    *            "409 Conflict" if the node already exists
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   Node createNode(Node node);

   /**
    * Creates or updates (uploads) a node //TODO document
    * 
    * @param nodeName
    * @throws HttpResponseException
    *            "409 Conflict" if the node already exists
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   Node updateNode(Node node);

   /**
    * @return list of node names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list nodes.
    */
   Set<String> listNodes();

   /**
    * 
    * @return true if the specified node name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the node.
    */
   boolean nodeExists(String name);

   /**
    * deletes an existing node.
    * 
    * @return last state of the node you deleted or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the node.
    */
   Node deleteNode(String name);

   /**
    * gets an existing node.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the node.
    */
   Node getNode(String name);

   /**
    * creates a new role
    * 
    * @return //TODO
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            role.
    * @throws HttpResponseException
    *            "409 Conflict" if the role already exists
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   Role createRole(Role role);

   /**
    * Creates or updates (uploads) a role //TODO document
    * 
    * @param roleName
    * @throws HttpResponseException
    *            "409 Conflict" if the role already exists
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   Role updateRole(Role role);

   /**
    * @return list of role names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list roles.
    */
   Set<String> listRoles();

   /**
    * 
    * @return true if the specified role name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the role.
    */
   boolean roleExists(String name);

   /**
    * deletes an existing role.
    * 
    * @return last state of the role you deleted or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the role.
    */
   Role deleteRole(String name);

   /**
    * gets an existing role.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the role.
    */
   Role getRole(String name);
}
