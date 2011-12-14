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
package org.jclouds.openstack.nova.v1_1.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.domain.ServerDetails;

/**
 * Provides synchronous access to Server.
 * <p/>
 * 
 * @see ServerAsyncClient
 * @see <a href="http://docs.openstack.org/api/openstack-compute/1.1/content/" />
 * @author Adrian Cole
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
    * configuration (cpu, memory and disk), ip adresses, cost, transfer, os and
    * more.
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   ServerDetails getServerDetails(String id);

    
}
