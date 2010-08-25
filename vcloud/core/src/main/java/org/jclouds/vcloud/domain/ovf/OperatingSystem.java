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

import javax.annotation.Nullable;

public class OperatingSystem {

   @Nullable
   protected final Integer id;
   @Nullable
   protected final String info;

   @Nullable
   protected final String description;

   public OperatingSystem(@Nullable Integer id, @Nullable String info, @Nullable String description) {
      this.id = id;
      this.info = info;
      this.description = description;
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
    * @return description or null
    */
   public String getDescription() {
      return description;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
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
      OperatingSystem other = (OperatingSystem) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
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
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + getId() + ", info=" + getInfo() + ", description=" + getDescription() + "]";
   }

}