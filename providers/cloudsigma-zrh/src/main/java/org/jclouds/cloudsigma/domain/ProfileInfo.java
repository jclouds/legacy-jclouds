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

package org.jclouds.cloudsigma.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Adrian Cole
 */
public class ProfileInfo {

   public static class Builder {
      protected String uuid;
      protected String email;
      protected String firstName;
      protected String lastName;
      protected String nickName;
      protected ProfileType type = ProfileType.REGULAR;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder email(String email) {
         this.email = email;
         return this;
      }

      public Builder firstName(String firstName) {
         this.firstName = firstName;
         return this;
      }

      public Builder lastName(String lastName) {
         this.lastName = lastName;
         return this;
      }

      public Builder nickName(String nickName) {
         this.nickName = nickName;
         return this;
      }

      public Builder type(ProfileType type) {
         this.type = type;
         return this;
      }

      public ProfileInfo build() {
         return new ProfileInfo(uuid, email, firstName, lastName, nickName, type);
      }

   }

   protected final String uuid;
   protected final String email;
   protected final String firstName;
   protected final String lastName;
   protected final String nickName;
   protected final ProfileType type;

   public ProfileInfo(String uuid, String email, String firstName, String lastName, String nickName, ProfileType type) {
      this.uuid = checkNotNull(uuid, "uuid");
      this.email = checkNotNull(email, "email");
      this.firstName = checkNotNull(firstName, "firstName");
      this.lastName = checkNotNull(lastName, "lastName");
      this.nickName = checkNotNull(nickName, "nickName");
      this.type = checkNotNull(type, "type");
   }

   /**
    * 
    * @return uuid of the profile.
    */
   public String getUuid() {
      return uuid;
   }

   /**
    * Checks for valid email address
    * 
    * @return email of the profile.
    */
   public String getEmail() {
      return email;
   }

   /**
    * 
    * @return firstName of the profile.
    */
   protected String getFirstName() {
      return firstName;
   }

   /**
    * 
    * @return lastName of the profile.
    */
   protected String getLastName() {
      return lastName;
   }

   /**
    * Used in phpBB nick name
    * 
    * @return nickName of the profile.
    */
   protected String getNickName() {
      return nickName;
   }

   /**
    * 
    * @return type of the profile.
    */
   protected ProfileType getType() {
      return type;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((email == null) ? 0 : email.hashCode());
      result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
      result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
      result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      ProfileInfo other = (ProfileInfo) obj;
      if (email == null) {
         if (other.email != null)
            return false;
      } else if (!email.equals(other.email))
         return false;
      if (firstName == null) {
         if (other.firstName != null)
            return false;
      } else if (!firstName.equals(other.firstName))
         return false;
      if (lastName == null) {
         if (other.lastName != null)
            return false;
      } else if (!lastName.equals(other.lastName))
         return false;
      if (nickName == null) {
         if (other.nickName != null)
            return false;
      } else if (!nickName.equals(other.nickName))
         return false;
      if (type != other.type)
         return false;
      if (uuid == null) {
         if (other.uuid != null)
            return false;
      } else if (!uuid.equals(other.uuid))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
            + ", nickName=" + nickName + ", type=" + type + "]";
   }

}