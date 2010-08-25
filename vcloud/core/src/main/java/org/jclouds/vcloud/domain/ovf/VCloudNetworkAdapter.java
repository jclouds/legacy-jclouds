/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.domain.ovf;

/**
 * @author Adrian Cole
 * 
 */
public class VCloudNetworkAdapter extends ResourceAllocation {
   private final String ipAddress;
   private final boolean primaryNetworkConnection;
   private final String ipAddressingMode;

   public VCloudNetworkAdapter(int id, String name, String description, ResourceType type, String subType,
            String hostResource, String address, Integer addressOnParent, Integer parent, Boolean connected,
            long virtualQuantity, String virtualQuantityUnits, String ipAddress, boolean primaryNetworkConnection,
            String ipAddressingMode) {
      super(id, name, description, type, subType, hostResource, address, addressOnParent, parent, connected,
               virtualQuantity, virtualQuantityUnits);
      this.ipAddress = ipAddress;
      this.primaryNetworkConnection = primaryNetworkConnection;
      this.ipAddressingMode = ipAddressingMode;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public boolean isPrimaryNetworkConnection() {
      return primaryNetworkConnection;
   }

   public String getIpAddressingMode() {
      return ipAddressingMode;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
      result = prime * result + ((ipAddressingMode == null) ? 0 : ipAddressingMode.hashCode());
      result = prime * result + (primaryNetworkConnection ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VCloudNetworkAdapter other = (VCloudNetworkAdapter) obj;
      if (ipAddress == null) {
         if (other.ipAddress != null)
            return false;
      } else if (!ipAddress.equals(other.ipAddress))
         return false;
      if (ipAddressingMode == null) {
         if (other.ipAddressingMode != null)
            return false;
      } else if (!ipAddressingMode.equals(other.ipAddressingMode))
         return false;
      if (primaryNetworkConnection != other.primaryNetworkConnection)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + ", type=" + getType()
               + ", address=" + getAddress() + ", ipAddress=" + ipAddress + ", ipAddressingMode=" + ipAddressingMode
               + ", primaryNetworkConnection=" + primaryNetworkConnection + "]";
   }

}