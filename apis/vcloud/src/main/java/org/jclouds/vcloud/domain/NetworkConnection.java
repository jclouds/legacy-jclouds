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
package org.jclouds.vcloud.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.network.IpAddressAllocationMode;

/**
 * describes a single network connection.
 * 
 * @author Adrian Cole
 */
public class NetworkConnection {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String network;
      private int networkConnectionIndex;
      private String ipAddress;
      private String externalIpAddress;
      private boolean connected;
      private String MACAddress;
      private IpAddressAllocationMode ipAddressAllocationMode;

      public Builder network(String network) {
         this.network = network;
         return this;
      }

      public Builder networkConnectionIndex(int networkConnectionIndex) {
         this.networkConnectionIndex = networkConnectionIndex;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      public Builder connected(boolean connected) {
         this.connected = connected;
         return this;
      }

      public Builder MACAddress(String MACAddress) {
         this.MACAddress = MACAddress;
         return this;
      }

      public Builder ipAddressAllocationMode(IpAddressAllocationMode ipAddressAllocationMode) {
         this.ipAddressAllocationMode = ipAddressAllocationMode;
         return this;
      }

      public NetworkConnection build() {
         return new NetworkConnection(network, networkConnectionIndex, ipAddress, externalIpAddress, connected,
               MACAddress, ipAddressAllocationMode);
      }

      public static Builder fromNetworkConnection(NetworkConnection in) {
         return new Builder().network(in.getNetwork()).networkConnectionIndex(in.getNetworkConnectionIndex())
               .ipAddress(in.getIpAddress()).externalIpAddress(in.getExternalIpAddress()).connected(in.isConnected())
               .MACAddress(in.getMACAddress()).ipAddressAllocationMode(in.getIpAddressAllocationMode());
      }
   }

   private final String network;
   private final int networkConnectionIndex;
   @Nullable
   private final String ipAddress;
   @Nullable
   private final String externalIpAddress;
   private final boolean connected;
   @Nullable
   private final String MACAddress;
   private final IpAddressAllocationMode ipAddressAllocationMode;

   public NetworkConnection(String network, int networkConnectionIndex, @Nullable String ipAddress,
         @Nullable String externalIpAddress, boolean connected, @Nullable String MACAddress,
         IpAddressAllocationMode ipAddressAllocationMode) {
      this.network = network;
      this.networkConnectionIndex = networkConnectionIndex;
      this.ipAddress = ipAddress;
      this.externalIpAddress = externalIpAddress;
      this.connected = connected;
      this.MACAddress = MACAddress;
      this.ipAddressAllocationMode = ipAddressAllocationMode;
   }

   /**
    * @return The name of the network to which this connection connects.
    */
   public String getNetwork() {
      return network;
   }

   /**
    * @return The value in the rasd:AddressOnParent element of the device supporting this
    *         connection.
    */
   public int getNetworkConnectionIndex() {
      return networkConnectionIndex;
   }

   /**
    * @return IP address of this connection
    */
   @Nullable
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * @return If the network that the NIC is connected to has NAT or port mapping, the external
    *         address is populated in this element.
    */
   @Nullable
   public String getExternalIpAddress() {
      return externalIpAddress;
   }

   /**
    * @return If the vApp is deployed, specifies the current state of its connection. If the vApp is
    *         undeployed, specifies whether this connection should be connected at deployment time.
    */
   public boolean isConnected() {
      return connected;
   }

   /**
    * @return MAC address of this connection
    */
   @Nullable
   public String getMACAddress() {
      return MACAddress;
   }

   /**
    * @return specifies how an IP address is allocated to this connection
    */
   public IpAddressAllocationMode getIpAddressAllocationMode() {
      return ipAddressAllocationMode;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((MACAddress == null) ? 0 : MACAddress.hashCode());
      result = prime * result + (connected ? 1231 : 1237);
      result = prime * result + ((externalIpAddress == null) ? 0 : externalIpAddress.hashCode());
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((ipAddressAllocationMode == null) ? 0 : ipAddressAllocationMode.hashCode());
      result = prime * result + ((network == null) ? 0 : network.hashCode());
      result = prime * result + networkConnectionIndex;
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
      NetworkConnection other = (NetworkConnection) obj;
      if (MACAddress == null) {
         if (other.MACAddress != null)
            return false;
      } else if (!MACAddress.equals(other.MACAddress))
         return false;
      if (connected != other.connected)
         return false;
      if (externalIpAddress == null) {
         if (other.externalIpAddress != null)
            return false;
      } else if (!externalIpAddress.equals(other.externalIpAddress))
         return false;
      if (ipAddress == null) {
         if (other.ipAddress != null)
            return false;
      } else if (!ipAddress.equals(other.ipAddress))
         return false;
      if (ipAddressAllocationMode == null) {
         if (other.ipAddressAllocationMode != null)
            return false;
      } else if (!ipAddressAllocationMode.equals(other.ipAddressAllocationMode))
         return false;
      if (network == null) {
         if (other.network != null)
            return false;
      } else if (!network.equals(other.network))
         return false;
      if (networkConnectionIndex != other.networkConnectionIndex)
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromNetworkConnection(this);
   }

   @Override
   public String toString() {
      return "[network=" + network + ", connected=" + connected + ", ipAddress=" + ipAddress + ", externalIpAddress="
            + externalIpAddress + ", networkConnectionIndex=" + networkConnectionIndex + ", ipAddressAllocationMode="
            + ipAddressAllocationMode + ", MACAddress=" + MACAddress + "]";
   }

}