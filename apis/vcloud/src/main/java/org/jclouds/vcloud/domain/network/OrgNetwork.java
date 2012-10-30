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
package org.jclouds.vcloud.domain.network;

import java.util.List;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.network.internal.OrgNetworkImpl;

import com.google.inject.ImplementedBy;

/**
 * A vDC is a deployment environment for vApps. A Vdc element provides a user view of a vDC.
 * 
 * @author Adrian Cole
 */
@org.jclouds.vcloud.endpoints.Network
@ImplementedBy(OrgNetworkImpl.class)
public interface OrgNetwork extends ReferenceType {
   /**
    * The org this network belongs to.
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   ReferenceType getOrg();

   /**
    * optional description
    * 
    * @since vcloud api 0.8
    */
   @Nullable
   String getDescription();

   /**
    * read‐only container for Task elements. Each element in the container represents a queued,
    * running, or failed task owned by this object.
    * 
    * @since vcloud api 0.9
    */
   List<Task> getTasks();

   /**
    * 
    * @return properties of the network
    * 
    * @since vcloud api 0.9, but emulated for 0.8
    */
   Configuration getConfiguration();

   /**
    * A reference the network pool from which this network is provisioned. This element, which is
    * required when creating a NatRouted or Isolated network, is returned in response to a creation
    * request but not shown in subsequent GET requests.
    * 
    * @since vcloud api 0.9
    */
   @Nullable
   ReferenceType getNetworkPool();

   /**
    * list of external IP addresses that this network can use for NAT.
    * 
    * @since vcloud api 0.9
    */
   Set<String> getAllowedExternalIpAddresses();

   /**
    * The Configuration element specifies properties of a network.
    */
   interface Configuration {
      /**
       * defines the address range, gateway, netmask, and other properties of the network.
       * 
       * @since vcloud api 0.9, but emulated for 0.8
       */
      @Nullable
      IpScope getIpScope();

      /**
       * reference to a network to which this network connects
       * 
       * @since vcloud api 0.9
       */
      @Nullable
      ReferenceType getParentNetwork();

      /**
       * defines how this network is connected to its ParentNetwork
       * 
       * @since vcloud api 0.8
       */
      FenceMode getFenceMode();

      /**
       * defines a set of network features.
       * 
       * @since vcloud api 0.9, but emulated for 0.8
       */
      @Nullable Features getFeatures();
   }

}
