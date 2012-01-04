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
package org.jclouds.glesys.features;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.FormParam;

import org.jclouds.concurrent.Timeout;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerAllowedArguments;
import org.jclouds.glesys.domain.ServerConsole;
import org.jclouds.glesys.domain.ServerCreated;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerLimit;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.domain.ServerTemplate;
import org.jclouds.glesys.options.ServerCloneOptions;
import org.jclouds.glesys.options.ServerCreateOptions;
import org.jclouds.glesys.options.ServerDestroyOptions;
import org.jclouds.glesys.options.ServerEditOptions;
import org.jclouds.glesys.options.ServerStatusOptions;
import org.jclouds.glesys.options.ServerStopOptions;

/**
 * Provides synchronous access to Server.
 * <p/>
 *
 * @author Adrian Cole
 * @author Adam Lowe
 * @see ServerAsyncClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ServerClient {

   /**
    * Get a list of all servers on this account.
    *
    * @return an account's associated server objects.
    */
   Set<Server> listServers();

   /**
    * Get detailed information about a server such as hostname, hardware
    * configuration (cpu, memory and disk), ip addresses, cost, transfer, os and
    * more.
    *
    * @param id id of the server
    * @return server or null if not found
    */
   ServerDetails getServerDetails(String id);

   /**
    * Get detailed information about a server status including up-time and hardware usage
    * (cpu, disk, memory and bandwidth)
    *
    * @param id      id of the server
    * @param options optional parameters
    * @return the status of the server or null if not found
    */
   ServerStatus getServerStatus(String id, ServerStatusOptions... options);

   /**
    * Get detailed information about a server's limits (for OpenVZ only).
    * <p/>
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   Map<String, ServerLimit> getServerLimits(String id);


   /**
    * Get information about how to connect to a server via VNC
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   ServerConsole getServerConsole(String id);

   /**
    * Get information about the OS templates available
    *
    * @return a map of templates, keyed on platform
    */
   Map<String, Set<ServerTemplate>> getTemplates();

   /**
    * Get information about valid arguments to #createServer for each platform
    *
    * @return a map of argument lists, keyed on platform
    */
   Map<String, ServerAllowedArguments> getServerAllowedArguments();

   /**
    * Reset the fail count for a server limit (for OpenVZ only).
    *
    * @param id   id of the server
    * @param type the type of limit to reset
    */

   void resetServerLimit(String id, String type);

   /**
    * Reboot a server
    *
    * @param id id of the server
    */
   void rebootServer(String id);

   /**
    * Start a server
    *
    * @param id id of the server
    */
   void startServer(String id);

   /**
    * Stop a server
    *
    * @param id      id of the server
    * @param options optional parameters
    */
   void stopServer(String id, ServerStopOptions... options);

   /**
    * Create a new server
    *
    * @param datacenter the data center to create the new server in
    * @param platform   the platform to use (i.e. "Xen" or "OpenVZ")
    * @param hostname   the host name of the new server
    * @param template   the template to use to create the new server
    * @param disksize   the amount of disk space, in GB, to allocate
    * @param memorysize the memory, in MB, to allocate
    * @param cpucores   the number of CPU cores to allocate
    * @param rootpw     the root password to use
    * @param transfer   the transfer size
    * @param options    optional settings ex. description
    */
   ServerCreated createServer(String datacenter, String platform,
                              String hostname, String template, int disksize, int memorysize,
                              int cpucores, String rootpw, int transfer, ServerCreateOptions... options);

   /**
    * Edit the configuration of a server
    *
    * @param serverid the serverId of the server to edit
    * @param options  the settings to change
    */
   void editServer(String serverid, ServerEditOptions... options);

   /**
    * Clone a server
    *
    * @param serverid the serverId of the server to clone
    * @param hostname the new host name of the cloned server
    * @param options  the settings to change
    */
   ServerCreated cloneServer(String serverid, String hostname, ServerCloneOptions... options);

   /**
    * Destroy a server
    *
    * @param id     the id of the server
    * @param keepIp if ServerDestroyOptions.keepIp(true) the servers ip will be retained for use in your GleSYS account
    */
   void destroyServer(String id, ServerDestroyOptions keepIp);

   /**
    * Reset the root password of a server
    *
    * @param id       the id of the server
    * @param password the new password to use
    */
   void resetPassword(@FormParam("serverid") String id, @FormParam("newpassword") String password);


}