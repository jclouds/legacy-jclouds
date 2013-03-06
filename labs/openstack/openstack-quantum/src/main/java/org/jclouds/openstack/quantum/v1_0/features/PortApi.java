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

import org.jclouds.openstack.quantum.v1_0.domain.Attachment;
import org.jclouds.openstack.quantum.v1_0.domain.Port;
import org.jclouds.openstack.quantum.v1_0.domain.PortDetails;
import org.jclouds.openstack.quantum.v1_0.domain.Reference;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Port operations on the openstack quantum API.
 * <p/>
 * A port represents a virtual switch port on a logical network switch where all the interfaces attached to a given network are connected.
 * <p/>
 * A port has an administrative state which is either 'DOWN' or 'ACTIVE'. Ports which are administratively down will not be able to receive/send traffic.
 *
 * @author Adam Lowe
 * @see PortAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-network/1.0/content/Ports.html">api doc</a>
 */
public interface PortApi {
   /**
    * Returns the list of all ports currently defined in Quantum for the requested network
    */
   FluentIterable<? extends Reference> listReferences();

   /**
    * Returns the set of ports currently defined in Quantum for the requested network.
    */
   FluentIterable<? extends Port> list();

   /**
    * Returns a specific port.
    */
   Port get(String id);

   /**
    * Returns a specific port in detail.
    */
   PortDetails getDetails(String id);

   /**
    * Create a new port on the specified network
    */
   Reference create();

   /**
    * Create a new port on the specified network, with the requested state
    */
   Reference create(Port.State state);

   /**
    * Updates the state of a port
    */
   boolean updateState(String id, Port.State state);

   /**
    * Deletes a port from a network
    */
   boolean delete(String id);

   /**
    * Returns the attachment for the specified port.
    */
   Attachment showAttachment(String portId);

   /**
    * Plugs an attachment into the specified port
    */
   boolean plugAttachment(String portId, String attachmentId);

   /**
    *  Unplugs the attachment currently plugged into the specified port
    */
   boolean unplugAttachment(String portId);
}
