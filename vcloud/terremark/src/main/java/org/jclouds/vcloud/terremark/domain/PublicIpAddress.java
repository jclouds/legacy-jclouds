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

package org.jclouds.vcloud.terremark.domain;

import java.net.URI;

/**
 * @author Adrian Cole
 */
public class PublicIpAddress implements Comparable<PublicIpAddress> {
   private final int id;
   private final String address;
   private final URI location;

   public PublicIpAddress(int id, String address, URI location) {
      this.id = id;
      this.address = address;
      this.location = location;
   }

   public int getId() {
      return id;
   }

   public URI getLocation() {
      return location;
   }

   public String getAddress() {
      return address;
   }

   public int compareTo(PublicIpAddress that) {
      final int BEFORE = -1;
      final int EQUAL = 0;
      final int AFTER = 1;

      if (this == that)
         return EQUAL;

      if (this.id < that.getId())
         return BEFORE;
      if (this.id > that.getId())
         return AFTER;
      return EQUAL;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + id;
      result = prime * result + ((location == null) ? 0 : location.hashCode());
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
      PublicIpAddress other = (PublicIpAddress) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (id != other.id)
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "PublicIpAddress [address=" + address + ", id=" + id + ", location=" + location + "]";
   }

}