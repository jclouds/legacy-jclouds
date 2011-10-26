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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Extends the SoftLayer_Software_Component data type to include operating system specific properties.
 * 
 * @author Jason King
 * @see <a href=
 *      "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Software_Component_OperatingSystem"
 *      />
 */
public class OperatingSystem implements Comparable<OperatingSystem> {

   // There are other properties

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private Set<Password> passwords = Sets.newLinkedHashSet();

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder password(Password password) {
         this.passwords.add(checkNotNull(password, "password"));
         return this;
      }

      public Builder passwords(Iterable<Password> passwords) {
         this.passwords = ImmutableSet.<Password> copyOf(checkNotNull(passwords, "passwords"));
         return this;
      }

      public OperatingSystem build() {
         return new OperatingSystem(id, passwords);
      }

      public static Builder fromOperatingSystem(OperatingSystem in) {
         return OperatingSystem.builder()
                               .id(in.getId())
                               .passwords(in.getPasswords());
      }
   }

   private int id = -1;
   private Set<Password> passwords = Sets.newLinkedHashSet();

   // for deserializer
   OperatingSystem() {

   }

   public OperatingSystem(int id,Iterable<Password> passwords) {
      this.id = id;
      this.passwords = ImmutableSet.<Password> copyOf(checkNotNull(passwords, "passwords"));
   }

   @Override
   public int compareTo(OperatingSystem arg0) {
      return new Integer(id).compareTo(arg0.getId());
   }

   /**
    * @return An ID number identifying this Software Component (Software Installation)
    */
   public int getId() {
      return id;
   }

   /**
    * 
    * @return Username/Password pairs used for access to this Software Installation.
    */
   public Set<Password> getPasswords() {
      return passwords;
   }

   public Builder toBuilder() {
      return Builder.fromOperatingSystem(this);
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
      OperatingSystem other = (OperatingSystem) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", passwords=" + passwords + "]";
   }
}
