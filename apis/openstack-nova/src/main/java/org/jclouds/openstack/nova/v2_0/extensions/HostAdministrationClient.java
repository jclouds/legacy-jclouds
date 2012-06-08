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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

/**
 * Provides asynchronous access to Host Administration features via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see HostAdministrationAsyncClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.HOSTS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface HostAdministrationClient {

   /**
    * Returns the list of hosts
    *
    * @return the usage information
    */
   Set<Host> listHosts();

   /**
    * Retrieves the physical/usage resource on a specific host
    *
    * @return the usage information
    */
   Set<HostResourceUsage> getHostResourceUsage(String hostId);

   /**
    * Allow the specified host to accept new instances.
    *
    * @return true if successful
    */
   Boolean enableHost(String hostId);

   /**
    * Prevent the specified host from accepting new instances.
    *
    * @return true if successful
    */
   Boolean disableHost(String hostId);

   /**
    * Start host maintenance window.
    * <p/>
    * Note: this triggers guest VMs evacuation.
    *
    * @return true if successful
    */
   Boolean startHostMaintenance(String hostId);

   /**
    * Stop host maintenance window.
    *
    * @return true if successful
    */
   Boolean stopHostMaintenance(String hostId);

   /**
    * Startup a host.
    *
    * @return true if successful
    */
   Boolean startupHost(String hostId);

   /**
    * Shutdown a host.
    *
    * @return true if successful
    */
   Boolean shutdownHost(String hostId);

   /**
    * Reboot a host.
    *
    * @return true if successful
    */
   Boolean rebootHost(String hostId);

}
