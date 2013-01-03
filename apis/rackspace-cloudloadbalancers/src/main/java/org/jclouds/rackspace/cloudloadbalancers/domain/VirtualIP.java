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
package org.jclouds.rackspace.cloudloadbalancers.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A virtual IP (VIP) makes a load balancer accessible by clients. The load balancing service
 * supports either a public VIP, routable on the public Internet, or a ServiceNet address, routable
 * only within the region in which the load balancer resides.
 * 
 * @author Adrian Cole
 */
public class VirtualIP implements Comparable<VirtualIP> {

   private int id;
   private String address;
   private Type type;
   private IPVersion ipVersion;

   // for serialization only
   VirtualIP() {
   }

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

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("address", address).add("ipVersion", ipVersion).add("type", type);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      VirtualIP that = VirtualIP.class.cast(obj);
      return Objects.equal(this.id, that.id);
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
   
   public static Builder builder() {
      return new Builder();
   }
}
