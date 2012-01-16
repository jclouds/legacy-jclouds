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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.NetworkSection;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkConfig {

   public Builder toBuilder() {
      return builder().fromNetworkConfig(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String networkName;
      private URI parentNetwork;
      private FenceMode fenceMode;

      public Builder networkName(String networkName) {
         this.networkName = networkName;
         return this;
      }

      public Builder parentNetwork(URI parentNetwork) {
         this.parentNetwork = parentNetwork;
         return this;
      }

      public Builder fenceMode(FenceMode fenceMode) {
         this.fenceMode = fenceMode;
         return this;
      }

      public Builder fromNetworkConfig(NetworkConfig in) {
         return networkName(in.getNetworkName()).parentNetwork(in.getParentNetwork()).fenceMode(in.getFenceMode());
      }

      public NetworkConfig build() {
         return new NetworkConfig(networkName, parentNetwork, fenceMode);
      }
   }

   @Nullable
   private final String networkName;
   private final URI parentNetwork;
   @Nullable
   private final FenceMode fenceMode;

   /**
    * 
    * Create a new NetworkConfig.
    * 
    * @param networkName
    *           a valid {@networkConfig
    *           org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection network in the vapp
    *           template}, or null to have us choose default
    * @param parentNetwork
    *           a valid {@networkConfig org.jclouds.vcloud.domain.Org#getNetworks in
    *           the Org}
    * @param fenceMode
    *           how to manage the relationship between the two networks
    */
   public NetworkConfig(String networkName, URI parentNetwork, FenceMode fenceMode) {
      this.networkName = networkName;
      this.parentNetwork = checkNotNull(parentNetwork, "parentNetwork");
      this.fenceMode = fenceMode;
   }

   public NetworkConfig(URI parentNetwork) {
      this(null, parentNetwork, null);
   }

   /**
    * A name for the network. If the
    * {@link org.jclouds.vcloud.domain.VAppTemplate#getNetworkSection} includes a
    * {@link NetworkSection.Network} network element, the name you specify for the vApp network must
    * match the name specified in that element’s name attribute.
    * 
    * @return
    */
   public String getNetworkName() {
      return networkName;
   }

   /**
    * 
    * @return A reference to the organization network to which this network connects.
    */
   public URI getParentNetwork() {
      return parentNetwork;
   }

   /**
    * A value of bridged indicates that this vApp network is connected directly to the organization
    * network.
    */
   public FenceMode getFenceMode() {
      return fenceMode;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((fenceMode == null) ? 0 : fenceMode.hashCode());
      result = prime * result + ((parentNetwork == null) ? 0 : parentNetwork.hashCode());
      result = prime * result + ((networkName == null) ? 0 : networkName.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      NetworkConfig other = (NetworkConfig) obj;
      if (fenceMode == null) {
         if (other.fenceMode != null)
            return false;
      } else if (!fenceMode.equals(other.fenceMode))
         return false;
      if (parentNetwork == null) {
         if (other.parentNetwork != null)
            return false;
      } else if (!parentNetwork.equals(other.parentNetwork))
         return false;
      if (networkName == null) {
         if (other.networkName != null)
            return false;
      } else if (!networkName.equals(other.networkName))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[networkName=" + networkName + ", parentNetwork=" + parentNetwork + ", fenceMode=" + fenceMode + "]";
   }
}