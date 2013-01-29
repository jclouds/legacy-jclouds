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
package org.jclouds.rackspace.cloudloadbalancers.features;

import org.jclouds.rackspace.cloudloadbalancers.domain.HealthMonitor;

/**
 * The load balancing service includes a health monitoring operation which periodically checks your back-end nodes to 
 * ensure they are responding correctly. If a node is not responding, it is removed from rotation until the health 
 * monitor determines that the node is functional. In addition to being performed periodically, the health check also 
 * is performed against every node that is added to ensure that the node is operating properly before allowing it to 
 * service traffic. Only one health monitor is allowed to be enabled on a load balancer at a time.
 * </p>
 * As part of your strategy for monitoring connections, you should consider defining secondary nodes that provide 
 * failover for effectively routing traffic in case the primary node fails. This is an additional feature that will 
 * ensure you remain up in case your primary node fails.
 * <p/>
 * @see HealthMonitorAsyncApi
 * @author Everett Toews
 */
public interface HealthMonitorApi {
   /**
    * Create or update a health monitor.
    */
   void createOrUpdate(HealthMonitor healthMonitor);

   /**
    * Get health monitor.
    */
   HealthMonitor get();
   
   /**
    * Delete health monitor.
    * 
    * @return true on a successful delete, false if the health monitor was not found
    */
   boolean delete();
}