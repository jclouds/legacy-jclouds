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
package org.jclouds.vcloud.domain.ovf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.vcloud.domain.ReferenceType;

/**
 * A description of the operating system supported by a virtual machine.
 */
public class VCloudOperatingSystemSection extends OperatingSystemSection {
   protected final String type;
   protected final URI href;
   @Nullable
   protected final String vmwOsType;
   protected final ReferenceType edit;

   public VCloudOperatingSystemSection(@Nullable Integer id, @Nullable String info, @Nullable String description, String type,
            URI href, @Nullable String vmwOsType, ReferenceType edit) {
      super(id, info, description);
      this.type = type;
      this.href = href;
      this.vmwOsType = vmwOsType;
      this.edit = checkNotNull(edit, "edit");
   }

   public String getType() {
      return type;
   }

   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return VMware osType, if running on VMware
    */
   public String getVmwOsType() {
      return vmwOsType;
   }

   /**
    * 
    * @return edit link
    */
   public ReferenceType getEdit() {
      return edit;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", type=" + getType() + ", id=" + getId() + ", vmwOsType=" + getVmwOsType()
               + ", description=" + getDescription() + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((edit == null) ? 0 : edit.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((vmwOsType == null) ? 0 : vmwOsType.hashCode());
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
      VCloudOperatingSystemSection other = (VCloudOperatingSystemSection) obj;
      if (edit == null) {
         if (other.edit != null)
            return false;
      } else if (!edit.equals(other.edit))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (vmwOsType == null) {
         if (other.vmwOsType != null)
            return false;
      } else if (!vmwOsType.equals(other.vmwOsType))
         return false;
      return true;
   }
}