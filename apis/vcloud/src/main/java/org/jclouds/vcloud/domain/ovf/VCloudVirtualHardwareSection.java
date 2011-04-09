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

import java.net.URI;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.ovf.VirtualHardwareSection;

/**
 * A description of the virtual hardware supported by a virtual machine.
 */
public class VCloudVirtualHardwareSection extends VirtualHardwareSection {
   protected final String type;
   protected final URI href;

   public VCloudVirtualHardwareSection(String type, URI href, String info, Iterable<String> transports,
            VirtualSystemSettingData virtualSystem,
            Iterable<? extends ResourceAllocationSettingData> resourceAllocations) {
      super(info, transports, virtualSystem, resourceAllocations);
      this.type = type;
      this.href = href;
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   @Override
   public int hashCode() {
      return href.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      VCloudVirtualHardwareSection other = (VCloudVirtualHardwareSection) obj;
      return href.equals(other.href);
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", type=" + getType() + ", info=" + getInfo() + ", virtualSystem=" + getSystem()
               + "]";
   }
}