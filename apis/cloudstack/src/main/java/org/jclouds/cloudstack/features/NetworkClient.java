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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.options.CreateNetworkOptions;
import org.jclouds.cloudstack.options.ListNetworksOptions;

/**
 * Provides synchronous access to CloudStack network features.
 * <p/>
 * 
 * @see NetworkAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface NetworkClient {
   /**
    * Lists networks
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return networks matching query, or empty set, if no networks are found
    */
   Set<Network> listNetworks(ListNetworksOptions... options);

   /**
    * get a specific network by id
    * 
    * @param id
    *           network to get
    * @return network or null if not found
    */
   Network getNetwork(String id);

   /**
    * Creates a network
    * 
    * @param zoneId
    *           the Zone ID for the Vlan ip range
    * @param networkOfferingId
    *           the network offering id
    * @param name
    *           the name of the network
    * @param displayText
    *           the display text of the network
    * @param options
    *           optional parameters
    * @return newly created network
    */
   Network createNetworkInZone(String zoneId, String networkOfferingId, String name, String displayText,
         CreateNetworkOptions... options);

   /**
    * Deletes a network
    * 
    * @param id
    *           the ID of the network
    * @return job id related to destroying the network, or null if resource was
    *         not found
    */
   String deleteNetwork(String id);
}
