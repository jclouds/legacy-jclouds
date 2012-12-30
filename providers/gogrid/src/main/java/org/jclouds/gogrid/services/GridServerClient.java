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
package org.jclouds.gogrid.services;

import java.util.Map;
import java.util.Set;
import org.jclouds.domain.Credentials;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;

/**
 * Provides synchronous access to GoGrid.
 * <p/>
 * 
 * @see GridServerAsyncClient
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 * 
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
public interface GridServerClient {

   /**
    * Returns the list of all servers.
    * 
    * The result can be narrowed down by providing the options.
    * 
    * @param getServerListOptions
    *           options to narrow down the result
    * @return servers found by the request
    */
   Set<Server> getServerList(GetServerListOptions... getServerListOptions);

   /**
    * Returns the server(s) by unique name(s).
    * 
    * Given a name or a set of names, finds one or multiple servers.
    * 
    * @param names
    *           to get the servers
    * @return server(s) matching the name(s)
    */
   Set<Server> getServersByName(String... names);

   /**
    * Returns the server(s) by unique id(s).
    * 
    * Given an id or a set of ids, finds one or multiple servers.
    * 
    * @param ids
    *           to get the servers
    * @return server(s) matching the ids
    */
   Set<Server> getServersById(long... ids);

   /**
    * Returns a map of running servers' names to the log in credentials.
    * 
    * @return map <String server name => Credentials>
    */
   Map<String, Credentials> getServerCredentialsList();

   /**
    * 
    * @return the login user and password of a server, or null if none found
    */
   Credentials getServerCredentials(long id);

   /**
    * Adds a server with specified attributes
    * 
    * @param name
    *           name of the server
    * @param image
    *           image (id or name)
    * @param ram
    *           ram type (id or name)
    * @param ip
    *           ip address
    * @param addServerOptions
    *           options to make it a sandbox instance or/and description
    * @return created server
    */
   Server addServer(String name, String image, String ram, String ip,
         AddServerOptions... addServerOptions);

   /**
    * Changes the server's state according to {@link PowerCommand}
    * 
    * @param idOrName
    *           id or name of the server to apply the command
    * @param power
    *           new desired state
    * @return server immediately after applying the command
    */
   Server power(String idOrName, PowerCommand power);

   /**
    * Deletes the server by Id
    * 
    * @param id
    *           id of the server to delete
    * @return server before the command is executed
    */
   Server deleteById(long id);

   /**
    * Deletes the server by name;
    * 
    * NOTE: Using this parameter may generate an error if one or more servers
    * share a non-unique name.
    * 
    * @param name
    *           name of the server to be deleted
    * 
    * @return server before the command is executed
    */
   Server deleteByName(String name);
   
   /**
    * Edits an existing server
    * 
    * @param id
    *           id of the existing server
    * @param newDescription
    *           description to replace the current one
    * @return edited server
    */
   Server editServerDescription(long id, String newDescription);
   
   /**
    * Edits an existing server
    * 
    * @param id
    *           id of the existing server
    * @param idOrNameOfType
    *           type to replace the current one
    * @return edited server
    */
   Server editServerType(long id, String idOrNameOfType);
   
   /**
    * Edits an existing server
    * 
    * @param id
    *           id of the existing server
    * @param idOrNameOfRam
    *           ram to replace the current one
    * @return edited server
    */
   Server editServerRam(long id, String idOrNameOfRam);
   
   /**
    * Retrieves the list of supported RAM configurations. The objects will have
    * RAM ID, name and description. In most cases, id or name will be used for
    * {@link #addServer}.
    * 
    * To see how RAM maps to CPU and disk space (as of March 2010), see
    * {@link org.jclouds.gogrid.compute.config.GoGridComputeServiceContextModule#provideSizeToRam}
    * .
    * 
    * @return supported ram sizes
    */
   Set<Option> getRamSizes();

   /**
    * Retrieves the list of supported server types, for example Web/App Server and Database Server. In most cases, id
    * or name will be used for {@link #editServerType}.
    * 
    * @return supported server types
    */
   Set<Option> getTypes();
   
   /**
    * Retrieves the list of supported Datacenters to launch servers into. The
    * objects will have datacenter ID, name and description. In most cases, id
    * or name will be used for {@link #addServer}.
    * 
    * @return supported datacenters
    */
   Set<Option> getDatacenters();
}
