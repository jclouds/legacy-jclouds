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

package org.jclouds.chef;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.binders.BindToJsonPayload;

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
   Client createClient(String name);

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
   Client generateKeyForClient(String name);

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
   void createNode(Node node);

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
   void createRole(Role role);

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

   /**
    * lists databags available to the client
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   Set<String> listDatabags();

   /**
    * creates a databag.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   void createDatabag(String databagName);

   /**
    * true is a databag exists
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   boolean databagExists(String databagName);

   /**
    * Delete a data bag, including its items
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   void deleteDatabag(String databagName);

   /**
    * Show the items in a data bag.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   Set<String> listDatabagItems(String databagName);

   /**
    * Create a data bag item in the data bag
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    *            <p/>
    * @throws IllegalStateException
    *            if the item already exists
    * 
    */
   DatabagItem createDatabagItem(String databagName, @BinderParam(BindToJsonPayload.class) DatabagItem node);

   /**
    * Update (or create if not exists) a data bag item
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   DatabagItem updateDatabagItem(String databagName, DatabagItem item);

   /**
    * determines if a databag item exists
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   boolean databagItemExists(String databagName, String databagItemId);

   /**
    * gets an existing databag item.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   DatabagItem getDatabagItem(String databagName, String databagItemId);

   /**
    * Delete a data bag item
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   DatabagItem deleteDatabagItem(String databagName, String databagItemId);

   /**
    * Show indexes you can search on
    * <p/>
    * By default, the "role", "node" and "client" indexes will always be
    * available.
    * <p/>
    * Note that the search indexes may lag behind the most current data by at
    * least 10 seconds at any given time - so if you need to write data and
    * immediately query it, you likely need to produce an artificial delay (or
    * simply retry until the data is available.)
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the databag.
    */
   Set<String> listSearchIndexes();

   /**
    * search all roles.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   SearchResult<? extends Role> searchRoles();

   /**
    * search all clients.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   SearchResult<? extends Client> searchClients();

   /**
    * search all nodes.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   SearchResult<? extends Node> searchNodes();

   /**
    * search all items in a databag.
    * <p/>
    * Note that without any request parameters this will return all of the data
    * within the index.
    * 
    * @return The response contains the total number of rows that matched your
    *         request, the position this result set returns (useful for paging)
    *         and the rows themselves.
    */
   SearchResult<? extends DatabagItem> searchDatabag(String databagName);

}
