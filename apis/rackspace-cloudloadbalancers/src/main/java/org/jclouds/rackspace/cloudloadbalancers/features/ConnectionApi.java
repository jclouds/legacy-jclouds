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

import org.jclouds.rackspace.cloudloadbalancers.domain.ConnectionThrottle;

/**
 * Connection management features.
 * <p/>
 * 
 * @see ConnectionAsyncApi
 * @author Everett Toews
 */
public interface ConnectionApi {
   /**
    * The connection throttling feature imposes limits on the number of connections per IP address to help mitigate 
    * malicious or abusive traffic to your applications. The attributes in the table that follows can be configured 
    * based on the traffic patterns for your sites.
    */
   void createOrUpdateConnectionThrottle(ConnectionThrottle connectionThrottle);

   /**
    * Get connection throttle.
    */
   ConnectionThrottle getConnectionThrottle();
   
   /**
    * Delete connection throttle.
    * 
    * @return true on a successful delete, false if the connection throttle was not found
    */
   boolean deleteConnectionThrottle();
   
   /**
    * Determine if the load balancer is logging connections.
    */
   boolean isConnectionLogging();
   
   /**
    * Enable logging connections.
    */
   void enableConnectionLogging();
   
   /**
    * Disable logging connections.
    */
   void disableConnectionLogging();
}