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
package org.jclouds.openstack.quantum.v1_0.features;

import org.jclouds.openstack.quantum.v1_0.domain.Network;
import org.jclouds.openstack.quantum.v1_0.domain.NetworkDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Network operations on the openstack quantum API.
 * <p/>
 * Each tenant can define one or more networks. A network is a virtual isolated layer-2 broadcast domain reserved to the
 * tenant. A tenant can create several ports for a network, and plug virtual interfaces into these ports.
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.quantum.v1_0.features.NetworkAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/1.0/content/Networks.html">api doc</a>
 */
public interface NetworkApi {

   /**
    * Returns the list of all networks currently defined in Quantum for the current tenant. The list provides the unique
    * identifier of each network configured for the tenant.
    */
   FluentIterable<? extends Reference> listReferences();

   /**
    * Returns all networks currently defined in Quantum for the current tenant.
    */
   FluentIterable<? extends Network> list();

   /**
    * Returns the specific network.
    */
   Network get(String id);

   /**
    * Returns the details of the specific network.
    */
   NetworkDetails getDetails(String id);

   /**
    * Create a new network with the specified symbolic name
    */
   Reference create(String name);

   /**
    * Adjusts the symbolic name of a network
    *
    * @param id   the id of the Network to modify
    * @param name the new name for the Network
    */
   boolean rename(String id, String name);

   /**
    * Deletes the specified network
    */
   boolean delete(String id);
}
