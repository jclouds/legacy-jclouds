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
package org.jclouds.openstack.quantum.v1_0;

import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.openstack.quantum.v1_0.features.NetworkApi;
import org.jclouds.openstack.quantum.v1_0.features.PortApi;
import org.jclouds.openstack.v2_0.features.ExtensionApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides synchronous access to Quantum.
 * <p/>
 *
 * @author Adam Lowe
 * @see QuantumAsyncApi
 * @see <a href="http://docs.openstack.org/api/openstack-network/1.0/content">api doc</a>
 */
public interface QuantumApi {
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides synchronous access to Extension features.
    */
   @Delegate
   ExtensionApi getExtensionApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkApi getNetworkApiForZone(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);

   /**
    * Provides synchronous access to Port features.
    */
   @Delegate
   @Path("/networks/{net}")
   PortApi getPortApiForZoneAndNetwork(@EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone,
                                               @PathParam("net") String networkId);
}
