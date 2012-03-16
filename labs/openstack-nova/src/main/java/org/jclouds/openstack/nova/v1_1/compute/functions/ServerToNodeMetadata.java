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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.openstack.nova.v1_1.domain.Address;
import org.jclouds.openstack.nova.v1_1.domain.Server;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A function for transforming a nova-specific Server into a generic
 * NodeMetadata object.
 * 
 * @author Matt Stephenson
 */
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   @Override
   public NodeMetadata apply(Server server) {
      return new NodeMetadataBuilder()
            // TODO: scope id to region, if there's a chance for conflict
            .id(server.getId())
            .providerId(server.getId())
            .name(server.getName())
            .publicAddresses(
                  Iterables.transform(server.getPublicAddresses(), new AddressToStringTransformationFunction()))
            .privateAddresses(
                  Iterables.transform(server.getPrivateAddresses(), new AddressToStringTransformationFunction()))
            .state(server.getStatus().getNodeState()).userMetadata(ImmutableMap.copyOf(server.getMetadata())).build();
   }

   private class AddressToStringTransformationFunction implements Function<Address, String> {
      @Override
      public String apply(Address address) {
         return address.getAddr();
      }
   }
}
