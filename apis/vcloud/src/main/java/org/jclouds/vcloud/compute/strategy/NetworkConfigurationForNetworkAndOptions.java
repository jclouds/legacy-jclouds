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
package org.jclouds.vcloud.compute.strategy;

import java.net.URI;

import javax.inject.Inject;

import org.jclouds.ovf.Network;
import org.jclouds.vcloud.compute.options.VCloudTemplateOptions;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.NetworkConfig;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Beta
public class NetworkConfigurationForNetworkAndOptions {
   protected final Supplier<NetworkConfig> defaultNetworkConfig;
   protected final FenceMode defaultFenceMode;

   @Inject
   protected NetworkConfigurationForNetworkAndOptions(Supplier<NetworkConfig> defaultNetworkConfig,
         FenceMode defaultFenceMode) {
      this.defaultNetworkConfig = defaultNetworkConfig;
      this.defaultFenceMode = defaultFenceMode;
   }

   /**
    * 
    * returns a {@link NetworkConfig} used to instantiate a vAppTemplate to
    * either the default parent (org) network, or one specified by options.
    * 
    * @param networkToConnect
    *           network defined in the VAppTemplate you wish to connect to
    * @param vOptions
    *           options to override defaults with
    * @return
    */
   public NetworkConfig apply(Network networkToConnect, VCloudTemplateOptions vOptions) {
      NetworkConfig config;
      URI userDefinedParentNetwork = vOptions.getParentNetwork();
      FenceMode fenceMode = vOptions.getFenceMode() != null ? vOptions.getFenceMode() : defaultFenceMode;
      if (userDefinedParentNetwork != null) {
         config = NetworkConfig.builder().networkName("jclouds").fenceMode(fenceMode)
               .parentNetwork(userDefinedParentNetwork).build();
      } else {
         config = defaultNetworkConfig.get().toBuilder().fenceMode(fenceMode).build();
      }

      // if we only have a disconnected network, we are adding a new section
      // for the upstream
      if (InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn.networkWithNoIpAllocation
            .apply(networkToConnect)) {
         // TODO: remove the disconnected entry
      } else {
         config = config.toBuilder().networkName(networkToConnect.getName()).build();
      }
      return config;
   }

}
