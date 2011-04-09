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

import org.jclouds.vcloud.domain.ReferenceType;

/**
 * @author Adrian Cole
 * 
 */
public class EditableResourceAllocation extends ResourceAllocation {
   private final ReferenceType edit;

   public EditableResourceAllocation(int id, String name, String description, ResourceType type, String subType,
            String hostResource, String address, Integer addressOnParent, Integer parent, Boolean connected,
            long virtualQuantity, String virtualQuantityUnits, ReferenceType edit) {
      super(id, name, description, type, subType, hostResource, address, addressOnParent, parent, connected,
               virtualQuantity, virtualQuantityUnits);
      this.edit = edit;

   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", description=" + getDescription() + ", type=" + getType()
               + ", virtualQuantity=" + getVirtualQuantity() + ", virtualQuantityUnits=" + getVirtualQuantityUnits()
               + ", edit=" + edit + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((edit == null) ? 0 : edit.hashCode());
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
      EditableResourceAllocation other = (EditableResourceAllocation) obj;
      if (edit == null) {
         if (other.edit != null)
            return false;
      } else if (!edit.equals(other.edit))
         return false;
      return true;
   }

}