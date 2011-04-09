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
package org.jclouds.vcloud.domain.ovf;


/**
 * @author Adrian Cole
 * 
 */
public class VCloudHardDisk extends ResourceAllocation {
   private final long capacity;
   private final int busType;
   private final String busSubType;

   public VCloudHardDisk(int id, String name, String description, ResourceType type, String subType,
            String hostResource, String address, Integer addressOnParent, Integer parent, Boolean connected,
            long virtualQuantity, String virtualQuantityUnits, long capacity, int busType, String busSubType) {
      super(id, name, description, type, subType, hostResource, address, addressOnParent, parent, connected,
               virtualQuantity, virtualQuantityUnits);
      this.capacity = capacity;
      this.busType = busType;
      this.busSubType = busSubType;
   }

   public long getCapacity() {
      return capacity;
   }

   public int getBusType() {
      return busType;
   }

   public String getBusSubType() {
      return busSubType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((busSubType == null) ? 0 : busSubType.hashCode());
      result = prime * result + busType;
      result = prime * result + (int) (capacity ^ (capacity >>> 32));
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
      VCloudHardDisk other = (VCloudHardDisk) obj;
      if (busSubType == null) {
         if (other.busSubType != null)
            return false;
      } else if (!busSubType.equals(other.busSubType))
         return false;
      if (busType != other.busType)
         return false;
      if (capacity != other.capacity)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + ", type=" + getType()
               + ", virtualQuantity=" + getVirtualQuantity() + ", virtualQuantityUnits=" + getVirtualQuantityUnits()
               + ", capacity=" + capacity + ", busType=" + busType + ", busSubType=" + busSubType + "]";
   }

}