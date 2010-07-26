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
package org.jclouds.slicehost.domain;

import java.util.Date;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class Backup {

   private final int id;
   private final String name;
   private final String sliceId;
   private final Date date;

   public Backup(int id, String name, String sliceId, Date date) {
      this.id = id;
      this.name = name;
      this.sliceId = sliceId;
      this.date = date;
   }

   /**
    * @return id of the backup
    */
   public int getId() {
      return id;
   }

   /**
    * @return Examples: weekly, my snapshot
    */
   public String getName() {
      return name;
   }

   /**
    * @return The Slice this backup was made from
    */
   public String getSliceId() {
      return sliceId;
   }

   /**
    * @return The time the backup was taken
    */
   public Date getDate() {
      return date;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((date == null) ? 0 : date.hashCode());
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((sliceId == null) ? 0 : sliceId.hashCode());
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
      Backup other = (Backup) obj;
      if (date == null) {
         if (other.date != null)
            return false;
      } else if (!date.equals(other.date))
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (sliceId == null) {
         if (other.sliceId != null)
            return false;
      } else if (!sliceId.equals(other.sliceId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[date=" + date + ", id=" + id + ", name=" + name + ", sliceId=" + sliceId + "]";
   }

}
