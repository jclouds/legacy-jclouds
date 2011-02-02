/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not user this file except in compliance with the License.
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

package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class VLANInfo {
   public static class Builder {
      protected String uuid;
      protected String name;
      protected String user;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
         return this;
      }

      public VLANInfo build() {
         return new VLANInfo(uuid, name, user);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((user == null) ? 0 : user.hashCode());
         result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
         Builder other = (Builder) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         if (user == null) {
            if (other.user != null)
               return false;
         } else if (!user.equals(other.user))
            return false;
         if (uuid == null) {
            if (other.uuid != null)
               return false;
         } else if (!uuid.equals(other.uuid))
            return false;
         return true;
      }
   }

   @Nullable
   protected final String uuid;
   protected final String name;
   protected final String user;

   public VLANInfo(String uuid, String name, String user) {
      this.uuid = checkNotNull(uuid, "uuid");
      this.name = checkNotNull(name, "name");
      this.user = checkNotNull(user, "user");
   }

   /**
    * 
    * @return uuid of the vlan.
    */
   @Nullable
   public String getUuid() {
      return uuid;
   }

   /**
    * 
    * @return name of the vlan
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return user owning the vlan
    */
   public String getUser() {
      return user;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
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
      VLANInfo other = (VLANInfo) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;

      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", user=" + user + "]";
   }

}