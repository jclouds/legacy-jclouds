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
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.Host;
import org.jclouds.openstack.nova.v2_0.domain.HostResourceUsage;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides asynchronous access to Host Administration features via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see HostAdministrationAsyncApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.HOSTS)
public interface HostAdministrationApi {

   /**
    * Returns the list of hosts
    *
    * @return the usage information
    */
   FluentIterable<? extends Host> list();

   /**
    * Retrieves the physical/usage resource on a specific host
    *
    * @return the usage information
    */
   FluentIterable<? extends HostResourceUsage> listResourceUsage(String hostId);

   /**
    * Allow the specified host to accept new instances.
    *
    * @return true if successful
    */
   boolean enable(String hostId);

   /**
    * Prevent the specified host from accepting new instances.
    *
    * @return true if successful
    */
   boolean disable(String hostId);

   /**
    * Start host maintenance window.
    * <p/>
    * Note: this triggers guest VMs evacuation.
    *
    * @return true if successful
    */
   boolean startMaintenance(String hostId);

   /**
    * Stop host maintenance window.
    *
    * @return true if successful
    */
   boolean stopMaintenance(String hostId);

   /**
    * Startup a host.
    *
    * @return true if successful
    */
   boolean startup(String hostId);

   /**
    * Shutdown a host.
    *
    * @return true if successful
    */
   boolean shutdown(String hostId);

   /**
    * Reboot a host.
    *
    * @return true if successful
    */
   boolean reboot(String hostId);

}
