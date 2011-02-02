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

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class StaticIPInfo {
   public static class Builder {
      protected String ip;
      protected String user;
      protected String netmask;
      protected Set<String> nameservers = ImmutableSet.of();
      protected String gateway;

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder user(String user) {
         this.user = user;
         return this;
      }

      public Builder nameservers(Iterable<String> nameservers) {
         this.nameservers = ImmutableSet.copyOf(checkNotNull(nameservers, "nameservers"));
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public StaticIPInfo build() {
         return new StaticIPInfo(ip, user, netmask, nameservers, gateway);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
         result = prime * result + ((nameservers == null) ? 0 : nameservers.hashCode());
         result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
         result = prime * result + ((user == null) ? 0 : user.hashCode());
         result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
         if (gateway == null) {
            if (other.gateway != null)
               return false;
         } else if (!gateway.equals(other.gateway))
            return false;
         if (nameservers == null) {
            if (other.nameservers != null)
               return false;
         } else if (!nameservers.equals(other.nameservers))
            return false;
         if (netmask == null) {
            if (other.netmask != null)
               return false;
         } else if (!netmask.equals(other.netmask))
            return false;
         if (user == null) {
            if (other.user != null)
               return false;
         } else if (!user.equals(other.user))
            return false;
         if (ip == null) {
            if (other.ip != null)
               return false;
         } else if (!ip.equals(other.ip))
            return false;
         return true;
      }
   }

   protected final String ip;
   protected final String user;
   protected final String netmask;
   protected final Set<String> nameservers;
   protected final String gateway;

   public StaticIPInfo(String ip, String user, String netmask, Iterable<String> nameservers, String gateway) {
      this.ip = checkNotNull(ip, "ip");
      this.user = checkNotNull(user, "user");
      this.netmask = checkNotNull(netmask, "netmask");
      this.nameservers = ImmutableSet.copyOf(checkNotNull(nameservers, "nameservers"));
      this.gateway = checkNotNull(gateway, "gateway");
   }

   /**
    * 
    * @return ip of the ip.
    */
   @Nullable
   public String getAddress() {
      return ip;
   }

   /**
    * 
    * @return user owning the ip
    */
   public String getUser() {
      return user;
   }

   /**
    * 
    * @return netmask of the ip
    */
   public String getNetmask() {
      return netmask;
   }

   /**
    * 
    * @return nameservers of the ip
    */
   public Set<String> getNameservers() {
      return nameservers;
   }

   /**
    * 
    * @return gateway of the ip
    */
   public String getGateway() {
      return gateway;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
      result = prime * result + ((nameservers == null) ? 0 : nameservers.hashCode());
      result = prime * result + ((netmask == null) ? 0 : netmask.hashCode());
      result = prime * result + ((user == null) ? 0 : user.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
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
      StaticIPInfo other = (StaticIPInfo) obj;
      if (gateway == null) {
         if (other.gateway != null)
            return false;
      } else if (!gateway.equals(other.gateway))
         return false;
      if (nameservers == null) {
         if (other.nameservers != null)
            return false;
      } else if (!nameservers.equals(other.nameservers))
         return false;
      if (netmask == null) {
         if (other.netmask != null)
            return false;
      } else if (!netmask.equals(other.netmask))
         return false;
      if (user == null) {
         if (other.user != null)
            return false;
      } else if (!user.equals(other.user))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[ip=" + ip + ", user=" + user + ", netmask=" + netmask + ", nameservers="
            + nameservers + ", gateway=" + gateway + "]";
   }

}