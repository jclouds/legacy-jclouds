/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudservers.domain;

import java.net.InetAddress;
import java.util.Comparator;
import java.util.SortedSet;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

public class Addresses {
   private static final Comparator<InetAddress> ADDRESS_COMPARATOR = new Comparator<InetAddress>() {

      @Override
      public int compare(InetAddress o1, InetAddress o2) {
         return (o1 == o2) ? 0 : o1.getHostAddress().compareTo(o2.getHostAddress());
      }

   };

   @SerializedName("public")
   private SortedSet<InetAddress> publicAddresses = Sets.newTreeSet(ADDRESS_COMPARATOR);
   @SerializedName("private")
   private SortedSet<InetAddress> privateAddresses = Sets.newTreeSet(ADDRESS_COMPARATOR);

   public Addresses() {
   }

   public Addresses(SortedSet<InetAddress> publicAddresses, SortedSet<InetAddress> privateAddresses) {
      this.publicAddresses = publicAddresses;
      this.privateAddresses = privateAddresses;
   }

   public void setPublicAddresses(SortedSet<InetAddress> publicAddresses) {
      this.publicAddresses = publicAddresses;
   }

   public SortedSet<InetAddress> getPublicAddresses() {
      return publicAddresses;
   }

   public void setPrivateAddresses(SortedSet<InetAddress> privateAddresses) {
      this.privateAddresses = privateAddresses;
   }

   public SortedSet<InetAddress> getPrivateAddresses() {
      return privateAddresses;
   }

   @Override
   public String toString() {
      return "Addresses [privateAddresses=" + privateAddresses + ", publicAddresses="
               + publicAddresses + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
      result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
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
      Addresses other = (Addresses) obj;
      if (privateAddresses == null) {
         if (other.privateAddresses != null)
            return false;
      } else if (!privateAddresses.equals(other.privateAddresses))
         return false;
      if (publicAddresses == null) {
         if (other.publicAddresses != null)
            return false;
      } else if (!publicAddresses.equals(other.publicAddresses))
         return false;
      return true;
   }

}
