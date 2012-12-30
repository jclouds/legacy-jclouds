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

import java.util.Set;
import org.jclouds.gogrid.domain.Ip;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.options.GetIpListOptions;

/**
 * @author Oleksiy Yarmula
 */
public interface GridIpClient {

   /**
    * Returns all IPs in the system that match the options
    * 
    * @param options
    *           options to narrow the search down
    * @return IPs found by the search
    */
   Set<Ip> getIpList(GetIpListOptions... options);

   /**
    * Returns the list of unassigned IPs.
    * 
    * NOTE: this returns both public and private IPs!
    * 
    * @return unassigned IPs
    */
   Set<Ip> getUnassignedIpList();

   /**
    * Returns the list of unassigned public IPs.
    * 
    * @return unassigned public IPs
    */
   Set<Ip> getUnassignedPublicIpList();

   /**
    * Returns the list of assigned IPs
    * 
    * NOTE: this returns both public and private IPs!
    * 
    * @return assigned IPs
    */
   Set<Ip> getAssignedIpList();

   /**
    * Retrieves the list of supported Datacenters to retrieve ips from. The objects will have
    * datacenter ID, name and description. In most cases, id or name will be used for
    * {@link #addServer}.
    * 
    * @return supported datacenters
    */
   Set<Option> getDatacenters();
}
