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
package org.jclouds.ibm.smartcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class IP {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String ip;
      private String hostname;
      private int type;

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder type(int type) {
         this.type = type;
         return this;
      }

      public IP build() {
         return new IP(ip, hostname, type);
      }
   }

   private String ip;
   private String hostname;
   @Beta
   private int type;

   IP() {

   }

   public IP(String ip, String hostname, int type) {
      this.ip = checkNotNull(ip, "ip");
      this.hostname = checkNotNull(hostname, "hostname");
      this.type = type;
   }

   // TODO custom parser to do this once
   public String getHostname() {
      return "".equals(hostname.trim()) ? null : hostname.trim();
   }

   public String getIP() {
      return "".equals(ip.trim()) ? null : ip.trim();
   }

   public int getType() {
      return type;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + type;
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
      IP other = (IP) obj;
      if (hostname == null) {
         if (other.hostname != null)
            return false;
      } else if (!hostname.equals(other.hostname))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (type != other.type)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[hostname=%s, ip=%s, type=%s]", hostname, ip, type);
   }

}
