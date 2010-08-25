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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;

/**
 * A description of the operating system supported by a virtual machine.
 */
public class OperatingSystem extends ReferenceTypeImpl {

   @Nullable
   private final Integer id;
   @Nullable
   private final String info;
   @Nullable
   private final String vmwOsType;
   @Nullable
   private final String description;
   private final ReferenceType edit;

   public OperatingSystem(@Nullable String name, String type, URI href, @Nullable Integer id, @Nullable String info,
            @Nullable String vmwOsType, @Nullable String description, ReferenceType edit) {
      super(name, type, href);
      this.id = id;
      this.info = info;
      this.vmwOsType = vmwOsType;
      this.description = description;
      this.edit = checkNotNull(edit, "edit");
   }

   /**
    * 
    * @return ovf id
    */
   public Integer getId() {
      return id;
   }

   /**
    * 
    * @return ovf info
    */
   public String getInfo() {
      return info;
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
    * @return description or null
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return edit link
    */
   public ReferenceType getEdit() {
      return edit;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((edit == null) ? 0 : edit.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
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
      OperatingSystem other = (OperatingSystem) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (edit == null) {
         if (other.edit != null)
            return false;
      } else if (!edit.equals(other.edit))
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
      if (vmwOsType == null) {
         if (other.vmwOsType != null)
            return false;
      } else if (!vmwOsType.equals(other.vmwOsType))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", name=" + getName() + ", type=" + getType() + ", id=" + getId() + ", vmwOsType="
               + getVmwOsType() + ", description=" + getDescription() + "]";
   }
}