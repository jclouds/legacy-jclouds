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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

/**
 *
 * Contains a password for a specific software component instance
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Software_Component_Password"
 *      />
 */
public class Password implements Comparable<Password> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private String username;
      private String password;

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder username(String username) {
         this.username = username;
         return this;
      }

      public Builder password(String password) {
         this.password = password;
         return this;
      }

      public Password build() {
         return new Password(id, username, password);
      }

      public static Builder fromPassword(Password in) {
         return Password.builder().id(in.getId())
                                 .username(in.getUsername())
                                 .password(in.getPassword());
      }
   }

   private int id = -1;
   private String username;
   private String password;

   // for deserializer
   Password() {

   }

   public Password(int id, String username, String password) {
      this.id = id;
      this.username = checkNotNull(emptyToNull(username),"username cannot be null or empty:"+username);
      this.password = password;
   }

    @Override
   public int compareTo(Password arg0) {
      return new Integer(id).compareTo(arg0.getId());
   }

   /**
    * @return An id number for this specific username/password pair.
    */
   public int getId() {
      return id;
   }

   /**
    * @return The username part of the username/password pair.
    */
   public String getUsername() {
      return username;
   }

   /**
    * @return The password part of the username/password pair.
    */
   public String getPassword() {
      return password;
   }

   public Builder toBuilder() {
      return Builder.fromPassword(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (id ^ (id >>> 32));
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
      Password other = (Password) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Password [id=" + id + ", username=" + username + ", password=**********]";
   }
   
   
}
