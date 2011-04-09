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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class System {
   protected final int id;
   protected final String name;
   protected final String identifier;
   protected final Set<String> virtualSystemTypes = Sets.newLinkedHashSet();

   public System(int id, String name, String identifier, Iterable<String> virtualSystemTypes) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.identifier = checkNotNull(identifier, "identifier");
      Iterables.addAll(this.virtualSystemTypes, checkNotNull(virtualSystemTypes, "virtualSystemTypes"));

   }

   public String getName() {
      return name;
   }

   public int getId() {
      return id;
   }

   public String getIdentifier() {
      return identifier;
   }

   /**
    * specifies a virtual system virtualSystemTypes identifier, which is an implementation defined string that
    * uniquely identifies the virtualSystemTypes of the virtual system.
    * 
    * <p/>
    * For example, a virtual system virtualSystemTypes identifier could be vmx-4 for VMware’s fourth-generation
    * virtual hardware or xen-3 for Xen’s third-generation virtual hardware.
    * 
    */
   public Set<String> getVirtualSystemTypes() {
      return virtualSystemTypes;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((virtualSystemTypes == null) ? 0 : virtualSystemTypes.hashCode());
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
      System other = (System) obj;
      if (id != other.id)
         return false;
      if (identifier == null) {
         if (other.identifier != null)
            return false;
      } else if (!identifier.equals(other.identifier))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (virtualSystemTypes == null) {
         if (other.virtualSystemTypes != null)
            return false;
      } else if (!virtualSystemTypes.equals(other.virtualSystemTypes))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "VirtualSystem [id=" + id + ", identifier=" + identifier + ", name=" + name + ", virtualSystemTypes="
               + virtualSystemTypes + "]";
   }

}