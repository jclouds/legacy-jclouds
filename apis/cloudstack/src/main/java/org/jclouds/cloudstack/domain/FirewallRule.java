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
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * @author Andrei Savu
 */
public class FirewallRule implements Comparable<FirewallRule> {

   public static enum Protocol {
      TCP,
      UDP,
      ICMP,
      UNKNOWN;

      public static Protocol fromValue(String value) {
         try {
            return valueOf(value.toUpperCase());
         } catch(IllegalArgumentException e) {
            return UNKNOWN;
         }
      }

      @Override
      public String toString() {
         return name().toUpperCase();
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String CIDRs;

      private int startPort;
      private int endPort;

      private String icmpCode;
      private String icmpType;

      private String ipAddress;
      private long ipAddressId;

      private Protocol protocol;
      private String state;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public Builder CIDRs(String CIDRs) {
         this.CIDRs = CIDRs;
         return this;
      }

      public Builder startPort(int startPort) {
         this.startPort = startPort;
         return this;
      }

      public Builder endPort(int endPort) {
         this.endPort = endPort;
         return this;
      }

      public Builder icmpCode(String icmpCode) {
         this.icmpCode = icmpCode;
         return this;
      }

      public Builder icmpType(String icmpType) {
         this.icmpType = icmpType;
         return this;
      }

      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      public Builder ipAddressId(long ipAddressId) {
         this.ipAddressId = ipAddressId;
         return this;
      }

      public Builder protocol(Protocol protocol) {
         this.protocol = protocol;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public FirewallRule build() {
         return new FirewallRule(id, CIDRs, startPort, endPort, icmpCode,
            icmpType, ipAddress, ipAddressId, protocol, state);
      }
   }

   private long id;
   @SerializedName("cidrlist")
   private String CIDRs;
   @SerializedName("startport")
   private int startPort;
   @SerializedName("endport")
   private int endPort;
   @SerializedName("icmpcode")
   private String icmpCode;
   @SerializedName("icmptype")
   private String icmpType;
   @SerializedName("ipaddress")
   private String ipAddress;
   @SerializedName("ipaddressid")
   private long ipAddressId;
   private Protocol protocol;
   private String state;

   public FirewallRule(long id, String CIDRs, int startPort, int endPort,
         String icmpCode, String icmpType, String ipAddress, long ipAddressId,
         Protocol protocol, String state) {
      this.id = id;
      this.CIDRs = CIDRs;
      this.startPort = startPort;
      this.endPort = endPort;
      this.icmpCode = icmpCode;
      this.icmpType = icmpType;
      this.ipAddress = ipAddress;
      this.ipAddressId = ipAddressId;
      this.protocol = protocol;
      this.state = state;
   }

   @Override
   public int compareTo(FirewallRule arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   public long getId() {
      return id;
   }

   public String getCIDRs() {
      return CIDRs;
   }

   public int getStartPort() {
      return startPort;
   }

   public int getEndPort() {
      return endPort;
   }

   public String getIcmpCode() {
      return icmpCode;
   }

   public String getIcmpType() {
      return icmpType;
   }

   public String getIpAddress() {
      return ipAddress;
   }

   public long getIpAddressId() {
      return ipAddressId;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public String getState() {
      return state;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      FirewallRule that = (FirewallRule) o;

      if (endPort != that.endPort) return false;
      if (id != that.id) return false;
      if (startPort != that.startPort) return false;
      if (CIDRs != null ? !CIDRs.equals(that.CIDRs) : that.CIDRs != null)
         return false;
      if (icmpCode != null ? !icmpCode.equals(that.icmpCode) : that.icmpCode != null)
         return false;
      if (icmpType != null ? !icmpType.equals(that.icmpType) : that.icmpType != null)
         return false;
      if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null)
         return false;
      if (ipAddressId != that.ipAddressId)
         return false;
      if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null)
         return false;
      if (state != null ? !state.equals(that.state) : that.state != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (CIDRs != null ? CIDRs.hashCode() : 0);
      result = 31 * result + startPort;
      result = 31 * result + endPort;
      result = 31 * result + (icmpCode != null ? icmpCode.hashCode() : 0);
      result = 31 * result + (icmpType != null ? icmpType.hashCode() : 0);
      result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
      result = 31 * result + (int) (ipAddressId ^ (ipAddressId >>> 32));
      result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "FirewallRule{" +
         "id=" + id +
         ", CIDRs='" + CIDRs + '\'' +
         ", startPort=" + startPort +
         ", endPort=" + endPort +
         ", icmpCode='" + icmpCode + '\'' +
         ", icmpType='" + icmpType + '\'' +
         ", ipAddress='" + ipAddress + '\'' +
         ", ipAddressId='" + ipAddressId + '\'' +
         ", protocol='" + protocol + '\'' +
         ", state='" + state + '\'' +
         '}';
   }
}
