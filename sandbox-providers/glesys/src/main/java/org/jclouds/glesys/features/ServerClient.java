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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.concurrent.Timeout;
import org.jclouds.glesys.domain.*;
import org.jclouds.javax.annotation.Nullable;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Server.
 * <p/>
 *
 * @author Adrian Cole
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
    * @param id id of the server
    * @return the status of the server or null if not found
    */
   ServerStatus getServerStatus(String id);

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

   // TODO should these be squished into single sets?
   Map<String, Set<Template>> getTemplates();
   Map<String, ServerAllowedArguments> getAllowedArguments();
   
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
    * @param id id of the server
    */
   void stopServer(String id);

   /**
    * Create a new server
    *
    * @param datacenter  the data center to create the new server in
    * @param platform    the platform to use (i.e. "Xen" or "OpenVZ")
    * @param hostname    the host name of the new server
    * @param template    the template to use to create the new server
    * @param disksize    the amount of disk space, in GB, to allocate
    * @param memorysize  the memory, in MB, to allocate
    * @param cpucores    the number of CPU cores to allocate
    * @param rootpw      the root password to use
    * @param transfer    the transfer size
    * @param description a description of the server
    * @param ip          ip address to assign to the new server, required by Xen platform
    */
   ServerCreated createServer(String datacenter, String platform,
                              String hostname, String template, int disksize, int memorysize,
                              int cpucores, String rootpw, int transfer, @Nullable String description, @Nullable String ip);

   /**
    * Destroy a server
    *
    * @param id     the id of the server
    * @param keepIp if 1 the servers ip will be retained for use in your Glesys account
    */
   void destroyServer(String id, int keepIp);

   /**
    * Reset the root password of a server
    *
    * @param id       the id of the server
    * @param password the new password to use
    */
   void resetPassword(@FormParam("serverid") String id, @FormParam("newpassword") String password);

   
   
}