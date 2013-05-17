/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A virtual IP (VIP) makes a load balancer accessible by clients. The load balancing service
 * supports either a public virtual IP, routable on the public Internet, or a ServiceNet address, routable
 * only within the region in which the load balancer resides.
 * 
 * @author Everett Toews
 */
public class VirtualIP {

   private final Type type;
   private final IPVersion ipVersion;

   /**
    * Use this method to easily create virtual IPs. Only public IPv6 virtual IPs can be created.
    */
   public static VirtualIP publicIPv6() {
      return new VirtualIP(Type.PUBLIC, IPVersion.IPV6);
   }
   
   protected VirtualIP(Type type, IPVersion ipVersion) {
      this.type = checkNotNull(type, "type");
      this.ipVersion = checkNotNull(ipVersion, "ipVersion");
   }

   public Type getType() {
      return type;
   }

   public IPVersion getIpVersion() {
      return ipVersion;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("ipVersion", ipVersion).add("type", type);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ipVersion, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      VirtualIP that = VirtualIP.class.cast(obj);
      return Objects.equal(this.ipVersion, that.ipVersion)
            && Objects.equal(this.type, that.type);
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
       * An address that is routable only on the Rackspace ServiceNet.
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
}
