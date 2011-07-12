/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.domain;

/**
 * @author Seshu Pasam
 */
public class NetworkAdapter implements Comparable<NetworkAdapter> {
   private final String macAddress;
   private final String name;
   private final Subnet subnet;

   public NetworkAdapter(String macAddress, String name, Subnet subnet) {
      this.macAddress = macAddress;
      this.name = name;
      this.subnet = subnet;
   }

   public int compareTo(NetworkAdapter that) {
      return (this == that) ? 0 : getMacAddress().compareTo(that.getMacAddress());
   }

   public String getMacAddress() {
      return macAddress;
   }

   public String getName() {
      return name;
   }

   public Subnet getSubnet() {
      return subnet;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((macAddress== null) ? 0 : macAddress.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((subnet == null) ? 0 : subnet.hashCode());
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
      NetworkAdapter other = (NetworkAdapter) obj;
      if (macAddress == null) {
         if (other.macAddress != null)
            return false;
      } else if (!macAddress.equals(other.macAddress))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (subnet == null) {
         if (other.subnet != null)
            return false;
      } else if (!subnet.equals(other.subnet))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[MAC address=" + macAddress + ", name=" + name + ", subnet=" + subnet + "]";
   }
}
