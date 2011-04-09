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

/**
 * @author Adrian Cole
 */
public class VirtualSystem {
   private final String id;
   private final String info;
   private final String name;
   private final OperatingSystemSection operatingSystem;
   private final Set<VirtualHardwareSection> hardware = Sets.newLinkedHashSet();

   public VirtualSystem(String id, String info, String name, OperatingSystemSection operatingSystem,
            Iterable<? extends VirtualHardwareSection> hardware) {
      this.id = id;
      this.info = info;
      this.name = name;
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      Iterables.addAll(this.hardware, checkNotNull(hardware, "hardware"));
   }

   public String getId() {
      return id;
   }

   public String getInfo() {
      return info;
   }

   public String getName() {
      return name;
   }

   public OperatingSystemSection getOperatingSystem() {
      return operatingSystem;
   }

   /**
    * Each VirtualSystem element may contain one or more VirtualHardwareSection elements, each of
    * which describes the virtual hardware required by the virtual system.
    * */
   public Set<? extends VirtualHardwareSection> getHardware() {
      return hardware;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((hardware == null) ? 0 : hardware.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
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
      VirtualSystem other = (VirtualSystem) obj;
      if (hardware == null) {
         if (other.hardware != null)
            return false;
      } else if (!hardware.equals(other.hardware))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", name=" + getName() + ", info=" + getInfo() + ", os=" + getOperatingSystem()
               + ", hardware=" + getHardware() + "]";
   }
}