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

import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIPWithId;

/**
 * A virtual IP makes a load balancer accessible by clients. The load balancing service supports either a public VIP,
 * routable on the public Internet, or a ServiceNet address, routable only within the region in which the load balancer 
 * resides.
 * <p/>
 * 
 * @see VirtualIPAsyncApi
 * @author Everett Toews
 */
public interface VirtualIPApi {
   /**
    * Create a new virtual IP.
    */
   VirtualIPWithId create(VirtualIP virtualIP);

   /**
    * List the virtual IPs.
    */
   Iterable<VirtualIPWithId> list();
   
   /**
    * Delete a virtual IP.
    * 
    * @see VirtualIPApi#delete(Iterable)
    * 
    * @return true on a successful delete, false if the virtual IP was not found
    */
   boolean delete(int id);
   
   /**
    * Batch delete virtual IPs given the specified ids.
    * 
    * All load balancers must have at least one virtual IP associated with them at all times. Attempting to delete the
    * last virtual IP will result in an exception. The current default limit is ten ids per request. Any 
    * and all configuration data is immediately purged and is not recoverable. If one or more of the items in the list 
    * cannot be removed due to its current status, an exception is thrown along with the ids of the ones the 
    * system identified as potential failures for this request.
    * 
    * @return true on a successful delete, false if the virtual IP was not found
    */
   boolean delete(Iterable<Integer> ids);
}