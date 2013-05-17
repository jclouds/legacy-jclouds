/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.features;

import java.util.Map;
import org.jclouds.glesys.domain.AllowedArgumentsForCreateServer;
import org.jclouds.glesys.domain.Console;
import org.jclouds.glesys.domain.OSTemplate;
import org.jclouds.glesys.domain.ResourceUsage;
import org.jclouds.glesys.domain.Server;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerLimit;
import org.jclouds.glesys.domain.ServerSpec;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.glesys.options.CloneServerOptions;
import org.jclouds.glesys.options.CreateServerOptions;
import org.jclouds.glesys.options.DestroyServerOptions;
import org.jclouds.glesys.options.UpdateServerOptions;
import org.jclouds.glesys.options.ServerStatusOptions;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Server.
 * <p/>
 *
 * @author Adrian Cole
 * @author Adam Lowe
 * @see ServerAsyncApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
public interface ServerApi {

   /**
    * Get a list of all servers on this account.
    *
    * @return an account's associated server objects.
    */
   FluentIterable<Server> list();

   /**
    * Get detailed information about a server such as hostname, hardware
    * configuration (cpu, memory and disk), ip addresses, cost, transfer, os and
    * more.
    *
    * @param id id of the server
    * @return server or null if not found
    */
   ServerDetails get(String id);

   /**
    * Get detailed information about a server status including up-time and
    * hardware usage (cpu, disk, memory and bandwidth)
    *
    * @param id      id of the server
    * @param options optional parameters
    * @return the status of the server or null if not found
    */
   ServerStatus getStatus(String id, ServerStatusOptions... options);

   /**
    * Get detailed information about a server's limits (for OpenVZ only).
    * <p/>
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   Map<String, ServerLimit> getLimits(String id);

   /**
    * Get information about how to connect to a server via VNC
    *
    * @param id id of the server
    * @return the requested information about the server or null if not found
    */
   Console getConsole(String id);

   /**
    * Get information about the OS templates available
    *
    * @return the set of information about each template
    */
   FluentIterable<OSTemplate> listTemplates();

   /**
    * Get information about valid arguments to #createServer for each platform
    *
    * @return a map of argument lists, keyed on platform
    */
   Map<String, AllowedArgumentsForCreateServer> getAllowedArgumentsForCreateByPlatform();

   /**
    * Reset the fail count for a server limit (for OpenVZ only).
    *
    * @param id   id of the server
    * @param type the type of limit to reset
    */
   Map<String, ServerLimit> resetLimit(String id, String type);

   /**
    * Reboot a server
    *
    * @param id id of the server
    */
   ServerDetails reboot(String id);

   /**
    * Start a server
    *
    * @param id id of the server
    */
   ServerDetails start(String id);

   /**
    * Stop a server
    *
    * @param id id of the server
    */
   ServerDetails stop(String id);

   /**
    * hard stop a server
    *
    * @param id id of the server
    */
   ServerDetails hardStop(String id);

   /**
    * Create a new server
    *
    * @param hostname     the host name of the new server
    * @param rootPassword the root password to use
    * @param options      optional settings ex. description
    */
   ServerDetails createWithHostnameAndRootPassword(ServerSpec serverSpec, String hostname, String rootPassword,
         CreateServerOptions... options);

   /**
    * Update the configuration of a server
    *
    * @param serverid the serverId of the server to edit
    * @param options  the settings to change
    */
   ServerDetails update(String serverid, UpdateServerOptions options);

   /**
    * Clone a server
    *
    * @param serverid the serverId of the server to clone
    * @param hostname the new host name of the cloned server
    * @param options  the settings to change
    */
   ServerDetails clone(String serverid, String hostname, CloneServerOptions... options);

   /**
    * Destroy a server
    *
    * @param id     the id of the server
    * @param keepIp if DestroyServerOptions.keepIp(true) the servers ip will be retained for use in your GleSYS account
    */
   ServerDetails destroy(String id, DestroyServerOptions keepIp);

   /**
    * Reset the root password of a server
    *
    * @param id       the id of the server
    * @param password the new password to use
    */
   ServerDetails resetPassword(String id, String password);

   /**
    * Return resource usage over time for server
    *
    * @param id       the id of the server
    * @param resource the name of the resource to retrieve usage information for (e.g. "cpuusage")
    * @param resolution the time-period to extract data for (one of "minute", "hour" or "day)
    */
   @Beta
   // TODO: better name
   ResourceUsage getResourceUsage(String id, String resource, String resolution);

}
