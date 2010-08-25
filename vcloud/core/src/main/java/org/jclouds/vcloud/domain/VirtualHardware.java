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

package org.jclouds.vcloud.domain;

import java.util.Set;

import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.System;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * A description of the virtual hardware supported by a virtual machine.
 */
public class VirtualHardware {

   protected final String info;
   protected final System virtualSystem;
   protected final Set<ResourceAllocation> resourceAllocations = Sets.newLinkedHashSet();

   public VirtualHardware(String info, System virtualSystem, Iterable<? extends ResourceAllocation> resourceAllocations) {
      this.info = info;
      this.virtualSystem = virtualSystem;
      Iterables.addAll(this.resourceAllocations, resourceAllocations);
   }

   public String getInfo() {
      return info;
   }

   public System getSystem() {
      return virtualSystem;
   }

   public Set<? extends ResourceAllocation> getResourceAllocations() {
      return resourceAllocations;
   }

   @Override
   public String toString() {
      return "[info=" + getInfo() + ", virtualSystem=" + getSystem() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((resourceAllocations == null) ? 0 : resourceAllocations.hashCode());
      result = prime * result + ((virtualSystem == null) ? 0 : virtualSystem.hashCode());
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
      VirtualHardware other = (VirtualHardware) obj;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (resourceAllocations == null) {
         if (other.resourceAllocations != null)
            return false;
      } else if (!resourceAllocations.equals(other.resourceAllocations))
         return false;
      if (virtualSystem == null) {
         if (other.virtualSystem != null)
            return false;
      } else if (!virtualSystem.equals(other.virtualSystem))
         return false;
      return true;
   }

}