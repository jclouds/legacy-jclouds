/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.cloudloadbalancers.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A virtual IP (VIP) makes a load balancer accessible by clients. The load balancing service
 * supports either a public VIP, routable on the public Internet, or a ServiceNet address, routable
 * only within the region in which the load balancer resides.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s03s01.html"
 *      />
 */
public class VirtualIP implements Comparable<VirtualIP> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int id = -1;
      private String address;
      private Type type;
      private IPVersion ipVersion = IPVersion.IPV4;

      public Builder id(int id) {
         this.id = id;
         return this;
      }

      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder ipVersion(IPVersion ipVersion) {
         this.ipVersion = ipVersion;
         return this;
      }

      public VirtualIP build() {
         return new VirtualIP(id, address, type, ipVersion);
      }
   }

   /**
    * Virtual IP Types
    */
   public static enum Type {
      /**
       * An address that is routable on the public Internet.
       */
      PUBLIC,
      /**
       * An address that is routable only on ServiceNet.
       */
      SERVICENET, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   /**
    * Virtual IP Versions
    */
   public static enum IPVersion {

      IPV4, IPV6, UNRECOGNIZED;

      public static IPVersion fromValue(String ipVersion) {
         try {
            return valueOf(checkNotNull(ipVersion, "ipVersion"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   // for serialization only
   VirtualIP() {

   }

   private int id;
   private String address;
   private Type type;
   private IPVersion ipVersion;

   public VirtualIP(int id, String address, Type type, IPVersion ipVersion) {
      checkArgument(id != -1, "id must be specified");
      this.id = id;
      this.address = checkNotNull(address, "address");
      this.type = checkNotNull(type, "type");
      this.ipVersion = checkNotNull(ipVersion, "ipVersion");
   }

   public int getId() {
      return id;
   }

   public String getAddress() {
      return address;
   }

   public Type getType() {
      return type;
   }

   public IPVersion getIpVersion() {
      return ipVersion;
   }

   @Override
   public int compareTo(VirtualIP arg0) {
      return address.compareTo(arg0.address);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
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
      VirtualIP other = (VirtualIP) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[address=%s, id=%s, ipVersion=%s, type=%s]", address, id, ipVersion, type);
   }

}
